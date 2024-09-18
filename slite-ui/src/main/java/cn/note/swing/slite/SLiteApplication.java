
package cn.note.swing.slite;

import cn.note.swing.slite.core.ApplicationManager;
import cn.note.swing.slite.core.StorageManager;
import cn.note.swing.slite.view.TrayFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * SLite应用程序主类，负责初始化应用程序并支持单实例运行。
 *
 * @author jee
 * @version 1.0.1
 */
@Slf4j
public class SLiteApplication {


    private static FileLock FILE_LOCK;

    /**
     * 锁定文件路径，用于保证应用程序的单实例运行。
     */
    private static RandomAccessFile RANDOM_ACCESS_FILE;

    /**
     * 程序入口点。
     *
     * @param args 命令行参数
     * @throws Exception 如果初始化过程中发生错误，则抛出异常
     */
    public static void main(String[] args) {
        try {
            singleLaunch(() -> {
                try {
                    ApplicationManager.initialize();
                    new TrayFrame();
                } catch (Exception e) {
                    throw new RuntimeException("Initialization failed", e);
                }
            });
        } catch (Exception e) {
            log.error("Application failed to start: ", e);
        }
    }

    /**
     * 保证应用程序只能单实例运行，并在程序退出时释放锁。
     *
     * @param runnable 应用程序启动后需要执行的运行时代码
     */
    private static void singleLaunch(Runnable runnable) {
        try {
            RANDOM_ACCESS_FILE = new RandomAccessFile(StorageManager.getInstance().getLockFilePath(), "rw");
            log.info("{}==>", RANDOM_ACCESS_FILE);
            FILE_LOCK = RANDOM_ACCESS_FILE.getChannel().tryLock();
            if (FILE_LOCK != null) {
                SwingUtilities.invokeLater(runnable);
            } else {
                log.info("Program is running!");
            }
        } catch (IOException e) {
            log.error("Unable to acquire lock file: {}, error: {}", RANDOM_ACCESS_FILE, e.getMessage());
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(SLiteApplication::releaseLock));
        }
    }

    /**
     * 释放文件锁，确保资源被正确关闭。
     */
    private static void releaseLock() {
        try {
            if (FILE_LOCK != null) {
                FILE_LOCK.release();
            }
            if (RANDOM_ACCESS_FILE != null) {
                RANDOM_ACCESS_FILE.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}