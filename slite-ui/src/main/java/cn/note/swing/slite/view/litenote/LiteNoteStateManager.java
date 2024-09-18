package cn.note.swing.slite.view.litenote;

import cn.note.swing.core.state.SwingStateManager;
import cn.note.swing.slite.view.litenote.state.*;

/**
 * 状态管理
 * 主要用于各个UI状态交互
 */
public class LiteNoteStateManager {



    /**
     * 编辑状态
     *
     * @see LiteNoteListWindow#listenerEditState(LiteNoteEditState)
     * @see LiteNoteEditDialog#listenerEditState(LiteNoteEditState)
     */
    public static void postEditState(LiteNoteEditState liteNoteEditState) {
        SwingStateManager.sendMessage(liteNoteEditState);
    }


    /**
     * 删除状态
     *
     * @see LiteNoteListWindow#listenerDeleteState(LiteNoteDeleteState)
     */
    public static void postDeleteState(LiteNoteDeleteState liteNoteDeleteState) {
        SwingStateManager.sendMessage(liteNoteDeleteState);
    }


    /**
     * 拖拽状态
     *
     * @see LiteNoteListWindow#listenerDragState(LiteNoteDragState)
     * @see LiteNoteEditDialog#listenerDragState(LiteNoteDragState)
     */
    public static void postDragState(LiteNoteDragState liteNoteDragState) {
        SwingStateManager.sendMessage(liteNoteDragState);
    }

    /**
     * 搜索状态
     *
     * @see LiteNoteListWindow#listenerSearchSate(LiteNoteSearchState)
     * @see LiteNoteEditDialog#listenerSearchSate(LiteNoteSearchState)
     * @see LiteNoteSearchDialog#listenerSearchSate(LiteNoteSearchState)
     */
    public static void postSearchState(LiteNoteSearchState liteNoteSearchState) {
        SwingStateManager.sendMessage(liteNoteSearchState);
    }

    /**
     * 关闭状态
     *
     * @see LiteNoteListWindow#listenerCloseState(LiteNoteCloseState)
     * @see LiteNoteEditDialog#listenerCloseState(LiteNoteCloseState)
     */
    public static void postCloseState(LiteNoteCloseState liteNoteCloseState) {
        SwingStateManager.sendMessage(liteNoteCloseState);
    }

}
