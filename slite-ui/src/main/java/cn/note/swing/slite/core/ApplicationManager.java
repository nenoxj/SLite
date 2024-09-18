package cn.note.swing.slite.core;

import cn.note.slite.litenote.service.LiteNoteService;
import cn.note.swing.core.util.WinUtil;
import cn.note.swing.core.view.filechooser.FileChooserUtil;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.slite.view.litenote.LiteNoteView;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 应用配置
 *
 * @author jee
 * @version 1.0
 */
@Slf4j
public class ApplicationManager implements Serializable {
    private static final long serialVersionUID = -7803224023035359062L;

    private static final ApplicationManager INSTANCE = new ApplicationManager();

    /*资源包*/
    private BundleManager sliteBundle;

    private ApplicationManager() {
    }


    public static ApplicationManager getInstance() {
        return INSTANCE;
    }


    public BundleManager getSliteBundle() {
        if (sliteBundle == null) {
            sliteBundle = new BundleManager("setting", "SliteBundle");
        }
        return sliteBundle;
    }

    public void initMainView() {
        try {
            LiteNoteService liteNoteService = SettingManager.getInstance().getLiteNoteService();
            new LiteNoteView(liteNoteService);
        } catch (Exception e) {
            WinUtil.alert(sliteBundle.getString("Application.initError.text") + e.getMessage());
            System.exit(-1);
            throw new RuntimeException(e);
        }

    }

    /**
     * 系统初始化
     *
     * @throws Exception 初始化异常
     */
    public static void initialize() throws Exception {
        // 初始化配置
        SettingManager.getInstance().initialize();
        // 初始化主题
        ThemeFlatLaf.install();
        // 初始化主视图
        ApplicationManager.getInstance().initMainView();
        // 初始化文件选择器
        FileChooserUtil.install();
    }

}
