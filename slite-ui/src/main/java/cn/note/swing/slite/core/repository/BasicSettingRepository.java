package cn.note.swing.slite.core.repository;

import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.bean.BasicSetting;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author jee
 * @version 1.0
 */
public class BasicSettingRepository extends SettingRepository<BasicSetting> {

    private BundleManager bundleManager;

    public Map<String, String> getLanguageMap() {
        if (bundleManager == null) {
            bundleManager = ApplicationManager.getInstance().getSliteBundle();
            String[] arrays = bundleManager.getArray("Setting.basic.language.supports");
            for (String array : arrays) {
                String[] pair = array.split(":");
                languageMap.put(pair[0], pair[1]);
            }
        }
        return languageMap;
    }

    private Map<String, String> languageMap = new TreeMap<>();

}
