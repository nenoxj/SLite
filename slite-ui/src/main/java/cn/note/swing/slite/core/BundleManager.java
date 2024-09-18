package cn.note.swing.slite.core;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 资源管理器
 * 基于ResourceBundle 的getString扩展封装
 *
 * @author jee
 * @version 1.0
 */
@Slf4j
public class BundleManager {

    private ResourceBundle bundle = null;

    private String resourcesPackage;

    /**
     * @param resourcesPackage 配置文件所在目录以.拼接
     * @param propertiesName   配置文件名称不需要带.properties
     */
    public BundleManager(String resourcesPackage, String propertiesName) {
        try {
            this.resourcesPackage = resourcesPackage;
            Locale locale = Locale.getDefault();
            if (locale.getLanguage().equals("zh")) {
                propertiesName += "_zh_CN";
            } else if (locale.getLanguage().equals("en")) {
                propertiesName += "_en_US";
            }
            log.info("create bundle==>{}", propertiesName);
            bundle = ResourceBundle.getBundle(StrUtil.format("{}.{}", resourcesPackage, propertiesName));
        } catch (MissingResourceException e) {
            log.error("Couldn't load bundle: {}", resourcesPackage, e);
        }
    }


    /**
     * @param key key名称
     * @return value值
     */
    public String getString(String key) {
        return bundle != null ? bundle.getString(key) : key;
    }

    public String getError(String key, Exception error) {
        return getString(key).concat(":").concat(error.getMessage());
    }


    public String[] getArray(String key) {
        return StrUtil.split(getString(key), ",");
    }


    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    /**
     * 获取颜色
     */
    public Color getColor(String key) {
        return Color.decode(getString(key));
    }

    /**
     * @param key 配置key
     * @return 是否包含
     */
    public boolean containsKey(String key) {
        return bundle.containsKey(key);
    }

    /**
     * @param key key名称
     * @return 快捷键
     */
    public KeyStroke getKeyStroke(String key) {
        return KeyStroke.getKeyStroke(getString(key));
    }

    /**
     * @param key key名称
     * @return 助记键
     */
    public char getMnemonic(String key) {
        return (getString(key)).charAt(0);
    }


    /**
     * 获取图片按扭
     *
     * @param iconKey 配置key
     * @return 图片图标
     */
    public ImageIcon getImageIcon(String iconKey) {
        return new ImageIcon(ResourceUtil.getResource(getIconUrl(iconKey)));
    }


    /**
     * @param key 前缀
     * @return url地址
     */
    public URL getUrl(String key) {
        return ResourceUtil.getResource(getIconUrl(key));
    }


    /**
     * @param iconKey 图标名称
     * @return 图标
     */
    public FlatSVGIcon getIcon(String iconKey) {
        return getIcon(iconKey, 16);
    }

    /**
     * @param iconKey 文件名称
     * @param size    大小
     * @return 图标
     */
    public FlatSVGIcon getIcon(String iconKey, int size) {
        return new FlatSVGIcon(getIconUrl(iconKey), size, size);
    }


    public String getIconUrl(String iconKey) {
        return StrUtil.format("{}/{}", resourcesPackage, bundle.getString(iconKey));
    }
}
