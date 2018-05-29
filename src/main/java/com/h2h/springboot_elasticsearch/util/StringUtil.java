package com.h2h.springboot_elasticsearch.util;


/**
 * Created by 张杰斌 on 2018/5/24.
 */
public class StringUtil {

    public static boolean isBlank(String str){
        if (str == null) {
            return true;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            switch (str.charAt(i)) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    // case '\b':
                    // case '\f':
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isIndexName(String str){
        if(isBlank(str)){
            return false;
        }
        if(str.matches(".*[ \"*\\<,>/?].*")){
            return false;
        }
        return true;
    }

}
