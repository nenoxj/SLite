package cn.note.swing.slite.view.menu.setting;

import cn.note.swing.core.util.ButtonFactory;
import cn.note.swing.core.util.MessageUtil;
import cn.note.swing.core.util.WinUtil;
import cn.note.swing.core.view.filechooser.FileChooserUtil;
import cn.note.swing.core.view.form.*;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.BundleManager;
import cn.note.swing.slite.core.DefaultUIConstants;
import cn.note.swing.slite.core.SettingManager;
import cn.note.swing.slite.core.bean.BasicSetting;
import cn.note.swing.slite.core.repository.BasicSettingRepository;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 基础设置
 */
@Slf4j
public class BasicSettingPanel extends JPanel {

    private final BundleManager bundleManager = ApplicationManager.getInstance().getSliteBundle();

    private JLabel applyOnRestartTip;

    private JESelectedFormItem<SelectedItem> languageSelected;

    private JEInputFormItem dataDirectory;

    private JButton confirm;

    private BasicSetting basicSetting;

    private BasicSettingRepository basicSettingRepository;

    public BasicSettingPanel() {
        init();
        render();
        bindEvents();
    }

    private void init() {
        basicSetting = new BasicSetting();
        basicSettingRepository = SettingManager.getInstance().getBasicSettingRepository();
        super.setLayout(new MigLayout("", "[grow]", "[][][grow][]"));
        applyOnRestartTip = new JLabel(bundleManager.getString("TrayMenu.item.setting.apply.on-restart"));
        applyOnRestartTip.setForeground(DefaultUIConstants.redColor);
        languageSelected = new JESelectedFormItem<SelectedItem>(bundleManager.getString("Setting.basic.select-language.text"));

        JTextField dataDirectoryChooser = FileChooserUtil.inputDirChooser(bundleManager.getString("Setting.basic.store-directory.text"));
        dataDirectoryChooser.setEditable(false);
        dataDirectory = new JEInputFormItem(bundleManager.getString("Setting.basic.store-directory.text"), dataDirectoryChooser);
        dataDirectory.validEmpty();
        confirm = ButtonFactory.primaryButton(bundleManager.getString("TrayMenu.item.setting.button.confirm-text"));


        // 加载语言配置
        Map<String, String> languageMap = basicSettingRepository.getLanguageMap();
        languageMap.forEach((key, value) -> {
            SelectedItem selectedItem = new SelectedItem(key, value);
            languageSelected.addSelectItem(selectedItem);
        });

        // 设置语言
        try {
            BasicSetting basicSetting = basicSettingRepository.load();
            if (basicSetting != null) {
                String language = basicSetting.getLanguage();
                if (languageMap.containsKey(language)) {
                    SelectedItem selectedItem = new SelectedItem(language, languageMap.get(language));
                    languageSelected.setFieldValue(selectedItem);
                }
            }
        } catch (Exception e) {
            log.error("load language error:", e);
        }

        //设置存储目录
        dataDirectory.setFieldValue(SettingManager.getInstance().getDataDirectory().getAbsolutePath());

    }

    private void render() {
        super.add(languageSelected, "cell 0 0");
        super.add(applyOnRestartTip, "cell 0 0");
        super.add(dataDirectory, "cell 0 1,aligny top");
        super.add(confirm, "cell 0 3,right");
    }

    private void bindEvents() {

        // 目录验证
        dataDirectory.addValidateForm(() -> {
            String dataDir = dataDirectory.getFieldValue();
            File file = new File(dataDir);
            if (!file.exists() || !file.isDirectory()) {
                return ValidateStatus.fail(bundleManager.getString("Setting.basic.data-dir.change.error"));
            }
            return ValidateStatus.ok();
        });


        // 保存语言配置
        confirm.addActionListener(e -> {

            String dataDir = dataDirectory.getFieldValue();
            try {
                SettingManager.getInstance().changeDataDirectory(new File(dataDir));
            } catch (IOException e1) {
                WinUtil.alert(bundleManager.getError("Setting.basic.data-dir.change.error", e1));
                return;
            }
            basicSetting.setDataDirectory(dataDir);
            SelectedItem selectedItem = languageSelected.getFieldValue();
            basicSetting.setLanguage(selectedItem.getKey());
            try {
                basicSettingRepository.save(basicSetting);
                MessageUtil.ok(confirm,bundleManager.getString("Setting.save.success.tip"));
            } catch (Exception e1) {
                log.error("set language error:", e);
            }
        });

    }

}
