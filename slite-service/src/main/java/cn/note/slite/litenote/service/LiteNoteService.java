package cn.note.slite.litenote.service;

import cn.note.slite.core.entity.LiteNote;
import cn.note.slite.core.entity.Page;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * LiteNote服务
 */
public interface LiteNoteService {


    /**
     * 批量保存服务
     * @param liteNoteList  笔记列表
     */
    void batchSave(List<LiteNote> liteNoteList) throws IOException;

    /**
     * 保存或更新笔记
     *
     * @param liteNote 笔记
     */
    void saveOrUpdate(LiteNote liteNote) throws IOException;


    /**
     * 删除笔记
     * @param id  笔记ID
     */
    void deleteById(String id) throws IOException;


    /**
     * 查询笔记列表
     *
     * @param text 查询文本
     * @return 查询列表
     */
    List<LiteNote> search(String text, int size) throws IOException;


    /**
     * 分页查询笔记列表
     *
     * @param text        查询文本
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 分页搜索结果
     * @throws IOException 查询异常
     */
    Page<LiteNote> searchPage(String text, int currentPage, int pageSize) throws IOException;

    /**
     * 重建索引
     */
    void rebuildIndex() throws IOException;


    /**
     * 修改存储目录
     * @param storeDirectory  存储目录
     */
    void changeStoreDirectory(File storeDirectory) throws IOException;
}
