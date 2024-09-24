package cn.note.swing.slite.view.menu;

import cn.note.swing.core.util.BorderUtil;
import cn.note.swing.core.util.LocationUtil;
import cn.note.swing.core.view.Direction;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.view.component.ModalFrame;
import cn.note.swing.slite.view.menu.setting.BasicSettingPanel;
import cn.note.swing.slite.view.menu.setting.KeyMapSettingPanel;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

/**
 * 设置视图
 *
 * @author jee
 * @version 1.0
 */
public class SettingFrame extends ModalFrame {
    private BundleManager bundleManager;


    public SettingFrame() {
        LocationUtil.centerScreenWindow(this, DefaultUIConstants.getMenuPanelWidth(), DefaultUIConstants.getMenuPanelHeight());
        init();
        render();
    }

    private void init() {
        this.bundleManager = ApplicationManager.getInstance().getSliteBundle();
        super.setTitle(bundleManager.getString("TrayMenu.item.setting.title"));
    }

    private void render() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_INSETS, new Insets(5, 50, 5, 50));
        tabbedPane.addTab(bundleManager.getString("Setting.basic.text"), new BasicSettingPanel());
        tabbedPane.addTab(bundleManager.getString("Setting.keymap.text"), new KeyMapSettingPanel());
        tabbedPane.setBorder(BorderUtil.lineBorder(Direction.TOP, 1, DefaultUIConstants.grayColor));
        bodyPanel.add(tabbedPane, "grow,gaptop 10");
    }

    public static void main(String[] args) throws Exception {
        ApplicationManager.initialize();
        new SettingFrame().setVisible(true);
    }
}
