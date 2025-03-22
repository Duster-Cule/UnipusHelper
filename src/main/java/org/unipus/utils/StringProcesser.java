package org.unipus.utils;

public class StringProcesser {

    public static String processIlligalCharacter(String string) {
        // 定义允许的字符集（确保转义正确）：
        String y = "\"a-zA-ZёЁÀ-ÿ0-9一-龥Ѐ–ӿ\uFF00-\uFFEF\uFFF0-\uFFFF　 ⼀-\u2FDF㐀-䶵豈-\uFAFF\uE863가-\uD7AFᄀ-ᇿ\u3130-\u318Fꥠ-\uA97Fힰ-\uD7FF\u3040-ゟ゠-ヿㇰ-ㇿĀ-ſ\u0080-ÿḀ-ỿ„€「」『』々ヽヾ[\\\\],.{}()<>?/\\\\|`~\\\"'@#$%&*《》（）+=_￥¥。，、;:：；【】…？！!”“’‘•·\\r\\n\\t\\\\\\\\-\\\\s\"";

        String regex = "[^" + y + "]";

        String input = "极简主义的生活方式——不仅仅是摆脱物质财产，\n它让你专注于家庭、朋友等真正重要的事情。";

        return input.replaceAll(regex, "");
    }

}
