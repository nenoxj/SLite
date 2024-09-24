package cn.note.swing.slite.view.litenote;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.StrUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.swing.core.util.FrameUtil;
import cn.note.swing.core.util.SwingCoreUtil;
import cn.note.swing.core.view.TextConstants;
import cn.note.swing.core.view.modal.swingx.JModalWindow;
import cn.note.swing.core.view.panel.JERoundPanel;
import cn.note.swing.core.view.rsta.JECodeReader;
import cn.note.swing.core.view.rsta.RstaLanguage;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.core.view.theme.ThemeUI;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.core.SettingManager;
import cn.note.swing.slite.core.bean.Config;
import cn.note.swing.slite.view.litenote.state.LiteNoteDeleteState;
import cn.note.swing.slite.view.litenote.state.LiteNoteEditState;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class LiteNoteItemPanel extends JERoundPanel {

    private JPanel itemPanel;

    private JECodeReader codeReader;

    private LiteNote liteNote;

    private JButton copyButton;

    private JButton editButton;

    private JButton deleteButton;
    private JButton moreButton;
    private JPopupMenu copyPopupMenu;

    private JPopupMenu deletePopupMenu;

    private JMenuItem confirmDeleteMenu;

    private BundleManager bundleManager;

    private JModalWindow moreWindow;

    LiteNoteItemPanel(LiteNote liteNote) {
        super(DefaultUIConstants.blackColor, 8);
        this.bundleManager = ApplicationManager.getInstance().getSliteBundle();
        this.liteNote = liteNote;
        super.setLayout(new GridLayout(1, 1));
        init();
        render();
        bindEvents();
    }

    private void init() {
        codeReader = new JECodeReader(liteNote.getContent(), RstaLanguage.text);
        itemPanel = new JPanel(new MigLayout("insets 2 5 0 5,gap 0", ""));
        copyButton = iconButton(bundleManager.getIcon("LiteNote.list.item.copyCode.icon"), bundleManager.getString("LiteNote.list.item.copyCode.tip"));
        editButton = iconButton(bundleManager.getIcon("LiteNote.list.item.edit.icon"), bundleManager.getString("LiteNote.list.item.edit.tip"));
        deleteButton = iconButton(bundleManager.getIcon("LiteNote.list.item.delete.icon"), bundleManager.getString("LiteNote.list.item.delete.tip"));

        //二次删除确认
        deletePopupMenu = new JPopupMenu();
        deletePopupMenu.setBorderPainted(false);
        confirmDeleteMenu = new JMenuItem(bundleManager.getString("LiteNote.list.item.delete.again-tip"));
        deletePopupMenu.add(confirmDeleteMenu);
        deleteButton.setComponentPopupMenu(deletePopupMenu);

        //复制提醒
        copyPopupMenu = new JPopupMenu();
        copyPopupMenu.add(new JMenuItem(bundleManager.getString("LiteNote.list.item.copyCode.again-tip")));
        copyPopupMenu.setBorderPainted(false);
        copyButton.setComponentPopupMenu(copyPopupMenu);

        // 滚动条
        ThemeUI themeUI = ThemeFlatLaf.reverseTheme;
        RTextScrollPane textScrollPane = new RTextScrollPane(codeReader);
        textScrollPane.setBackground(codeReader.getBackground());
        textScrollPane.setBorder(BorderFactory.createEmptyBorder());
        textScrollPane.setLineNumbersEnabled(false);
        textScrollPane.setVerticalScrollBarPolicy(RTextScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        themeUI.setJScrollPaneUI(textScrollPane);

        // 更多
        moreButton = iconButton(bundleManager.getIcon("LiteNote.list.item.more.icon"), bundleManager.getString("LiteNote.list.item.more.tip"));
        moreWindow = new JModalWindow(SwingUtilities.getWindowAncestor(moreButton));
        JPanel morePanel = new JPanel(new MigLayout("insets 0,gap 0", "grow", "grow"));
        morePanel.setBackground(codeReader.getBackground());
        morePanel.add(deleteButton, "gapleft 5");
        morePanel.add(editButton, "gapleft 5");
        int scale = Toolkit.getDefaultToolkit().getScreenResolution();
        moreWindow.setSize(morePanel.getComponentCount() * 30 * scale / 96, 25 * scale / 96);
        moreWindow.setContentPane(morePanel);
        moreButton.addActionListener(e -> {
            Point p = moreButton.getLocationOnScreen();
            moreWindow.setLocation(p.x - moreWindow.getWidth(), p.y - 3);
            moreWindow.setVisible(!moreWindow.isVisible());
        });


        attachHandCursor(editButton, deleteButton, copyButton, moreButton);
        itemPanel.setBackground(codeReader.getBackground());
        itemPanel.add(textScrollPane, "span 1 2,h 50::100,w 100%");
        itemPanel.add(copyButton, "wrap");
        itemPanel.add(moreButton, "aligny 100%");
    }

    private void render() {
        super.add(itemPanel, "grow");
    }


    private JButton iconButton(Icon icon, String tip) {
        JButton button = new JButton();
        button.setBorderPainted(false);
        button.setIcon(icon);
        button.setToolTipText(tip);
        button.setBackground(codeReader.getBackground());
        return button;
    }


    private void bindEvents() {
        Action copyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = liteNote.getContent();
                if (StrUtil.isNotBlank(content)) {
                    Config config = SettingManager.getInstance().getConfig();
                    // 仅仅复制代码
                    if (config.isOnlyCopyCode()) {
                        List<String> lines = new ArrayList<>();
                        for (String line : Splitter.on(TextConstants.SEPARATOR).split(content)) {
                            if (StrUtil.isNotBlank(line) && !line.trim().startsWith(config.getCommentPrefix())) {
                                lines.add(line);
                            }
                        }
                        content = Joiner.on(TextConstants.SEPARATOR).join(lines);
                    }
                    ClipboardUtil.setStr(content);
                    Rectangle rect = copyButton.getVisibleRect();
                    copyPopupMenu.show(copyButton, rect.x, rect.y + rect.height);
                    SwingCoreUtil.onceTimer(1000, () -> copyPopupMenu.setVisible(false));
                }
            }
        };
        // 复制
        copyButton.addActionListener(copyAction);
        super.getActionMap().put("CopyCode", copyAction);

        //删除
        deleteButton.addActionListener(e -> {
            Rectangle rect = deleteButton.getVisibleRect();
            deletePopupMenu.show(deleteButton, rect.x, rect.y + rect.height);
        });

        // 确认删除
        confirmDeleteMenu.addActionListener(e -> {
            LiteNoteDeleteState deleteState = new LiteNoteDeleteState();
            deleteState.setDeleteComponent(LiteNoteItemPanel.this);
            deleteState.setDeletePopMenu(deletePopupMenu);
            deleteState.setId(liteNote.getId());
            deletePopupMenu.setVisible(false);
            LiteNoteStateManager.postDeleteState(deleteState);
        });

        // 编辑
        editButton.addActionListener(e -> {
            LiteNoteEditState editState = new LiteNoteEditState();
            editState.setLiteNote(liteNote);
            LiteNoteStateManager.postEditState(editState);
        });

        // 监听位置变化,隐藏二级窗口
        moreButton.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                moreWindow.setVisible(false);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                moreWindow.setVisible(false);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                moreWindow.setVisible(false);
            }
        });

    }

    private void attachHandCursor(JComponent... components) {
        for (JComponent component : components) {
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

    }

    public static void main(String[] args) {
        ThemeFlatLaf.install();
        LiteNote liteNote = new LiteNote();
        liteNote.setContent("#ffmpeg操作 \n ffmpeg -ss -tt");
        LiteNoteItemPanel itemPanel = new LiteNoteItemPanel(liteNote);
        FrameUtil.launchTest(itemPanel);
    }

}