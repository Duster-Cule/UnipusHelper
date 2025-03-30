package org.unipus.answer;

import org.unipus.unipus.QuestionType;

abstract public class Answer {
    protected String system = "";

    public static final String type = "default";

    protected String APIKey;
    protected String address;
    protected int port;


    public Answer(String APIKey, String system) {
        this.APIKey = APIKey;
        this.system = system;
    }

    public Answer(String address, int port, String system) {
        this.address = address;
        this.port = port;
        this.system = system;
    }

    abstract public String send(String chat);

    abstract public String send(String systemPrompt, String chat);

    abstract public String getAnswer(String question, QuestionType type);

}
