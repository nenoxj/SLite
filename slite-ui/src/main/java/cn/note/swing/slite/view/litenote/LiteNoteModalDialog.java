package cn.note.swing.slite.view.litenote;

import javax.swing.*;
import java.awt.*;

/**
 * 模态对话框
 */
class LiteNoteModalDialog extends JDialog {

    private int showHeight;

    public LiteNoteModalDialog(int height) {
        this.showHeight = height;
        super.setUndecorated(true);
    }


    protected void updateLocation(Rectangle rectangle) {
        rectangle.y = rectangle.y + rectangle.height;
        rectangle.height = showHeight;
        super.setBounds(rectangle);
    }
}
