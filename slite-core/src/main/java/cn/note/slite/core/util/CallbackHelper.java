package cn.note.slite.core.util;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Console;

/**
 * 回调辅助类
 *
 * @author jee
 * @version 1.0
 */
public class CallbackHelper {

    public static void stopwatchProxy(Callback callback) throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        callback.call();
        sw.stop();
        Console.log("opt time:==>{} ms", sw.getTotalTimeMillis());

    }
}
