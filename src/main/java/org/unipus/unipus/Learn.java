package org.unipus.unipus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.unipus.answer.deepseek.DeepseekAnswer;
import org.unipus.answer.ollama.OllamaAnswer;
import org.unipus.exceptions.AnswerLogicException;
import org.unipus.utils.StringProcesser;
import org.unipus.utils.WaitForHTML;

import java.util.*;
import java.util.function.Function;

public class Learn {

    WebDriver client;

    public Learn(WebDriver driver) {
        this.client = driver;
    }

    public Boolean learn(String LLMPlatform, String address, int port, String model, String APIKey, List<String> exceptsURL) throws InterruptedException {
        return startLearn(LLMPlatform, address, port, model, APIKey, exceptsURL);
    }

    private Boolean startLearn(String LLMPlatform, String address, int port, String model, String APIKey, List<String> exceptURLs) throws InterruptedException, AnswerLogicException {
        if (!WaitForHTML.waitForWebsiteJumpContainsURL(client, 5000,"ucontent.unipus.cn/_explorationpc_default/pc.html")) {
            throw new WrongThreadException("网址可能有误，请检查跳转。\n 当前地址:" + client.getCurrentUrl() + "需要地址:\n ucontent.unipus.cn/_explorationpc_default/pc.html");
        }

        if (client.getCurrentUrl() == null || exceptURLs == null) {
            return false;
        }
        String currentURL = client.getCurrentUrl();
        if(exceptURLs.stream()
                .filter(p -> p != null && !p.isEmpty())
                .map(String::toLowerCase)
                .anyMatch(currentURL::contains)) return false;

        WaitForHTML.waitForFindElementAppear(client, 10000, By.className("ant-message-success"));

        List<WebElement> iKnow = client.findElements(By.className("iKnow"));
        if (!iKnow.isEmpty()) {
            iKnow.getFirst().click();
        }

        //可能需要优化：index-0b6ce6ac.js中可以获取弹框队列
        List<WebElement> confirmbtn = client.findElements(By.className("ant-btn-primary"));
        while (!confirmbtn.isEmpty()) {
            confirmbtn.getFirst().click();
            //可能需要优化
            Thread.sleep((int) (900 + Math.random() * 100));
            confirmbtn = client.findElements(By.className("ant-btn-primary"));
        }

        QuestionType questionType = Query.queryQuestionType(client);

        System.out.println("当前页面：" + client.findElement(By.className("pc-break-crumb")).getText().replace("\n", " ") + "\n题目类型:" + questionType);

        switch (questionType) {

            case READING: {
                Actions actions = new Actions(client);
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(55000 + (int) (Math.random() * 10000));
                    actions.click();
                    System.out.println("已挂机" + (i + 1) + "分钟");
                }
                break;
            }

//            case WATCHING: {
//                List<WebElement> play;
//                String beforeTimeText, afterTimeText;
//                int xOffset = -50, yOffset = -100;
//                do {
//                    xOffset *= -1;
//                    yOffset *= -1;
//                    Actions actions = new Actions(client);
//                    actions.moveByOffset(xOffset, yOffset);
//
//                    if (!(play = client.findElements(By.xpath("//div[contains(@class, \"anticon-play\")]/svg"))).isEmpty()) {
//                        play.getFirst().click();
//                    }
//                    beforeTimeText = client.findElement(By.className("beforeTimeText")).getText().substring(0, 5);
//                    afterTimeText = client.findElement(By.className("afterTimeText")).getText();
//                    Thread.sleep((int) (900 + Math.random() * 500));
//                } while (!beforeTimeText.equals(afterTimeText));
//                break;
//            }

            case FILLINGBLANKS: {
                List<WebElement> textInputs = client.findElements(By.className("placeholder"));
//                List<String> quesNum = new ArrayList<>();
//                for (int i = 0; i < textInputs.size(); i++) {
//                    quesNum.add(i + ")");
//                }

                //接入LLM

                System.out.println("正在查询答案");
                String question = client.findElement(By.id("main-content")).getText();

                String answer = askquestion(LLMPlatform, address, port, model, APIKey, question, QuestionType.FILLINGBLANKS);

                HashMap<Integer, String> answers = new Gson().fromJson(answer, new TypeToken<HashMap<Integer, String>>() {
                }.getType());
                System.out.println("查询完毕");

                //end LLM

                List<WebElement> inputs = client.findElements(By.xpath("//div[contains(@class, \"input-user-answer\")]/input"));
                ArrayList<String> hints = new ArrayList<>();
                for (WebElement hint : client.findElements(By.xpath("//div[@class=\"component-htmlview\"]/*/*[contains(@style, 'color: #169179')]"))) {
                    hints.add(hint.getText());
                }
                if (answers.size() == inputs.size() && (inputs.size() == hints.size() || hints.isEmpty())) {
                    for (int i = 0; i < hints.size(); i++) {
                        String answer0 = answers.get(i + 1);
                        answer0 = answer0.replace(hints.get(i), "");
                        inputs.get(i).sendKeys(StringProcesser.processIlligalCharacter(answer0));
                    }
                } else
                    throw new AnswerLogicException("题目与答案不符|题目类型：FILLINGBLANKS|题目数量：" + inputs.size() + "|提示数量：" + hints.size() + "|答案数量：" + answers.size());

                Thread.sleep(500);
                client.findElement(By.xpath("//a[@class='btn' and text()='提 交']")).click();
                Thread.sleep(1000);
                if (!client.findElements(By.className("ant-modal-confirm-btns")).isEmpty()) {
                    client.findElement(By.xpath(".//span[normalize-space(.)='确 定']")).click();
                }
                WaitForHTML.waitForFindElementAppear(client, 10000, By.className("ant-message-success"));
                if(!client.findElements(By.className("grade")).isEmpty()) {
                    System.out.println("作答完成，分数：" + client.findElement(By.className("grade")).getText());
                } else System.out.println("作答完成");
                break;
            }

            case CHOOSING: {
                String derictions = client.findElement(By.className("abs-direction")).getText();
                String questions = client.findElement(By.className("layout-reply-container")).getText();
                String material = "";
                if (!client.findElements(By.className("has-material")).isEmpty()) {
                    material = client.findElement(By.className("text-material-wrapper")).getText();
                }
                List<WebElement> choices = client.findElements(By.className("option-wrap"));

                //接入LLM

                System.out.println("正在查询答案");

                String answer = askquestion(LLMPlatform, address, port, model, APIKey, derictions + "\n" + material + "\n" + questions, QuestionType.CHOOSING);

                HashMap<Integer, String> answers = new Gson().fromJson(answer, new TypeToken<HashMap<Integer, String>>() {
                }.getType());
                System.out.println("查询完毕");

                //end LLM

                if (answers.size() == choices.size()) {
                    Function<Character, Character> cast = c -> (char) (c - 'A' + '1');
                    for (int i = 0; i < answers.size(); i++) {
                        choices.get(i).findElement(By.xpath("./div[" + cast.apply(answers.get(i + 1).charAt(0)) + "]")).click();
                    }
                } else
                    throw new AnswerLogicException("题目与答案不符|题目类型：CHOOSING|题目数量：" + choices.size() + "|答案数量：" + answers.size());

                Thread.sleep(500);
                if(!client.findElements(By.xpath("//a[@class='btn' and text()='提 交']")).isEmpty()) {
                    client.findElement(By.xpath("//a[@class='btn' and text()='提 交']")).click();
                    Thread.sleep(1000);
                    if (!client.findElements(By.className("ant-modal-confirm-btns")).isEmpty()) {
                        client.findElement(By.xpath(".//span[normalize-space(.)='确 定']")).click();
                    }
                    WaitForHTML.waitForFindElementAppear(client, 10000, By.className("ant-message-success"));
                    if (!client.findElements(By.className("grade")).isEmpty()) {
                        System.out.println("作答完成，分数：" + client.findElement(By.className("grade")).getText());
                    } else System.out.println("作答完成");
                } else {
                    client.findElement(By.xpath("//a[@class='btn' and text()='下一题']")).click();
                    return startLearn(LLMPlatform, address, port, model, APIKey, exceptURLs);
                }
                break;
            }

            case SHORTANSWER: {
                String derictions = client.findElement(By.className("abs-direction")).getText();
                String questions = client.findElement(By.className("layout-reply-container")).getText();
                String material = "";
                if (!client.findElements(By.className("has-material")).isEmpty()) {
                    material = client.findElement(By.className("layout-material-container")).getText();
                }
                List<WebElement> inputs = client.findElements(By.className("question-inputbox-input"));
                inputs.addAll(client.findElements(By.className("question-textarea-content")));

                //接入LLM

                System.out.println("正在查询答案");

                String answer = askquestion(LLMPlatform, address, port, model, APIKey, derictions + "\n" + material + "\n" + "Questions are below, the number of questions：" + inputs.size() + "\n" + questions, QuestionType.SHORTANSWER);

                HashMap<Integer, String> answers = new Gson().fromJson(answer, new TypeToken<HashMap<Integer, String>>() {
                }.getType());
                System.out.println("查询完毕");

                //end LLM
                if (answers.size() == inputs.size()) {
                    for (int i = 0; i < answers.size(); i++) {
                        inputs.get(i).sendKeys(StringProcesser.processIlligalCharacter(answers.get(i + 1)));
                    }
                } else
                    throw new AnswerLogicException("题目与答案不符|题目类型：SHORTANSWER|题目数量：" + inputs.size() + "|答案数量：" + answers.size());

                Thread.sleep(500);
                client.findElement(By.xpath("//a[@class='btn' and text()='提 交']")).click();
                Thread.sleep(1000);
                if (!client.findElements(By.className("ant-modal-confirm-btns")).isEmpty()) {
                    client.findElement(By.xpath(".//span[normalize-space(.)='确 定']")).click();
                }
                WaitForHTML.waitForFindElementAppear(client, 10000, By.className("ant-message-success"));
                System.out.println("作答完成");
                break;
            }

            case BLANKEDCLOZE: {
                String derictions = client.findElement(By.className("abs-direction")).getText();
                ArrayList<String> options = new ArrayList<>();
                for (WebElement option : client.findElements(By.className("option-wrapper"))) {
                    options.add(option.getText());
                }
                String questions = client.findElement(By.className("question-material-banked-cloze-scoop")).getText();
                List<WebElement> inputs = client.findElements(By.xpath("//div[contains(@class, \"reply-wrapper\")]//input"));

                //接入LLM

                System.out.println("正在查询答案");

                StringBuilder material = new StringBuilder();
                for (String option : options) {
                    material.append(option);
                    material.append(" ");
                }

                String answer = askquestion(LLMPlatform, address, port, model, APIKey, derictions + "\n" + material + "\n" + questions, QuestionType.BLANKEDCLOZE);

                HashMap<Integer, String> answers = new Gson().fromJson(answer, new TypeToken<HashMap<Integer, String>>() {
                }.getType());
                System.out.println("查询完毕");

                //end LLM

                if (inputs.size() == answers.size()) {
                    for (int i = 0; i < inputs.size(); i++) {
                        inputs.get(i).sendKeys(StringProcesser.processIlligalCharacter(answers.get(i + 1)));
                    }
                } else
                    throw new AnswerLogicException("题目与答案不符|题目类型：SHORTANSWER|题目数量：" + inputs.size() + "|答案数量：" + answers.size());

                Thread.sleep(500);
                client.findElement(By.xpath("//a[@class='btn' and text()='提 交']")).click();
                Thread.sleep(1000);
                if (!client.findElements(By.className("ant-modal-confirm-btns")).isEmpty()) {
                    client.findElement(By.xpath(".//span[normalize-space(.)='确 定']")).click();
                }
                WaitForHTML.waitForFindElementAppear(client, 10000, By.className("ant-message-success"));
                System.out.println("作答完成");
                break;
            }

            case REVIEWANDCHECK: {
                for (WebElement checkbox : client.findElements(By.xpath("//tbody[@class=\"ant-table-tbody\"]/tr[position()>1]/td[2]/span"))) {
                    checkbox.click();
                }
                Thread.sleep(2000);
                System.out.println("作答完成");
                break;
            }

            case VOCABULARY: {
                WebElement next = client.findElement(By.xpath("//*[@class=\"action next\"]"));
                while (!Arrays.asList(next.getDomAttribute("class").split(" ")).contains("disabled")) {
                    next.click();
                    Thread.sleep(400 + (int) (Math.random() * 200));
                }
                Thread.sleep(1000);
                break;
            }

            case WATCHING://等待适配
            case LISTENING:
            case COMMENT:
            case MATCH:
            case UNITPROJECT:
                System.out.println("不支持作答的题型，已跳过");
                System.out.println("当前地址：" + client.getCurrentUrl());
                return false;
            case UNKNOWN:
                System.err.println("题目类型未知，请更新题目类型");
                System.out.println("当前地址：" + client.getCurrentUrl());
                return false;
            default:
                throw new AssertionError("Unreachable code reached");
        }
        return true;
    }

    private static String askquestion(String LLMPlatform, String address, int port, String model, String APIKey, String question, QuestionType questionType) {
        switch (LLMPlatform) {
            case "deepseek":
                return new DeepseekAnswer(APIKey).getAnswer(question, questionType);
            case "ollama":
                boolean enableJSON = !model.contains("deepseek-r1");
                return new OllamaAnswer(address, port, model, enableJSON).getAnswer(question, questionType);
            default:
                System.err.println("模型名称错误");
                return "";
        }
    }

}
