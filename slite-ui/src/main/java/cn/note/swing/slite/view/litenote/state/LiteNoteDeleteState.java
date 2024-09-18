package cn.note.swing.slite.view.litenote.state;


import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Setter
@Getter
public class LiteNoteDeleteState {

    private String id;
    private JComponent deleteComponent;
    private JPopupMenu deletePopMenu;

}
