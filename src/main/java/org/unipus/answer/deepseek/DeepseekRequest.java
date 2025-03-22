package org.unipus.answer.deepseek;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DeepseekRequest {

    private static final String POSTURL = "https://api.deepseek.com/chat/completions";

    /**
     * 以下注释仅说明取值范围，如需要查询变量作用请访问 https://api-docs.deepseek.com/zh-cn/api/create-chat-completion
     */

    //Possible values: [deepseek-chat, deepseek-reasoner]
    private String model = "deepseek-chat";

    //Possible values: >= -2 and <= 2
    private float frequency_penalty = 0;

    //Possible values: > 1
    private int max_tokens = 1024;

    //Possible values: >= -2 and <= 2
    private float presence_penalty = 0;

    //Possible values: [text, json_object]
    private String response_format_type = "json_object";

    //Possible values: <= 2
    private float temperature = 0;

    //Possible values: <= 1
    private float top_p = 1;

    public DeepseekRequest() {
        this("deepseek-chat", 0, 2048, 0, "json_object", 0, 1);
    }

    public DeepseekRequest(String model, int max_tokens, String response_format_type, float temperature) {
        this(model, 0, 2048, 0, response_format_type, temperature, 1);
    }

    public DeepseekRequest(String model, float frequency_penalty, int max_tokens, float presence_penalty, String response_format_type, float temperature, float top_p) {
        this.model = model;
        this.frequency_penalty = frequency_penalty;
        this.max_tokens = max_tokens;
        this.presence_penalty = presence_penalty;
        this.response_format_type = response_format_type;
        this.temperature = temperature;
        this.top_p = top_p;
    }

    public DeepseekResponse askDeepseek(String systemPrompt, String userPrompt, String APIKey) {
        return askDeepseek(systemPrompt, userPrompt, APIKey, this.model, this.frequency_penalty, this.max_tokens, this.presence_penalty, this.response_format_type, this.temperature, this.top_p);
    }

    public static DeepseekResponse askDeepseek(String systemPrompt, String userPrompt, String APIKey, String model, float frequency_penalty, int max_tokens, float presence_penalty, String response_format_type, float temperature, float top_p) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.MINUTES)  // 增加连接超时
                .readTimeout(2, TimeUnit.MINUTES)     // 增加读取超时
                .writeTimeout(2, TimeUnit.MINUTES)    // 增加写入超时
                .build();

        RequestBody body = generateRequestBody(systemPrompt, userPrompt, model, frequency_penalty, max_tokens, presence_penalty, response_format_type, temperature, top_p);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer "+APIKey)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            System.out.println("deepseek回复：" + responseString);
            return new Gson().fromJson(responseString, DeepseekResponse.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestBody generateRequestBody(String systemPrompt, String userPrompt, String model, float frequency_penalty, int max_tokens, float presence_penalty, String response_format_type, float temperature, float top_p) {
        Map<String, Object> payloadMap = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(systemMsg);
        messages.add(userMsg);
        payloadMap.put("messages", messages);
        payloadMap.put("model", model);
        payloadMap.put("frequency_penalty", frequency_penalty);
        payloadMap.put("max_tokens", max_tokens);
        payloadMap.put("presence_penalty", presence_penalty);
        Map<String, String> responseFormat = new HashMap<>();
        responseFormat.put("type", response_format_type);
        payloadMap.put("response_format", responseFormat);
        payloadMap.put("stop", null);
        payloadMap.put("stream", false);
        payloadMap.put("stream_options", null);
        payloadMap.put("temperature", temperature);
        payloadMap.put("top_p", top_p);
        payloadMap.put("tools", null);
        payloadMap.put("tool_choice", "none");
        payloadMap.put("logprobs", false);
        payloadMap.put("top_logprobs", null);

        String jsonPayload = new Gson().toJson(payloadMap);
        MediaType mediaType = MediaType.parse("application/json");

        return RequestBody.Companion.create(jsonPayload, mediaType);
    }
}
