package cn.note.swing.slite.view;

import cn.hutool.core.util.StrUtil;
import cn.note.swing.core.lifecycle.LifecycleManager;
import cn.note.swing.core.util.WinUtil;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.SystemKeyManager;
import cn.note.swing.slite.view.litenote.LiteNoteStateManager;
import cn.note.swing.slite.view.litenote.state.LiteNoteSearchState;
import cn.note.swing.slite.view.menu.TrayPopupDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

/**
 * 托盘窗口
 *
 * @author jee
 * @version 1.0.1
 */
@Slf4j
public class TrayFrame extends JFrame {


    private static boolean closing = false;

    /* 托盘按钮*/
    private TrayIcon trayIcon;

    /* 右键弹出框*/
    private TrayPopupDialog trayPopupDialog;
    /* 应用配置*/
    private BundleManager bundleManager;

    public TrayFrame() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        init();
        LiteNoteStateManager.postSearchState(new LiteNoteSearchState());
    }


    private void init() {
        bundleManager = ApplicationManager.getInstance().getSliteBundle();
        String title = bundleManager.getString("Application.title");
        String version = bundleManager.getString("Application.version");
        Image imageIcon = bundleManager.getImageIcon("Application.imageIcon").getImage();
        this.setTitle(StrUtil.format("{} {}", title, version));
        this.setIconImage(imageIcon);
        initTray();
    }

    /**
     * 初始化托盘
     */
    private void initTray() {
        if (!SystemTray.isSupported()) {
            WinUtil.alert("当前系统不支持托盘!");
            return;
        }
        Image trayImage = bundleManager.getImageIcon("Application.trayIcon").getImage();
        trayIcon = new TrayIcon(trayImage, bundleManager.getString("Application.title"));
        initTrayPopupDialog();
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化托盘右键菜单
     */
    private void initTrayPopupDialog() {
        trayPopupDialog = new TrayPopupDialog(this);
        trayPopupDialog.registerQuitAction(e -> {
            TrayFrame.this.processWindowEvent(new WindowEvent(TrayFrame.this, WindowEvent.WINDOW_CLOSING));
        });
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // 右键事件
                    Point p = e.getLocationOnScreen();
                    trayPopupDialog.setLocation(p.x, p.y - trayPopupDialog.getHeight());
                    trayPopupDialog.setVisible(true);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    // 单机事件
                    LiteNoteStateManager.postSearchState(new LiteNoteSearchState());
                }
            }
        });
    }

    /**
     * 关闭前,执行销毁动作
     *
     * @param e 重写关闭事件
     * @see LifecycleManager#destroyAll()
     * @see SystemKeyManager
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (!closing) {
                closing = true;
                if (LifecycleManager.getDestroyEventCount() > 0) {
                    this.setVisible(false);
                    CountDownLatch cdl = LifecycleManager.destroyAll();
                    try {
                        if (cdl != null) {
                            log.info("background in progress !");
                            cdl.await();
                            log.info("background complete !");
                        }
                    } catch (InterruptedException e1) {
                        // ignore
                    } finally {
                        super.processWindowEvent(e);
                    }
                } else {
                    super.processWindowEvent(e);
                }
            }

        } else {
            super.processWindowEvent(e);
        }
    }

}
