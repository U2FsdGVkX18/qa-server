package qa.common;

import java.text.SimpleDateFormat;

/**
 * 时间转换工具
 */
public class TimeConvertTool {

    /**
     * 传入时间戳格式时间,获取日期时间型时间-(13位时间戳)
     */
    public static String getDateTime13(Long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(time);
    }

}
