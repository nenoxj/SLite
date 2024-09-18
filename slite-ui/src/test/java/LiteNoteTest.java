import cn.note.swing.core.util.FrameUtil;
import cn.note.swing.core.view.AbstractMigView;
import cn.note.swing.core.view.theme.ThemeFlatLaf;
import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.view.litenote.LiteNoteStateManager;
import cn.note.swing.slite.view.litenote.state.LiteNoteSearchState;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * @author jee
 * @version 1.0
 */
public class LiteNoteTest extends AbstractMigView {


    @Override
    protected void init() {
        LiteNoteStateManager.postSearchState(new LiteNoteSearchState());
    }

    @Override
    protected MigLayout defineMigLayout() {
        return new MigLayout();
    }

    @Override
    protected void render() {
        view.add(new JLabel("CTRL+Q"));
    }

    public static void main(String[] args) throws Exception {
        ThemeFlatLaf.install();
        ApplicationManager.initialize();
        FrameUtil.launchTest(LiteNoteTest.class);
    }
}
