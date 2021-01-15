package com.sunmnet.bigdata.web.zntb.dataprovider.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;

/**
 * Created by lion on 2018/11/8.
 */
public class MD5Util {

        /**
         * MD5方法
         *
         * @param text 明文
         * @return 密文
         * @throws Exception
         */
        public static String md5(String text) throws Exception {
            //加密后的字符串
            String encodeStr= DigestUtils.md5Hex(text);
            System.out.println("MD5加密后的字符串为:encodeStr="+encodeStr);
            return encodeStr;
        }

        /**
         * MD5验证方法
         *
         * @param text 明文
         * @param md5 密文
         * @return true/false
         * @throws Exception
         */
        public static boolean verify(String text,  String md5) throws Exception {
            //根据传入的密钥进行验证
            String md5Text = MD5(text);
            System.out.println("MD5加密后的字符串为new="+md5Text);
            if(md5Text.equalsIgnoreCase(md5))
            {
                System.out.println("MD5验证通过");
                return true;
            }
            return false;
        }

    /**
     * 32位MD5加密的大写字符串
     *
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
