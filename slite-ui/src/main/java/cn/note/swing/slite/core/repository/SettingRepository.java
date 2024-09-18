package cn.note.swing.slite.core.repository;

import cn.note.swing.core.filestore.SettingFileStore;
import cn.note.swing.slite.core.SettingManager;

/**
 * 配置存储类
 *
 * @author jee
 * @version 1.0
 */
public abstract class SettingRepository<T> {


    /**
     * 获取存储对象
     */
    public SettingFileStore<T> getStore() throws Exception {
        return SettingManager.getInstance().getConfigFileStore(this.getClass());
    }

    /**
     * 保存配置
     */
    public void save(T setting) throws Exception {
        getStore().serialize(setting);
    }

    /**
     * 加载配置
     */
    public T load() throws Exception {
        return getStore().deserialize();
    }
}
