import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.junit.Test;
import org.unipus.answer.deepseek.DeepseekAnswer;
import org.unipus.answer.deepseek.DeepseekResponse;
import org.unipus.unipus.QuestionType;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class deepseekTest {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)  // 增加连接超时
                .readTimeout(2, TimeUnit.MINUTES)     // 增加读取超时
                .writeTimeout(2, TimeUnit.MINUTES)    // 增加写入超时
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create("{\n  \"messages\": [\n    {\n      \"content\": \"You are a helpful assistant\",\n      \"role\": \"system\"\n    },\n    {\n      \"content\": \"Hi\",\n      \"role\": \"user\"\n    }\n  ],\n  \"model\": \"deepseek-chat\",\n  \"frequency_penalty\": 0,\n  \"max_tokens\": 2048,\n  \"presence_penalty\": 0,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"stop\": null,\n  \"stream\": false,\n  \"stream_options\": null,\n  \"temperature\": 1,\n  \"top_p\": 1,\n  \"tools\": null,\n  \"tool_choice\": \"none\",\n  \"logprobs\": false,\n  \"top_logprobs\": null\n}", mediaType);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer sk-5255995d00a049f0990f75d9fcab9429")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DeepseekResponse convert() {
        String json = "{\n" +
                "    \"id\": \"d4314441-af1c-49c7-9bf3-7d135da706f5\",\n" +
                "    \"object\": \"chat.completion\",\n" +
                "    \"created\": 1742268622,\n" +
                "    \"model\": \"deepseek-chat\",\n" +
                "    \"choices\": [\n" +
                "        {\n" +
                "            \"index\": 0,\n" +
                "            \"message\": {\n" +
                "                \"role\": \"assistant\",\n" +
                "                \"content\": \"{\\n  \\\"1\\\": \\\"collaboration\\\",\\n  \\\"2\\\": \\\"symbolize\\\",\\n  \\\"3\\\": \\\"onward\\\",\\n  \\\"4\\\": \\\"necessitate\\\",\\n  \\\"5\\\": \\\"acknowledge\\\",\\n  \\\"6\\\": \\\"instrumental\\\",\\n  \\\"7\\\": \\\"confidential\\\",\\n  \\\"8\\\": \\\"witness\\\"\\n}\"\n" +
                "            },\n" +
                "            \"logprobs\": null,\n" +
                "            \"finish_reason\": \"stop\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"usage\": {\n" +
                "        \"prompt_tokens\": 325,\n" +
                "        \"completion_tokens\": 72,\n" +
                "        \"total_tokens\": 397,\n" +
                "        \"prompt_tokens_details\": {\n" +
                "            \"cached_tokens\": 320\n" +
                "        },\n" +
                "        \"prompt_cache_hit_tokens\": 320,\n" +
                "        \"prompt_cache_miss_tokens\": 5\n" +
                "    },\n" +
                "    \"system_fingerprint\": \"fp_3a5770e1b4_prod0225\"\n" +
                "}";
        Gson gson = new Gson();
        return gson.fromJson(json, DeepseekResponse.class);
    }

    @Test
    public void testAnswerParse() {
        HashMap<Integer, String> answers = new Gson().fromJson(convert().getContent(), new TypeToken<HashMap<Integer,String>>(){}.getType());
        for (int i=0; i<answers.size(); i++) {
            System.out.println(i+":"+answers.get(i+1));
        }
    }

    @Test
    public void Test() {
        DeepseekAnswer deepseekAnswer = new DeepseekAnswer("sk-5255995d00a049f0990f75d9fcab9429");
        String question ="Directions:Read the following sentences from Text B. Then explain the underlined figurative language with your own words and identify in which way it is used.\n" +
                "1. … with a loud bang and a mushroom cloud of flames, the desert of Lop Nur in northwest China's Xinjiang Uygur Autonomous Region witnessed a new accomplishment for the Chinese people: the first detonation of a Chinese-made atomic bomb. (Para. 1)\n" +
                "2. It was in this year that his instrumental contributions finally came to light and his name was cemented in the history of China's national defense. (Para. 11)";

        String answer = deepseekAnswer.getAnswer(question, QuestionType.SHORTANSWER);
//        String answer = deepseekAnswer.send("你现在是一个解题专家，这里有一些题目需要你回答，请你判断其正确答案。请你[不要]输出除答案外的其他内容，比如对题目的思考、做题思路、评价等等。请将题目答案以[JSON]的形式输出，注意在输出的JSON中以题号：答案的键值对给出。"
//                ,question);
        System.out.println(answer);
    }
}
