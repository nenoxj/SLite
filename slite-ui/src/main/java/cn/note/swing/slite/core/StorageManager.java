package cn.note.swing.slite.core;

import cn.note.swing.core.filestore.RelativeFileStore;
import cn.note.swing.core.filestore.SettingFileStore;
import cn.note.swing.core.filestore.SystemFileManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 本地存储管理
 */
@Slf4j
public class StorageManager {

    private static StorageManager storageManager = new StorageManager();
    /* 默认存储 */
    private RelativeFileStore defaultFileStore;

    /* 配置存储 */
    private RelativeFileStore configFileStore;

    /* 数据库文件存储*/
    @Getter
    private RelativeFileStore dataFileStore;

    /* 索引文件存储*/
    @Getter
    private RelativeFileStore indexFileStore;

    private StorageManager() {
        this.defaultFileStore = new RelativeFileStore(SystemFileManager.USER_HOME, ".slite");
        configFileStore = defaultFileStore.getRelativeFileStore("config");
        dataFileStore = defaultFileStore.getRelativeFileStore("data");
        indexFileStore = defaultFileStore.getRelativeFileStore("index");
    }

    public static StorageManager getInstance() {
        return storageManager;
    }

    public File getLockFilePath() {
        return defaultFileStore.getFile("slite.lock");
    }

    /**
     * 获取配置存储对象
     *
     * @param clazz 存储类
     */
    public <T> SettingFileStore<T> getConfigFileStore(Class clazz) {
        return new SettingFileStore<T>(configFileStore, clazz.getSimpleName());
    }


    /**
     * 全局默认存储对象
     */
    public RelativeFileStore getGlobalDefaultFileStore() {
        return defaultFileStore;
    }

    /**
     * 获取根路径下存储
     */
    public RelativeFileStore getRelativeFileStore(String folderName) {
        return defaultFileStore.getRelativeFileStore(folderName);
    }

}
