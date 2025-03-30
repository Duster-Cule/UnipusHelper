package org.unipus.answer.ollama;

import org.unipus.answer.Answer;
import org.unipus.unipus.QuestionType;
import org.unipus.utils.StringProcesser;

public class OllamaAnswer extends Answer {

    public static final String type = "Ollama";
    private String model = "";
    private boolean enableJSON = true;

    public OllamaAnswer(String address, int port, String model, boolean enableJSON) {
        super(address, port, """
                You are now a problem-solving expert. Users will send you multiple questions that require answers.The questions the user sends you is divided into two parts: the "Directions" section and the question section. You must strictly follow all the requirements laid out in the "Directions" section when answering the question section. Below are the rules that you need to obey when outputing anwsers.
                
                For each question, determine the correct answer and output all answers **in a single-line JSON object** with the format `"question_number": "answer"`.
                
                **Important:**
                1. If there is only a single question but it contains multiple instructions, treat them as **one** question. Do **not** split them into separate question numbers.
                2. If a question is a multiple-choice type, only output the correct option (A/B/C/D). Do **not** output any of the option contents.
                3. If a question has multiple points or steps in its solution, combine them into one **plain text string**.
                4. **Do not** include any line breaks in the JSON object, except as `\\n` within the string values where necessary. Your final JSON must be **in one single line**.
                5. **Do not** nest any additional JSON structures inside the answer value.
                6. You must output strictly in JSON format. **Do not** include explanations, hints, or any other commentary outside of the JSON object.
                7. The keys of the JSON object are the question numbers (e.g., `"1"`, `"2"`, `"3"`, ...), and the values are the complete answers.
                8. Do **not** add any content unrelated to the answers.
                9.**Do not** output anything other than the answer in JSON format, including inline code, code blocks, etc markdown text. Output plain JSON text only.
                """);
        this.model = model;
        this.enableJSON = enableJSON;
    }

    public OllamaAnswer(String address, int port, String model, String systemPrompt, boolean enableJSON) {
        super(address, port, systemPrompt);
        this.model = model;
        this.enableJSON = enableJSON;
    }

    @Override
    public String send(String chat) {
        return send("You are a helpful assistant.", chat);
    }

    @Override
    public String send(String systemPrompt, String chat) {
        return new OllamaRequest(this.address, this.port, this.model, this.enableJSON).askOllama(systemPrompt, chat).getContent();
    }

    @Override
    public String getAnswer(String question, QuestionType type) {
        return StringProcesser.seperateThinkAndOutput(send(this.system, question),true)[0];
    }
}
