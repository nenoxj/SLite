package cn.note.swing.slite.view.menu;

import cn.note.swing.core.util.ButtonFactory;
import cn.note.swing.core.util.MessageUtil;
import cn.note.swing.core.util.WinUtil;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.SettingManager;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * 托盘右键窗口
 *
 * @author jee
 * @version 1.0
 */
public class TrayPopupDialog extends JDialog {

    private JPanel popupContainer;

    /*关于窗口*/
    private AboutFrame aboutWin;

    private SettingFrame settingFrame;

    private BundleManager bundleManager;

    public TrayPopupDialog(JFrame owner) {
        super(owner);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
//        this.setModalityType(ModalityType.APPLICATION_MODAL);

        // 不在激活窗口时,自动隐藏
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                TrayPopupDialog.this.setVisible(false);
            }
        });
        init();
    }

    private void init() {
        bundleManager = ApplicationManager.getInstance().getSliteBundle();
        popupContainer = new JPanel(new MigLayout("insets 0,gap 0, wrap", "grow", "grow"));
        popupContainer.setBorder(BorderFactory.createLineBorder(DefaultUIConstants.grayColor));
        super.add(popupContainer);
        registerPopupMenu();
    }


    private void addButton(String name, ActionListener actionListener) {
        JButton btn = new JButton(name);
        btn.setFocusable(false);
        ButtonFactory.decorativeButton(btn, Color.WHITE, Color.black, new Color(220, 220, 220), true);
        btn.setBorder(new FlatEmptyBorder(5, 5, 5, 5));
        btn.addActionListener(actionListener);
        popupContainer.add(btn, "h 30!,growx");
        super.setSize(DefaultUIConstants.getMenuItemWidth(), DefaultUIConstants.getMenuItemHeight()* popupContainer.getComponentCount());
    }

    private void registerPopupMenu() {
        //重建索引
        addButton(bundleManager.getString("TrayMenu.item.rebuild-index"), e -> {
            try {
                SettingManager.getInstance().getLiteNoteService().rebuildIndex();
                MessageUtil.ok(popupContainer,bundleManager.getString("TrayMenu.item.rebuild-index.success"));
            } catch (IOException e1) {
                WinUtil.alert(bundleManager.getError("TrayMenu.item.rebuild-index.error", e1));
            }
        });

        //设置
        addButton(bundleManager.getString("TrayMenu.item.setting"), e -> {
            if (settingFrame == null) {
                settingFrame = new SettingFrame();
            }
            settingFrame.setVisible(true);
        });


        // 关于
        addButton(bundleManager.getString("TrayMenu.item.about"), e -> {
            if (aboutWin == null) {
                aboutWin = new AboutFrame();
            }
            aboutWin.setVisible(true);
        });
    }

    public void registerQuitAction(ActionListener actionListener) {
        addButton(bundleManager.getString("TrayMenu.item.quit"), actionListener);
    }


}
