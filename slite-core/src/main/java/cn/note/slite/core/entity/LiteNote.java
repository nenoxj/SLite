package cn.note.slite.core.entity;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.ToString;

/**
 * @author jee
 * @version 1.0
 */
@ToString
public class LiteNote {

    /* 唯一ID*/
    private String id;

    /* 搜索内容*/
    private String content;

    /*分组*/
    private String groups;

    public LiteNote() {
    }

    public LiteNote(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getGroups() {
        return groups == null ? "" : groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
