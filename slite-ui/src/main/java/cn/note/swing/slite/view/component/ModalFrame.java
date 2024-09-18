package cn.note.swing.slite.view.component;

import cn.note.swing.core.component.ComponentMover;
import cn.note.swing.core.util.ButtonFactory;
import cn.note.swing.core.util.LocationUtil;
import cn.note.swing.core.util.SwingCoreUtil;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.slite.core.DefaultUIConstants;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * @author jee
 * @version 1.0
 */
public class ModalFrame extends JFrame {
    /*主容器*/
    private JPanel container;
    /*标题面板*/
    protected JPanel titlePanel;
    /*身体面板*/
    protected JPanel bodyPanel;
    /*标题*/
    private JLabel title;
    private JButton closeDialog;

    public ModalFrame() {
        this(null);
    }

    public ModalFrame(String title) {
        super.setUndecorated(true);
        ComponentMover cm = new ComponentMover();
        cm.registerComponent(this);
        init(title);
        render();

    }


    private void init(String title) {
        super.setIconImage(DefaultUIConstants.APP_ICON.getImage());
        container = new JPanel(new MigLayout("insets 0,gap 0", "grow", "[][grow]"));
        titlePanel = new JPanel(new MigLayout("insets 0", "grow"));
        bodyPanel = new JPanel(new MigLayout("insets 0", "grow", "grow"));
        this.title = new JLabel(title);
        SwingCoreUtil.offsetFontSize(this.title, 2f);

        //关闭按钮
        this.closeDialog = new JButton(DefaultUIConstants.CLOSE_ICON);
        this.closeDialog.setFocusable(false);
        this.closeDialog.setBorderPainted(false);
        ButtonFactory.decorativeButton(closeDialog, titlePanel.getBackground(), DefaultUIConstants.themeColor, DefaultUIConstants.redColor, false);
        closeDialog.addActionListener(e -> super.dispose());
    }

    private void render() {
        titlePanel.add(title, "gapleft 5");
        titlePanel.add(closeDialog, "w 25!,h 25!,right");
        container.add(titlePanel, "cell 0 0,growx");
        container.add(bodyPanel, "cell 0 1,grow");
        super.setContentPane(container);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public static void main(String[] args) throws Exception{
        ThemeFlatLaf.install();
        ModalFrame modalTitleDialog = new ModalFrame("测试");
        LocationUtil.centerScreenWindow(modalTitleDialog, 300, 500);
        modalTitleDialog.setVisible(true);
    }
}
