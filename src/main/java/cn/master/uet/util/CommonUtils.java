package cn.master.uet.util;

import java.util.Random;

/**
 * @author by 11's papa on 2021年06月01日
 * @version 1.0.0
 */
public class CommonUtils {
    /**
     * 生成指定长度的code
     *
     * @param codeLength code长度
     * @return : java.lang.String
     */
    public static String randomCode(int codeLength) {
        String str = "0123456789abcdefghijklmnopqrstvuwxyzABCDEFGHIJKLMNOPQRSTVWXYZ";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            char temp = str.charAt(random.nextInt(str.length()));
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

}
