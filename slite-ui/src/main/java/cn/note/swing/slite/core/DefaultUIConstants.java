package cn.note.swing.slite.core;

import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * 简单的UI颜色/高度/字体配置
 */
public class DefaultUIConstants {

    /*---------------icon---------------*/
    public static final BundleManager BUNDLE_MANAGER = ApplicationManager.getInstance().getSliteBundle();

    public static final ImageIcon CLOSE_ICON = BUNDLE_MANAGER.getIcon("Component.window.close.icon");

    public static final ImageIcon APP_ICON = BUNDLE_MANAGER.getImageIcon("Component.app.icon");

    /*---------------size---------------*/

    /**
     * 默认宽度
     */
    public static final int DEFAULT_WIDTH = 520;
    /**
     * 结果高度
     */
    public static final int RESULT_HEIGHT = 230;
    /**
     * 搜索高度
     */
    public static final int SEARCH_HEIGHT = 50;
    /**
     * 搜索Y轴偏移值
     */
    public static final int SEARCH_OFFSET_Y = 200;

    /*---------------color---------------*/

    /**
     * 主题色
     */
    public static final Color themeColor = Color.decode("#263238");

    /**
     * 灰色
     */
    public static final Color grayColor = Color.decode("#C2C2C2");
    /**
     * 黑色
     */
    public static final Color blackColor = Color.decode("#212121");

    /**
     * 蓝色
     */
    public static final Color blueColor = Color.decode("#1890FF");
    /**
     * 红色
     */
    public static final Color redColor = Color.decode("#FF4D4F");

    public static String toHEXColor(Color color) {
        String redHex = Integer.toHexString(color.getRed());
        String greenHex = Integer.toHexString(color.getGreen());
        String blueHex = Integer.toHexString(color.getBlue());
        return StrUtil.format("#{}{}{}", new Object[]{redHex, greenHex, blueHex}).toUpperCase();
    }

    /*---------------keystroke---------------*/
    /**
     * 编辑保存快捷键
     */
    public static final KeyStroke EDIT_SAVE_ACTION = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);

    /* 默认搜索快捷键*/
    public static final KeyStroke SEARCH_WINDOW_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);

    /* 默认添加快捷键*/
    public static final KeyStroke ADD_WINDOW_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);




}
