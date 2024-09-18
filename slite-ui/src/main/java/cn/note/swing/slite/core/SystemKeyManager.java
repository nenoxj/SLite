package cn.note.swing.slite.core;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 按键管理
 *
 * @author jee
 * @version 1.0
 */
@Slf4j
public class SystemKeyManager {

    private static SystemKeyManager INSTANCE = new SystemKeyManager();

    private final Map<String, SystemKeyRegister> keyStrokeMap;

    private SystemKeyManager() {
        keyStrokeMap = new HashMap<>();
    }

    public static SystemKeyManager getInstance() {
        return INSTANCE;
    }

    public void registerSystemHotKey(String keyName, KeyStroke keyStroke, Runnable listener) {
        if (keyName != null) {
            SystemKeyRegister systemKeyRegister = new SystemKeyRegister(keyStroke, listener);
            keyStrokeMap.put(keyName, systemKeyRegister);
        }
    }

    public void reRegisterSystemHotKey(String keyName) {
        if (keyStrokeMap.containsKey(keyName)) {
            SystemKeyRegister systemKeyRegister = keyStrokeMap.get(keyName);
            systemKeyRegister.register();
        }

    }

    public void unregisterSystemHotKey(String keyName) {
        if (keyStrokeMap.containsKey(keyName)) {
            SystemKeyRegister systemKeyRegister = keyStrokeMap.get(keyName);
            systemKeyRegister.unregister();
        }
    }

    public void updateSystemHotKey(String keyName, KeyStroke keyStroke) {
        if (keyStrokeMap.containsKey(keyName)) {
            SystemKeyRegister systemKeyRegister = keyStrokeMap.get(keyName);
            systemKeyRegister.updateRegisterKey(keyStroke);
        }
    }

}
