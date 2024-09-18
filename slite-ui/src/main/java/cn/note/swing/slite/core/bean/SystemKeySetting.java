package cn.note.swing.slite.core.bean;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * @author jee
 * @version 1.0
 */
@Setter
@Getter
public class SystemKeySetting {

    private final String openSearchKeyName = "openSearchWindow";

    private final String openAddKeyName = "openAddWindow";

    private KeyStroke openSearchKeyStroke;

    private KeyStroke openAddKeyStroke;
}
