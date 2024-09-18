package cn.note.swing.slite.core;

import cn.note.swing.core.lifecycle.LifecycleManager;
import com.tulskiy.keymaster.common.Provider;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * 系统按键注册器
 *
 * @author jee
 * @version 1.0
 */
@Setter
@Getter
public class SystemKeyRegister {

    /**
     * 热键值
     */
    private KeyStroke keyStroke;

    /**
     * 热键监听器
     */
    private Runnable listener;


    private Provider provider;


    /**
     * 是否注册
     */
    private boolean register;

    public SystemKeyRegister(KeyStroke keyStroke, @Nonnull Runnable listener) {
        this.keyStroke = keyStroke;
        this.listener = listener;
        this.provider = Provider.getCurrentProvider(true);
        // 添加销毁事件
        LifecycleManager.addDestroyEvent(provider::stop);
        register();
    }


    /**
     * 注册系统按键
     */
    public void register() {
        if (keyStroke != null && !register) {
            provider.register(keyStroke, hotKey -> listener.run());
            register = true;
        }

    }


    /**
     * 取消注册
     */
    public void unregister() {
        if (register) {
            provider.unregister(keyStroke);
            register = false;
        }
    }


    /**
     * 更新注册key
     */
    public void updateRegisterKey(KeyStroke keyStroke) {
        if (keyStroke == null) {
            unregister();
        } else {
            if (this.keyStroke.equals(keyStroke) && !register) {
                register();
            } else {
                unregister();
                this.keyStroke = keyStroke;
                register();
            }
        }
    }


}
