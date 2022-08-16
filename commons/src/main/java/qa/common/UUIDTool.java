package qa.common;

import java.util.UUID;

/**
 * 封装uuid工具类
 */
public class UUIDTool {

    /**
     * 获取一个字符型uuid,如:5d0a4773-89b4-44ee-9777-b8ed01b8850c
     *
     * @return String
     */
    public static String generateStr() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取一个字符型uuid,不带中间横线,如:5d0a477389b444ee9777b8ed01b8850c
     *
     * @return String
     */
    public static String generateStrWithoutHyphen() {
        String uuidStr = UUID.randomUUID().toString();
        return uuidStr.replace("-", "");
    }
}
