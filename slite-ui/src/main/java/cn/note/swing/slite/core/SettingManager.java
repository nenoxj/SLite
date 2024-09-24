package cn.note.swing.slite.core;

import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import cn.note.slite.litenote.service.LiteNoteHardDiskService;
import cn.note.slite.litenote.service.LiteNoteService;
import cn.note.swing.core.filestore.SettingFileStore;
import cn.note.swing.slite.core.bean.BasicSetting;
import cn.note.swing.slite.core.bean.Config;
import cn.note.swing.slite.core.bean.SystemKeySetting;
import cn.note.swing.slite.core.repository.BasicSettingRepository;
import cn.note.swing.slite.core.repository.SystemKeySettingRepository;
import cn.note.swing.slite.view.litenote.LiteNoteStateManager;
import cn.note.swing.slite.view.litenote.state.LiteNoteEditState;
import cn.note.swing.slite.view.litenote.state.LiteNoteSearchState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * @author jee
 * @version 1.0
 */
@Slf4j
public class SettingManager {

    private static final SettingManager SETTING_MANAGER = new SettingManager();


    @Getter
    private final BasicSettingRepository basicSettingRepository;

    @Getter
    private final SystemKeySettingRepository systemKeySettingRepository;
    /*数据目录*/
    @Getter
    private File dataDirectory;

    /* 内容服务*/
    private LiteNoteService liteNoteService;
    /*索引*/
    @Getter
    private File indexDirectory;

    @Getter
    private Config config;

    private SettingManager() {
        basicSettingRepository = new BasicSettingRepository();
        systemKeySettingRepository = new SystemKeySettingRepository();
        indexDirectory = StorageManager.getInstance().getIndexFileStore().homeDir();
        dataDirectory = StorageManager.getInstance().getDataFileStore().homeDir();
    }

    public static SettingManager getInstance() {
        return SETTING_MANAGER;
    }


    /**
     * 变更数据存储目录
     */
    public void changeDataDirectory(File dataDirectory) throws IOException {
        if (liteNoteService != null) {
            if (!dataDirectory.equals(this.dataDirectory)) {
                this.dataDirectory = dataDirectory;
                liteNoteService.changeStoreDirectory(dataDirectory);
            }
        }
    }

    public <T> SettingFileStore<T> getConfigFileStore(Class clazz) {
        return StorageManager.getInstance().getConfigFileStore(clazz);
    }

    public LiteNoteService getLiteNoteService() throws IOException {
        if (liteNoteService == null) {
            log.info("init dataFolder:{}", dataDirectory);
            log.info("init indexFolder:{}", indexDirectory);
            liteNoteService = new LiteNoteHardDiskService(dataDirectory, indexDirectory);
        }
        return liteNoteService;
    }


    /**
     * @throws Exception 初始化配置异常
     */
    public void initialize() throws Exception {
        initializeBasicSetting();
        initializeSystemKeySetting();
        initConfig();
    }


    /**
     * 初始化基本配置
     *
     * @throws Exception 基本配置初始化异常
     */
    private void initializeBasicSetting() throws Exception {
        BasicSetting basicSetting = basicSettingRepository.load();
        Console.log("basicSetting==>{}", JSONUtil.toJsonStr(basicSetting));
        if (basicSetting != null) {
            // 初始化语言
            String language = basicSetting.getLanguage();
            Locale locale;
            switch (language) {
                case "ZH_CN":
                    locale = Locale.CHINA;
                    break;
                case "EN_US":
                    locale = Locale.ENGLISH;
                    break;
                default:
                    locale = Locale.getDefault();
                    break;
            }
            Locale.setDefault(locale);

            // 初始化存储目录
            String dataDirectory = basicSetting.getDataDirectory();
            if (dataDirectory != null) {
                File dataDir = new File(dataDirectory);
                if (dataDir.exists() && dataDir.isDirectory()) {
                    this.dataDirectory = dataDir;
                }
            }
        }
    }

    /**
     * 初始化系统快捷键配置
     *
     * @throws Exception 系统快捷键配置初始化异常
     */
    private void initializeSystemKeySetting() throws Exception {
        SystemKeySetting systemKeySetting = systemKeySettingRepository.load();

        // 添加打开窗口系统按键
        SystemKeyManager.getInstance().registerSystemHotKey(systemKeySetting.getOpenSearchKeyName(), systemKeySetting.getOpenSearchKeyStroke(), () -> {
            LiteNoteStateManager.postSearchState(new LiteNoteSearchState());
        });

        // 添加搜索窗口系统按键
        SystemKeyManager.getInstance().registerSystemHotKey(systemKeySetting.getOpenAddKeyName(), systemKeySetting.getOpenAddKeyStroke(), () -> {
            LiteNoteStateManager.postEditState(new LiteNoteEditState());
        });
    }


    /**
     * 默认配置, 暂时未开发配置项
     */
    private void initConfig() {
        config = new Config();
    }
}
