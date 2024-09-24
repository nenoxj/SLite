package cn.note.swing.slite.view.litenote;

import cn.hutool.core.util.StrUtil;
import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.entity.Page;
import cn.note.slite.litenote.service.LiteNoteService;
import cn.note.swing.core.key.KeyActionFactory;
import cn.note.swing.core.listener.GlobalKeyListener;
import cn.note.swing.core.state.SwingStateManager;
import cn.note.swing.core.util.BorderUtil;
import cn.note.swing.core.util.FrameUtil;
import cn.note.swing.core.util.MessageUtil;
import cn.note.swing.core.view.Direction;
import cn.note.swing.core.view.page.JEMorePagination;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.core.view.theme.ThemeUI;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.view.litenote.state.*;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * 列表面板
 *
 * @author jee
 * @version 1.0
 */
@SuppressWarnings("UnstableApiUsage")
class LiteNoteListWindow extends LiteNoteModalWindow {

    private String searchContent;

    private SearchListPanel searchListPanel;

    private Timer timer;

    private BundleManager bundleManager;

    private LiteNoteService liteNoteService;

    private LiteNoteView liteNoteView;

    public LiteNoteListWindow(LiteNoteView liteNoteView, LiteNoteService liteNoteService) {
        super(DefaultUIConstants.getDefaultHeight());
        this.liteNoteView = liteNoteView;
        this.liteNoteService = liteNoteService;
        bundleManager = ApplicationManager.getInstance().getSliteBundle();
        searchListPanel = new SearchListPanel();
        searchListPanel.setBorder(BorderUtil.hideLineBorder(Direction.TOP, 1, DefaultUIConstants.grayColor));
        timer = new Timer(100, e -> doSearch());
        timer.setRepeats(false);
        super.setContentPane(searchListPanel);
        SwingStateManager.registerListener(this);
    }

    /**
     * 监听编辑状态
     */
    @Subscribe
    void listenerEditState(LiteNoteEditState liteNoteEditState) {
        super.setVisible(false);
    }


    @Subscribe
    void listenerDeleteState(LiteNoteDeleteState liteNoteDeleteState) {
        JPanel listPanel = searchListPanel.container;
        Component deleteComponent = liteNoteDeleteState.getDeleteComponent();
        listPanel.remove(deleteComponent);
        listPanel.revalidate();
        listPanel.repaint();
        try {
            liteNoteService.deleteById(liteNoteDeleteState.getId());
        } catch (IOException e) {
            MessageUtil.error(listPanel, bundleManager.getError("LiteNote.list.item.delete-error", e));
        }

    }

    @Subscribe
    void listenerSearchSate(LiteNoteSearchState liteNoteSearchState) {
        this.searchContent = liteNoteSearchState.getSearchContent();
        timer.restart();
    }

    @Subscribe
    void listenerCloseState(LiteNoteCloseState liteNoteCloseState) {
        this.setVisible(false);
    }

    @Subscribe
    void listenerDragState(LiteNoteDragState liteNoteDragState) {
        if (super.isVisible()) {
            updateLocation(liteNoteDragState.getRectangle());
        }
    }


    private void doSearch() {
        if (StrUtil.isBlank(searchContent)) {
            super.setVisible(false);
        } else {
            searchListPanel.search();
            // 显示位置
            Rectangle rectangle = liteNoteView.getSearchLocation();
            updateLocation(rectangle);
            super.setVisible(true);
        }
        timer.stop();
    }


    private class SearchListPanel extends JPanel {

        /* 容器*/
        private JPanel container;

        /*分页组件*/
        private JEMorePagination pagination;
        /* 系统UI*/
        private ThemeUI themeUI;
        private int currentPage = 1;
        private final int pageSize = 20;
        private Page<LiteNote> pageData;

        public SearchListPanel() {
            this.themeUI = ThemeFlatLaf.systemTheme;
            this.setLayout(new MigLayout("insets 0,gap 0", "[grow]", "[grow]"));
            init();
            render();
            bindEvents();
        }


        private void init() {
            // 分页组件
            pagination = new JEMorePagination(pageSize, bundleManager.getString("LiteNote.list.loadMore.text"), bundleManager.getString("LiteNote.list.noMore.text"));
            this.container = pagination.getContentPanel();

            JScrollPane scrollPane = pagination.getContentScrollPane();
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            // 设置滚动量
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);
            pagination.setScrollPaginationListener((contentPanel, pager) -> {
                contentPanel.removeAll();
                currentPage = pager.getCurrentPage();
                try {
                    // 重新获取
                    if (currentPage > 1) {
                        pageData = liteNoteService.searchPage(searchContent, currentPage, pageSize);
                    }
                    pageData.getList().forEach(liteNote -> {
                        LiteNoteItemPanel itemPanel = new LiteNoteItemPanel(liteNote);
                        contentPanel.add(itemPanel, "w 100%");
                    });
                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (IOException e) {
                    MessageUtil.error(this, bundleManager.getError("LiteNote.list.search-error", e));
                }

            });
//            search();
        }


        private void render() {
            super.add(pagination, "grow");
        }

        /**
         * 搜索
         */
        private void search() {
            // 搜索内容变更时,重置
            String oldSearchContent = Objects.toString(this.container.getClientProperty("searchContent"));
            if (!StrUtil.equals(oldSearchContent, searchContent)) {
                currentPage = 1;
            } else {
                this.container.putClientProperty("searchContent", searchContent);
            }
            // 重置时重新查询总数
            if (currentPage == 1) {
                try {
                    pageData = liteNoteService.searchPage(searchContent, currentPage, pageSize);
                } catch (IOException e) {
                    MessageUtil.error(this, bundleManager.getError("LiteNote.list.search-error", e));
                }
                pagination.setTotalCount(pageData.getTotalCount());
            }
        }

        private void bindEvents() {

            // 全局热键
            KeyActionFactory.registerGlobalKey(new GlobalKeyListener() {
                @Override
                public void callable(KeyEvent ke) {
                    int index = -1;
                    if (ke.getKeyCode() == KeyEvent.VK_1 && ke.isControlDown()) {
                        index = 0;
                    } else if (ke.getKeyCode() == KeyEvent.VK_2 && ke.isControlDown()) {
                        index = 1;
                    } else if (ke.getKeyCode() == KeyEvent.VK_3 && ke.isControlDown()) {
                        index = 2;
                    } else if (ke.getKeyCode() == KeyEvent.VK_4 && ke.isControlDown()) {
                        index = 3;
                    }

                    if (index > -1 && container.getComponentCount() > index + 1) {
                        JComponent c = (JComponent) container.getComponent(index);
                        c.getActionMap().get("CopyCode").actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        ThemeFlatLaf.install();
        FrameUtil.launchTest(SearchListPanel.class);
    }

}
