package cn.note.swing.slite.view.menu;

import cn.hutool.core.util.StrUtil;
import cn.note.swing.core.util.ButtonFactory;
import cn.note.swing.core.util.LocationUtil;
import cn.note.swing.core.util.SwingCoreUtil;
import cn.note.swing.core.util.WinUtil;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.slite.view.component.ModalFrame;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;

/**
 * 关于视图
 *
 * @author jee
 * @version 1.0
 */
public class AboutFrame extends ModalFrame {
    private BundleManager bundleManager;


    public AboutFrame() {
        LocationUtil.centerScreenWindow(this, 450, 300);
        init();
        render();
    }

    private void init() {
        this.bundleManager = ApplicationManager.getInstance().getSliteBundle();
        super.setTitle(bundleManager.getString("TrayMenu.item.about.title"));
    }

    private void render() {
        bodyPanel.add(new JLabel(bundleManager.getImageIcon("TrayMenu.item.about.icon")), "center,wrap");
        String title = bundleManager.getString("Application.title");
        String version = bundleManager.getString("Application.version");
        String author = bundleManager.getString("Application.author");
        JLabel description = new JLabel(StrUtil.format("{} {}", title, version, author));
        SwingCoreUtil.fixFontSize(description, 25);
        bodyPanel.add(description, "center,wrap");

        String helpUrl = bundleManager.getString("Application.helpUrl");
        JXHyperlink helpHyperLink = ButtonFactory.primaryHyperlink(helpUrl, e -> WinUtil.openBrowserUrl(helpUrl));
        bodyPanel.add(helpHyperLink, "center");
    }

    public static void main(String[] args) {
        ThemeFlatLaf.install();
//        Locale.setDefault(Locale.ENGLISH);
        new AboutFrame().setVisible(true);
    }
}
