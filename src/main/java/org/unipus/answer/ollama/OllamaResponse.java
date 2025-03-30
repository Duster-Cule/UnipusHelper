package org.unipus.answer.ollama;

import com.google.gson.annotations.SerializedName;

public class OllamaResponse {
    private String model;

    @SerializedName("created_at")
    private String createdAt;

    private Message message;

    @SerializedName("done_reason")
    private String doneReason;

    private boolean done;

    @SerializedName("total_duration")
    private long totalDuration;

    @SerializedName("load_duration")
    private long loadDuration;

    @SerializedName("prompt_eval_count")
    private int promptEvalCount;

    @SerializedName("prompt_eval_duration")
    private long promptEvalDuration;

    @SerializedName("eval_count")
    private int evalCount;

    @SerializedName("eval_duration")
    private long evalDuration;

    public static class Message {
        private String role;
        private String content;
    }

    /**
     * @return LLM反馈的所有内容，**包括思考过程**
     */
    public String getContent() {
        return this.message.content;
    }
}
