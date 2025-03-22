package org.unipus.answer;

import org.unipus.unipus.QuestionType;

abstract public class Answer {
    protected String system, prompt = "";

    protected static final String type = "default";

    protected String APIKey;

    public Answer(String APIKey) {
        this.APIKey = APIKey;
    }

    abstract public String send(String chat);

    abstract public String send(String systemPrompt, String chat);

    abstract public String getAnswer(String question, QuestionType type);

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
