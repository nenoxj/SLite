package cn.note.swing.slite.view.litenote;

import cn.note.swing.core.view.modal.swingx.JModalWindow;

import java.awt.*;

/**
 * 模态对话框
 */
class LiteNoteModalWindow extends JModalWindow {

    private int showHeight;

    public LiteNoteModalWindow(int height) {
        this.showHeight = height;
    }


    protected void updateLocation(Rectangle rectangle) {
        rectangle.y = rectangle.y + rectangle.height;
        rectangle.height = showHeight;
        super.setBounds(rectangle);
    }
}
