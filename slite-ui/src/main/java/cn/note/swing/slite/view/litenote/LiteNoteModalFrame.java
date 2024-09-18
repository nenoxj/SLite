package cn.note.swing.slite.view.litenote;

import cn.note.swing.slite.core.DefaultUIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * 模态对话框
 */
class LiteNoteModalFrame extends JFrame {

    private int showHeight;

    public LiteNoteModalFrame(int height) {
        this.showHeight = height;
        super.setUndecorated(true);
        super.setIconImage(DefaultUIConstants.APP_ICON.getImage());
    }


    protected void updateLocation(Rectangle rectangle) {
        rectangle.y = rectangle.y + rectangle.height;
        rectangle.height = showHeight;
        super.setBounds(rectangle);
    }
}
