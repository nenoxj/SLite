package cn.note.swing.slite.view.menu.setting;

import cn.note.swing.core.util.ButtonFactory;
import cn.note.swing.core.util.MessageUtil;
import cn.note.swing.core.util.SwingCoreUtil;
import cn.note.swing.core.view.form.JEForm;
import cn.note.swing.core.view.form.JEInputFormItem;
import cn.note.swing.core.view.textfield.JEKeymapTextField;
import cn.note.swing.slite.core.*;
import cn.note.swing.slite.core.bean.SystemKeySetting;
import cn.note.swing.slite.core.repository.SystemKeySettingRepository;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * 快捷键设置
 */
@Slf4j
public class KeyMapSettingPanel extends JPanel {

    private final BundleManager bundleManager = ApplicationManager.getInstance().getSliteBundle();

    private JEForm form;

    private JLabel tip;

    private JButton confirm;

    private JEKeymapTextField openSearchKeymap;

    private JEKeymapTextField openAddKeymap;

    private SystemKeySetting systemKeySetting;

    private SystemKeySettingRepository systemKeySettingRepository;

    public KeyMapSettingPanel() {
        init();
        render();
        bindEvents();
    }

    private void init() {
        super.setLayout(new MigLayout("", "[grow]", "[grow][]"));
        systemKeySettingRepository = SettingManager.getInstance().getSystemKeySettingRepository();
        try {
            systemKeySetting = systemKeySettingRepository.load();
            openSearchKeymap = createKeyMapTextField(systemKeySetting.getOpenSearchKeyName(), systemKeySetting.getOpenSearchKeyStroke());
            openAddKeymap = createKeyMapTextField(systemKeySetting.getOpenAddKeyName(), systemKeySetting.getOpenAddKeyStroke());

            form = new JEForm();
            form.addFormItem(new JEInputFormItem(bundleManager.getString("Setting.keymap.search-window.text"), openSearchKeymap), "openSearchKeyStroke");
            form.addFormItem(new JEInputFormItem(bundleManager.getString("Setting.keymap.add-window.text"), openAddKeymap), "openAddKeyStroke");
        } catch (Exception e) {
            log.error("Load keyMap setting error:", e);
        }
        tip = new JLabel(bundleManager.getString("Setting.keymap.tip"));
        tip.setForeground(DefaultUIConstants.redColor);
        confirm = ButtonFactory.primaryButton(bundleManager.getString("TrayMenu.item.setting.button.confirm-text"));
    }

    private void render() {
        super.add(form, "cell 0 0,aligny top,w 300::500");
        super.add(tip, "cell 0 1,growx");
        super.add(confirm, "cell 0 1,right");
    }

    private void bindEvents() {
        confirm.addActionListener(e -> {
            try {
                systemKeySetting.setOpenSearchKeyStroke(openSearchKeymap.getKeyStroke());
                systemKeySetting.setOpenAddKeyStroke(openAddKeymap.getKeyStroke());
                systemKeySettingRepository.save(systemKeySetting);
                MessageUtil.ok(confirm,bundleManager.getString("Setting.save.success.tip"));
            } catch (Exception e1) {
                log.error("Save keyMap setting error:", e1);
            }
        });
    }


    /**
     * @param bindKeyName 按键绑定key名称
     * @return 按键编辑器
     */
    private JEKeymapTextField createKeyMapTextField(String bindKeyName, KeyStroke defaultKeyStroke) {
        JEKeymapTextField keymapTextField = new JEKeymapTextField(defaultKeyStroke);
        SwingCoreUtil.handCursors(keymapTextField);
        keymapTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SystemKeyManager.getInstance().unregisterSystemHotKey(bindKeyName);
            }

            @Override
            public void focusLost(FocusEvent e) {
                SystemKeyManager.getInstance().updateSystemHotKey(bindKeyName, keymapTextField.getKeyStroke());
            }
        });
        return keymapTextField;
    }


}
