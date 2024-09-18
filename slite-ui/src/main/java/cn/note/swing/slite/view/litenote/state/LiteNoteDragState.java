package cn.note.swing.slite.view.litenote.state;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class LiteNoteDragState {
    private Rectangle rectangle;

    public LiteNoteDragState(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
}
