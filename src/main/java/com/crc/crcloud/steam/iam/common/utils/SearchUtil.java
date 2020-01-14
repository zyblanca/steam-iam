package com.crc.crcloud.steam.iam.common.utils;
import org.springframework.util.StringUtils;

/**
 * 检查查询条件
 */
public class SearchUtil {


    /**
     * 特殊符号进行转换,特殊符号造成查询的数据不准确或者异常
     * \ ==> \\
     * % ==> \%
     * _ ==> \_
     *
     * @param param
     * @return
     */
    public static String likeParam(String param) {
        if (!StringUtils.hasText(param)) {
            return param;
        }
        //暂时不需要判断该内容
//        if(param.length() == 50){
//             //抛出异常
//        }

        char[] array = param.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char a : array) {
            switch (a) {
                case '\\':
                case '%':
                case '_':
                    sb.append("\\").append(a);
                    break;
                default:
                    sb.append(a);
            }
        }
        return sb.toString();
    }


}
