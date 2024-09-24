package cn.note.swing.slite.view.litenote;

import cn.note.swing.core.component.ComponentMover;
import cn.note.swing.core.key.KeyActionFactory;
import cn.note.swing.core.listener.ChangeDocumentListener;
import cn.note.swing.core.state.SwingStateManager;
import cn.note.swing.core.util.ButtonFactory;
import cn.note.swing.core.util.LocationUtil;
import cn.note.swing.core.util.SwingCoreUtil;
import cn.note.swing.core.view.icon.SvgIconFactory;
import cn.note.swing.core.view.wrapper.TextFieldWrapper;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.view.litenote.state.LiteNoteCloseState;
import cn.note.swing.slite.view.litenote.state.LiteNoteDragState;
import cn.note.swing.slite.view.litenote.state.LiteNoteEditState;
import cn.note.swing.slite.view.litenote.state.LiteNoteSearchState;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * 搜索面板
 *
 * @author jee
 * @version 1.0
 */
class LiteNoteSearchDialog extends LiteNoteModalDialog {

    private final SearchTextPanel searchTextPanel;

    private final BundleManager bundleManager;

    public LiteNoteSearchDialog() {
        super(DefaultUIConstants.getSearchHeight());
        bundleManager = ApplicationManager.getInstance().getSliteBundle();
        searchTextPanel = new SearchTextPanel();
        this.setContentPane(searchTextPanel);
        init();
        Rectangle rectangle = LocationUtil.offsetScreenRectangle(DefaultUIConstants.getSearchOffsetY(), DefaultUIConstants.getDefaultWidth(), DefaultUIConstants.getSearchHeight());
        this.setBounds(rectangle);
        SwingStateManager.registerListener(this);
    }


    @Subscribe
    void listenerSearchSate(LiteNoteSearchState liteNoteSearchState) {
        if (!this.isVisible()) {
            this.setVisible(true);
        }
        this.toFront();
    }

    private void init() {
        // 监听ESC隐藏
        KeyActionFactory.bindEscAction(searchTextPanel, e -> {
            LiteNoteStateManager.postCloseState(new LiteNoteCloseState());
            this.dispose();
        });
        // 可拖拽
        ComponentMover cm = new ComponentMover() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Rectangle rectangle = LiteNoteSearchDialog.this.getBounds();
                LiteNoteDragState dragState = new LiteNoteDragState(rectangle);
                LiteNoteStateManager.postDragState(dragState);

            }
        };
        cm.registerComponent(this);

        // 焦点事件
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                searchTextPanel.fireSearchState();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
//                Console.log("窗口失去焦点.....");
            }
        });
    }

    /**
     * 搜索框
     */
    private class SearchTextPanel extends JPanel {

        private JTextField searchText;

        private JButton newNote;

        SearchTextPanel() {
            this.setLayout(new MigLayout("insets 0 10 0 10,gap 0", "grow", "grow"));
            init();
            render();
            bindEvents();
        }

        protected void init() {
            this.setBorder(BorderFactory.createLineBorder(DefaultUIConstants.grayColor, 1));
//        addNote = ButtonFactory.primaryBorderButton(null,SvgIconFactory.icon(SvgIconFactory.Common.add));
            newNote = ButtonFactory.primaryBorderButton(bundleManager.getString("LiteNote.search.new"));
            newNote.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            searchText = createSearchText();
        }

        protected void render() {
            add(searchText, "growx,center");
        }

        private JTextField createSearchText() {
            String searchStyle = "arc:0;focusWidth:0;borderWidth:0;innerOutlineWidth:0;innerFocusWidth:0;" +
                    "background:{};foreground:{};";
            JTextField textField = new JTextField();
            SwingCoreUtil.offsetFontSize(textField, 2f);
            return TextFieldWrapper.create(textField)
                    .prefixIcon(SvgIconFactory.icon(SvgIconFactory.Common.search))
                    .style(searchStyle, DefaultUIConstants.toHEXColor(super.getBackground()), DefaultUIConstants.toHEXColor(DefaultUIConstants.themeColor))
                    .caretColor(DefaultUIConstants.themeColor)
                    .placeholder(bundleManager.getString("LiteNote.search.placeholder"))
                    .showClear(() -> {
                        searchText.setText("");
                    })
                    .suffix(newNote)
                    .build();
        }

        /**
         * 通知搜索状态, 内容为空时拒绝
         */
        private void fireSearchState() {
            String searchContent = searchText.getText();
            LiteNoteSearchState liteNoteSearchState = new LiteNoteSearchState();
            liteNoteSearchState.setSearchContent(searchContent);
            LiteNoteStateManager.postSearchState(liteNoteSearchState);
        }

        /**
         * 修复不能获取事件问题
         * 手动创建焦点事件
         *
         * @see https://blog.csdn.net/MrKorbin/article/details/88320830
         * rebuild by jee
         */
        @Deprecated
        private void fireFocusEvent() {
//        searchText.dispatchEvent(new FocusEvent(searchText, FocusEvent.FOCUS_GAINED, false));
            KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(new FocusEvent(searchText, FocusEvent.FOCUS_GAINED));
            searchText.requestFocusInWindow();
        }

        private void bindEvents() {
            // 新增事件
            newNote.addActionListener(e -> {
                LiteNoteEditState editState = new LiteNoteEditState();
                LiteNoteStateManager.postEditState(editState);
            });

            // 搜索
            searchText.getDocument().addDocumentListener(new ChangeDocumentListener() {
                @Override
                public void update(DocumentEvent e) {
                    fireSearchState();
                }
            });
        }
    }

}
