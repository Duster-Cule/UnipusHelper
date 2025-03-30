package org.unipus.answer.ollama;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OllamaRequest {

    private static final String POSTURL = "/api/chat";

    private String address = "127.0.0.1";
    private int port = 11434;
    private String model;
    private boolean enableJSON = false;
    private float temprature = 0;
    private int num_ctx = 65535;
    private int num_predict = 4096;

    public OllamaRequest(String address, int port, String model, float temprature, boolean enableJSON) {
        this.address = address;
        this.port = port;
        this.temprature = temprature;
        this.enableJSON = enableJSON;
        this.model = model;
    }

    public OllamaRequest(String address, int port, String model, boolean enableJSON) {
        this.address = address;
        this.port = port;
        this.enableJSON = enableJSON;
        this.model = model;
    }

    public OllamaResponse askOllama(String systemPrompt, String userPrompt) {
        return askOllama(systemPrompt, userPrompt,"http://"+this.address+":"+this.port+POSTURL, this.model, this.enableJSON, this.temprature, this.num_ctx, this.num_predict);
    }

    public static OllamaResponse askOllama(String systemPrompt, String userPrompt, String POSTURL, String model, boolean enableJSON, float temprature, int num_ctx, int num_predict) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.MINUTES)  // 增加连接超时
                .readTimeout(5, TimeUnit.MINUTES)     // 增加读取超时
                .writeTimeout(5, TimeUnit.MINUTES)    // 增加写入超时
                .build();
        RequestBody body = generateRequestBody(systemPrompt, userPrompt, model, enableJSON, temprature, num_ctx, num_predict);
        Request request = new Request.Builder()
                .url(POSTURL)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            System.out.println("ollama回复：" + responseString);
            return new Gson().fromJson(responseString, OllamaResponse.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestBody generateRequestBody(String systemPrompt, String userPrompt, String model, boolean enableJSON, float temprature, int num_ctx, int num_predict) {
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
        Map<String, Object> optionsMap = new HashMap<>();
        optionsMap.put("temperature", temprature);
        optionsMap.put("num_ctx", num_ctx);
        optionsMap.put("num_predict", num_predict);
        payloadMap.put("options", optionsMap);
        if (enableJSON) payloadMap.put("format", "json");
        payloadMap.put("stream", false);

        String jsonPayload = new Gson().toJson(payloadMap);
        MediaType mediaType = MediaType.parse("application/json");

        return RequestBody.Companion.create(jsonPayload, mediaType);
    }
}
