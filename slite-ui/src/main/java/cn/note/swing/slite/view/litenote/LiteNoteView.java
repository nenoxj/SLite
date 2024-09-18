package cn.note.swing.slite.view.litenote;

import cn.note.slite.litenote.service.LiteNoteService;
import cn.note.swing.core.view.modal.swingx.JModalConfiguration;

import java.awt.*;

/**
 * 笔记搜索窗口
 *
 * @author jee
 * @version 1.0
 */
public class LiteNoteView {

    private static LiteNoteView INSTANCE;

    private LiteNoteService liteNoteService;

    private final LiteNoteSearchDialog searchDialog;

    private final LiteNoteEditDialog editDialog;

    private final LiteNoteListWindow listDialog;

    public LiteNoteView(LiteNoteService liteNoteService) {
        JModalConfiguration.disableWindowDragging();
        searchDialog = new LiteNoteSearchDialog();
        editDialog = new LiteNoteEditDialog(this, liteNoteService);
        listDialog = new LiteNoteListWindow(this, liteNoteService);
    }


    /**
     * @return 获取搜索框位置
     */
    public Rectangle getSearchLocation() {
        return searchDialog.getBounds();
    }

    /**
     * @return 搜索是否可见
     * @see LiteNoteSearchDialog
     */
    public boolean isSearchVisible() {
        return searchDialog.isVisible();
    }


    public void activeSearchWindow() {
        if (!searchDialog.isVisible()) {
            searchDialog.setVisible(true);
        }
        searchDialog.toFront();
    }


}
