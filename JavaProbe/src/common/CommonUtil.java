package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 工具类
 */
public class CommonUtil {

    /**
     * 写文件通用方法
     * @param filePath 文件路径
     * @param outStr 写入内容
     */
    public static void writeStr(String filePath, String outStr) {

        try {

            BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true));
            output.write(outStr+"\n");
            output.close();
        }
        catch (Exception e) {

            System.out.println(e.getMessage());
        }

    }

    /**
     * 删除文件
     * @param filePath
     */
    public static void deleteRepeat(String filePath) {

        try {

            File file = new File(filePath);

            if (file.exists()) {

                file.delete();
            }
        }
        catch (Exception e) {

            System.out.println(e.getMessage());
        }

    }
}
