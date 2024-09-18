package cn.note.swing.slite.core.repository;

import cn.note.swing.slite.core.bean.SystemKeySetting;

import static cn.note.swing.slite.core.DefaultUIConstants.ADD_WINDOW_KEYSTROKE;
import static cn.note.swing.slite.core.DefaultUIConstants.SEARCH_WINDOW_KEYSTROKE;

/**
 * @author jee
 * @version 1.0
 */
public class SystemKeySettingRepository extends SettingRepository<SystemKeySetting> {


    @Override
    public SystemKeySetting load() throws Exception {
        SystemKeySetting systemKeySetting = super.load();
        if (systemKeySetting == null) {
            return defaultSetting();
        }
        return systemKeySetting;
    }


    private SystemKeySetting defaultSetting() {
        SystemKeySetting systemKeySetting = new SystemKeySetting();
        systemKeySetting.setOpenAddKeyStroke(ADD_WINDOW_KEYSTROKE);
        systemKeySetting.setOpenSearchKeyStroke(SEARCH_WINDOW_KEYSTROKE);
        return systemKeySetting;
    }
}
