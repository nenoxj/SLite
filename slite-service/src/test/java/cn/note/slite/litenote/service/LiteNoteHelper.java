package cn.note.slite.litenote.service;

import cn.hutool.core.util.IdUtil;
import cn.note.slite.core.entity.LiteNote;

import java.util.ArrayList;
import java.util.List;

public class LiteNoteHelper {

    public static List<LiteNote> liteNoteList = new ArrayList<>();

    public static void createLiteNote(String content, String keywords) {
        liteNoteList.add(getLiteNote(content, keywords));
    }

    public static LiteNote getLiteNote(String content, String keywords) {
        LiteNote liteNote = new LiteNote();
        liteNote.setId(IdUtil.simpleUUID());
        liteNote.setContent(content);
        liteNote.setGroups(keywords);
        return liteNote;
    }


}
