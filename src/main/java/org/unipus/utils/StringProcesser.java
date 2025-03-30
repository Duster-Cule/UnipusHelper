package org.unipus.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class StringProcesser {
    /**
     * U校园中不允许输入特殊字符，本方法将特殊字符转为空格返回。
     * @param string 需处理的字符串
     * @return 处理后的字符串
     */
    public static String processIlligalCharacter(String string) {
        // 定义允许的字符集（确保转义正确）：

        String y = "a-zA-ZёЁÀ-ÿ0-9一-龥Ѐ–ӿ\uFF00-\uFFEF\uFFF0-\uFFFF　 ⼀-\u2FDF㐀-䶵豈-\uFAFF\uE863가-\uD7AFᄀ-ᇿ\u3130-\u318Fꥠ-\uA97Fힰ-\uD7FF\u3040-ゟ゠-ヿㇰ-ㇿĀ-ſ\u0080-ÿḀ-ỿ„€「」『』々ヽヾ\\[],\\.\\{}\\(\\)<>?/\\|`~\"'@#\\$%&*《》（）+=_￥¥。，、;:：；【】…？！!”“’‘•·\n\t\\\\-\\s";

        String regex = "[^" + y + "]" ;

        return string.replaceAll(regex, " ");
    }

    /**
     * 带深度思考的大模型会用<think></think>标签框住思考过程并与输出放在同一属性下。本方法实现了两者分离。
     * 有些大模型在输出过程中，无论prompt怎么写，都会返回以markdown格式包裹的代码块JSON，此时请启用JSON模式，本方法会将JSON外的代码块去除。
     * 若传入的字符串不含深度思考，则直接处理JSON后返回。此时返回数组下标1为空字符串。
     * @param response LLM的回复
     * @param enableJSON 回答是否启用JSON
     * @return 一个String数组，下标0代表输出，下标1代表思考过程
     * @throws IllegalArgumentException 当输入的字符串不合法
     */
    public static String[] seperateThinkAndOutput(String response, boolean enableJSON) throws IllegalArgumentException{
        String[] returnList = new String[2];
        if(response.startsWith("<think>")) {
            String[] responses = response.split("\\n</think>\\n", 2);
            returnList[1] = responses[0].replaceFirst("<think>\\n", "");
            returnList[0] = responses[1];
        } else {
            returnList[0] = response;
            returnList[1] = "";
        }
        if(enableJSON) {
            try {
                returnList[0] = returnList[0].substring(returnList[0].indexOf('{'), returnList[0].lastIndexOf('}')+1);
            } catch (StringIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("输入的字符串为" + response + "不包含JSON！");
            }
            if(!isValidJson(returnList[0])) {
                throw new IllegalArgumentException("输入的字符串为" + response + "，去除代码块有误");
            }
        }
        return returnList;
    }

    public static boolean isValidJson(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

}
