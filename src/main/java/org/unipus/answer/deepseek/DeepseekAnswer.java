package org.unipus.answer.deepseek;

import org.unipus.answer.Answer;
import org.unipus.unipus.QuestionType;

public class DeepseekAnswer extends Answer {

    protected static final String type = "Deepseek";

    public DeepseekAnswer(String APIKey) {
        super(APIKey);
    }

    @Override
    public String send(String chat) {
        return send("You are a helpful assistant.", chat);
    }

    @Override
    public String send(String systemPrompt, String chat) {
        return new DeepseekRequest("deepseek-chat", 2048, "json_object", 0)
                .askDeepseek(systemPrompt, chat, this.APIKey).getContent();
    }

    @Override
    public String getAnswer(String question, QuestionType type) {
        return send("You are now a problem-solving expert. Below are multiple questions that require answers.\n" +
                        "For each question, determine the correct answer, and output all answers as a JSON object with the format \"question_number: answer\".\n" +
                        "\n" +
                        "If a question is to choose the correct option, only output the correct option like A, B, C and D. Do not output any of option contents." +
                        "If a question has multiple points in its solution, combine those points into a single plain text string. Do not nest any additional JSON structures inside the value.\n" +
                        "\n" +
                        "When outputting, you must strictly follow these rules:\n" +
                        "1. Only output the JSON object. Do not include any other words, hints, explanations, thought processes, or commentary.\n" +
                        "2. The keys of the JSON object are the question numbers, and the values are the complete answers (including multiple points if needed) as one plain text string.\n" +
                        "3. Do not add any content unrelated to the answers.\n" +
                        "4. ***Only use ASCII characters (codes 32 to 126). Do not use any non-ASCII punctuation, text, or symbols.***\n" +
                        "   - Replace any intended em dash or en dash with the ASCII hyphen (\"-\").\n" +
                        "   - Use only straight quotes for quotation marks (\" or ').\n" +
                        "   - If you need an ellipsis, use three dots (\"...\").\n" +
                        "\n" +
                        "Ensure that any time you might use a special character, it is replaced with the appropriate ASCII equivalent. "
                ,question);
    }
}
