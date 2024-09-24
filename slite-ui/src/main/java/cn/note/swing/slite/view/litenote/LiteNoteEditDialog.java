package cn.note.swing.slite.view.litenote;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.litenote.service.LiteNoteService;
import cn.note.swing.core.key.KeyActionFactory;
import cn.note.swing.core.key.KeyActionStatus;
import cn.note.swing.core.listener.ChangeDocumentListener;
import cn.note.swing.core.state.SwingStateManager;
import cn.note.swing.core.util.*;
import cn.note.swing.core.view.Direction;
import cn.note.swing.core.view.rsta.JECodeEditor;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.view.litenote.state.LiteNoteCloseState;
import cn.note.swing.slite.view.litenote.state.LiteNoteDragState;
import cn.note.swing.slite.view.litenote.state.LiteNoteEditState;
import cn.note.swing.slite.view.litenote.state.LiteNoteSearchState;
import com.formdev.flatlaf.FlatClientProperties;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

/**
 * 编辑面板
 *
 * @author jee
 * @version 1.0
 */
@SuppressWarnings("UnstableApiUsage")
class LiteNoteEditDialog extends LiteNoteModalDialog {
    private LiteNote liteNote;

    /* 缓存*/
    private LiteNote cacheLiteNote;

    /* 文本内容*/
    private JECodeEditor contentEditor;
    /*分组*/
    private JTextField groups;
    /*保存*/
    private JButton save;

    private JPanel mainPanel;

    private BundleManager bundleManager;

    private LiteNoteService liteNoteService;

    private LiteNoteView liteNoteView;

    LiteNoteEditDialog(LiteNoteView liteNoteView, LiteNoteService liteNoteService) {
        super(DefaultUIConstants.getDefaultHeight());
        this.liteNoteView = liteNoteView;
        this.liteNoteService = liteNoteService;
        bundleManager = ApplicationManager.getInstance().getSliteBundle();
        mainPanel = new MainPanel();
        cacheLiteNote = new LiteNote(IdUtil.fastSimpleUUID());
        super.setContentPane(mainPanel);
        bindEvents();
        super.setVisible(true);
        SwingStateManager.registerListener(this);
    }

    /**
     * 监听编辑状态
     */
    @Subscribe
    void listenerEditState(LiteNoteEditState liteNoteEditState) {

        LiteNote editLiteNote = liteNoteEditState.getLiteNote();
        boolean echo = false;
        // 编辑内容强制覆盖
        if (editLiteNote != null) {
            this.liteNote = editLiteNote;
            mainPanel.putClientProperty("optStatus", "edit");
            echo = true;
        } else {
            mainPanel.putClientProperty("optStatus", "add");
            if (this.cacheLiteNote == null) {
                this.cacheLiteNote = new LiteNote(IdUtil.fastSimpleUUID());
            }
            this.liteNote = cacheLiteNote;
        }
        this.contentEditor.setEditorText(liteNote.getContent());
        this.groups.setText(liteNote.getGroups());
        if (liteNoteView.isSearchVisible()) {
            mainPanel.setBorder(BorderUtil.hideLineBorder(Direction.TOP, 1, DefaultUIConstants.grayColor));
        } else {
            mainPanel.setBorder(BorderFactory.createLineBorder(DefaultUIConstants.blueColor));
        }

        Rectangle rectangle = liteNoteView.getSearchLocation();
        updateLocation(rectangle);
        if (!super.isVisible()) {
            super.setVisible(true);
        } else {
            super.toFront();
        }

    }

    @Subscribe
    void listenerSearchSate(LiteNoteSearchState liteNoteSearchState) {
        this.setVisible(false);
    }

    @Subscribe
    void listenerCloseState(LiteNoteCloseState liteNoteCloseState) {
        this.setVisible(false);
    }

    @Subscribe
    void listenerDragState(LiteNoteDragState liteNoteDragState) {
        if (super.isVisible()) {
            Rectangle rectangle = liteNoteView.getSearchLocation();
            updateLocation(rectangle);
        }
    }

    private void bindEvents() {
        ActionListener saveAction = e -> {
            String text = contentEditor.getEditorText();
            if (StrUtil.isBlank(text)) {
                MessageUtil.error(save, bundleManager.getString("LiteNote.edit.valid.message-blank-error"));
                return;
            }
            try {
                liteNoteService.saveOrUpdate(liteNote);
                if (Objects.equals(mainPanel.getClientProperty("optStatus"), "edit")) {
                    this.liteNote = null;
                    this.cacheLiteNote = null;
                    this.setVisible(false);
                } else {
                    MessageUtil.ok(save, bundleManager.getString("LiteNote.edit.success-tip"));
                    this.cacheLiteNote = new LiteNote(IdUtil.fastSimpleUUID());
                    this.liteNote = cacheLiteNote;
                    this.groups.setText(null);
                    this.contentEditor.setEditorText(null);
                    this.contentEditor.requestFocusInWindow();
                }

            } catch (IOException ex) {
                MessageUtil.error(save, bundleManager.getError("LiteNote.edit.valid.save-error", ex));
            }
        };

        save.addActionListener(saveAction);
        KeyActionFactory.bindKeyAction(save, "EDIT_SAVE_ACTION", DefaultUIConstants.EDIT_SAVE_ACTION, saveAction, KeyActionStatus.WHEN_IN_FOCUSED_WINDOW);
        save.setToolTipText(SwingCoreUtil.keyStroke2Str(DefaultUIConstants.EDIT_SAVE_ACTION));

        // 记录编辑内容
        contentEditor.getTextArea().getDocument().addDocumentListener((ChangeDocumentListener) e -> {
            liteNote.setContent(contentEditor.getEditorText());
            if (Objects.equals(mainPanel.getClientProperty("optStatus"), "add")) {
                cacheLiteNote.setContent(contentEditor.getEditorText());
            }
        });

        // 记录关键词
        groups.getDocument().addDocumentListener((ChangeDocumentListener) e -> {
            liteNote.setGroups(groups.getText());
            if (Objects.equals(mainPanel.getClientProperty("optStatus"), "add")) {
                cacheLiteNote.setGroups(groups.getText());
            }
        });

        // 监听ESC隐藏
        KeyActionFactory.bindEscAction(save, e -> this.setVisible(false));

    }

    private class MainPanel extends JPanel {

        private JPanel container;

        MainPanel() {
            super.setLayout(new GridLayout(1, 1));
            init();
            render();
        }

        private void init() {
            contentEditor = new JECodeEditor();
            contentEditor.setPromptText(bundleManager.getString("LiteNote.edit.content.placeholder"));

            RTextScrollPane scrollPane = contentEditor.getScrollPane();
            scrollPane.setLineNumbersEnabled(false);
            scrollPane.setBorder(BorderUtil.lineBorder(Direction.BOTTOM, 1, DefaultUIConstants.blueColor));
            groups = new JTextField();
            groups.setBorder(null);
            groups.putClientProperty(FlatClientProperties.TEXT_FIELD_PADDING, new Insets(0, 8, 0, 8));
            groups.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, bundleManager.getString("LiteNote.edit.group.placeholder"));
            save = ButtonFactory.primaryButton(bundleManager.getString("LiteNote.edit.save.text"));

            container = new JPanel();
            container.setLayout(new MigLayout("gap 0", "grow", "[grow][]"));
            container.setBackground(groups.getBackground());
        }

        private void render() {

            container.add(contentEditor, "grow,cell 0 0");
            container.add(groups, "h 35,growx,cell 0 1");
            container.add(save, "cell 0 1");
            super.add(container);
        }
    }

    public static void main(String[] args) {
        ThemeFlatLaf.install();
        FrameUtil.launchTest(LiteNoteEditDialog.MainPanel.class);
    }

}
