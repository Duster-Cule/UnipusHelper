package org.unipus.answer.deepseek;

import java.util.ArrayList;

public class DeepseekResponse {
    private String id;
    private ArrayList<Choice> choices;
    private int created;
    private String model;
    private String system_fingerprint;
    private String object;

    public String getContent() {
        return this.choices.getFirst().message.content;
    }

    public ArrayList<String> getContents() {
        ArrayList<String> contents = new ArrayList<>();
        for(Choice choice : this.choices) {
            contents.add(choice.message.content);
        }
        return contents;
    }
}

class Choice {
    private String finish_reason;
    private int index;
    Message message;
    Logprobs logprobs;
}

class Message {
    String content;
    String reasoning_content;
    String role;
}

class Logprobs {

}

class Usage {
    private int completion_tokens;
    private int prompt_tokens;
    private int prompt_cache_hit_tokens;
    private int prompt_cache_miss_tokens;
    private int total_tokens;
    Completion_tokens_details completion_tokens_details;

    class Completion_tokens_details {
        private int reasoning_tokens;
    }
}