package org.unipus.unipus;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.unipus.exceptions.AnswerLogicException;
import org.unipus.exceptions.WrongRedirectionException;
import org.unipus.utils.WaitForHTML;

import java.time.Duration;
import java.util.*;

public class Query {

    int exceptnum = 0;
    WebDriver client;
    public Query(WebDriver driver){
        this.client = driver;
    }

    public int queryCourse(){
        client.get("https://uai.unipus.cn/home");
        WaitForHTML.waitForBackgroundJavaScript(client, 10000);
        WaitForHTML.waitForFindElementAppear(client, 2000, By.className("ucm-ant-btn-primary")).click();
        List<WebElement> btns = client.findElements(By.className("menu-side_title__lms49"));
        for(WebElement btn : btns) {
            if(Objects.equals(btn.getDomProperty("innerText"), "我的课程")) {
                btn.click();
                break;
            }
        }

        List<WebElement> courseList = WaitForHTML.waitForFindElementsAppear(client, 10000, By.className("slick-list"));
        List<WebElement> courses = new ArrayList<>();
        for (WebElement course : courseList) {
            courses.addAll(course.findElements(By.xpath(".//div[contains(@class, \"slick-active\")]")));
        }
        int courseNum = courses.size();
        switch (courseNum) {
            case 0:
                System.err.println("没有课程可以学习，请检查账户");
                return -1;
            case 1:
                System.out.println("找到1门课程");
                String[] courseInfo = courses.getFirst().getDomProperty("innerText").split("\n");
                System.out.println(courseInfo[0]);
                System.out.println(courseInfo[2]+courseInfo[3]);
                System.out.print(courseInfo[4]);
                if(!client.findElements(By.xpath("//span[contains(@class, \"ant-progress-text\")]/*[contains(@class, \"anticon-check-circle\")]")).isEmpty()) {
                    System.out.println("已完成");
                    return 1;
                } else System.out.println(courseInfo[5]);
                System.out.println("准备开始学习");
                courses.getFirst().click();
                break;
            default:
                System.out.println("共有" + courseNum + "门课程在学习");
                for (int i=0; i<courses.size(); i++) {
                    try {
                        WebElement cours = courses.get(i);
                        System.out.println((i + 1) + ":");
                        String[] courseInfo1 = cours.getDomProperty("innerText").split("\n");
                        System.out.println(courseInfo1[0]);
                        System.out.println(courseInfo1[2] + courseInfo1[3]);
                        System.out.print(courseInfo1[4]);
                        if (!client.findElements(By.xpath("//span[contains(@class, \"ant-progress-text\")]/*[contains(@class, \"anticon-check-circle\")]")).isEmpty()) {
                            System.out.println("已完成");
                        } else System.out.println(courseInfo1[5]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("获取课本信息失败，可能这本书不能学习");
                    }
                    System.out.println("====================================");
                }
                Scanner s = new Scanner(System.in);
                int index = 0;
                do {
                    System.out.println("请在下面输入你要学习的编号");
                    index = s.nextInt();
                }while (!(index>0&&index<=courseNum));
                courses.get(index-1).click();
        }
        return 0;
    }

    public int queryQuestion(String LLMPlatform, String address, int port, String model, String APIKey, int unitsComplete, List<String> exceptURLs) throws InterruptedException, AnswerLogicException, WrongRedirectionException {

        if(!WaitForHTML.waitForWebsiteJumpContainsURL(client, 10000, "uai.unipus.cn/app/cmgt/resource-detail")){
            throw new WrongRedirectionException("网址可能有误，请检查跳转 \n 当前地址:" + client.getCurrentUrl() + "\n 需要地址:uai.unipus.cn/app/cmgt/resource-detail/*");
        }
        WebElement unit = WaitForHTML.waitForFindElementAppear(client, 10000, By.className("unipus-tabs_unitTabScrollContainer__fXBxR"));
        List<WebElement> units = unit.findElements(By.xpath("./*"));
        System.out.println("共"+units.size()+"个单元，当前在第"+ (unitsComplete+1) +"个");
        for (int i=unitsComplete; i<units.size(); i++) {
            WebElement webElement = units.get(i);
            webElement.click();
            new WebDriverWait(client, Duration.ofSeconds(30)).until(
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        }
        int exceptnow = 0;
        for(int i=unitsComplete;i<units.size(); i++){
            units.get(i).click();
            List<WebElement> requiredCourses;
            try {
                requiredCourses = WaitForHTML.waitForFindElementsAppear(client, 1000, By.xpath("//div[contains(@class, \"unipus-tabs_itemActive\")]//*[contains(text(), '必修')]"));
            } catch (TimeoutException e) {
                requiredCourses = new ArrayList<>();
            }
            Iterator<WebElement> iterator = requiredCourses.iterator();
            while (iterator.hasNext()) {
                WebElement course = iterator.next();
                WebElement coursepanel = course.findElement(By.xpath("./../../.."));
                String completeStatus = coursepanel.findElement(By.xpath("./div[2]/div/div[2]")).getDomProperty("innerText");
//                WaitForHTML.waitForFindElementAppear(client, 10000, coursepanel);
                if ("已完成".equals(completeStatus)) {
                    iterator.remove();
                    continue;
                }
                if (exceptnow < this.exceptnum) {
                    exceptnow++;
                    iterator.remove();
                    continue;
                }

                String contentURL = client.getCurrentUrl();

                Actions actions = new Actions(client);
                actions.moveToElement(coursepanel).click().build().perform();

                if (!new Learn(client).learn(LLMPlatform, address, port, model, APIKey, exceptURLs)) {
                    this.exceptnum++;
                }
                client.get(contentURL);
                return unitsComplete;
            }
            System.out.println(units.get(i).getText()+"已完成");
            unitsComplete = i+1;
            exceptnum = 0;
        }
        System.out.println("已跳过"+ this.exceptnum + "个题目");
        return -1;
    }

    public static QuestionType queryQuestionType(WebDriver client) {
        WebElement question_warp = WaitForHTML.waitForFindElementAppear(client, 10000, By.xpath("//div[@class=\"question-wrap\"]"));
        WebElement question = client.findElement(By.xpath("//div[@class=\"question-wrap\"]//div[contains(@class, \"layoutBody-container\")]"));
        List<String> classes = Arrays.asList(question.getDomAttribute("class").split(" "));
        if(classes.contains("has-material")) {
            if(classes.contains("has-reply")) {
                if(!question_warp.findElements(By.cssSelector("div.question-audio")).isEmpty()) return QuestionType.LISTENING;
                if(!question_warp.findElements(By.xpath("//div[@class=\"discussion-cloud-reply\"]")).isEmpty()) return QuestionType.COMMENT;
                if(!question_warp.findElements(By.xpath("//div[@class=\"question-common-abs-choice\"]")).isEmpty()) return QuestionType.CHOOSING;
                if(!question_warp.findElements(By.xpath("//div[@class=\"reply-wrap\"]//div[@class=\"question-inputbox\"]")).isEmpty()) return QuestionType.SHORTANSWER;
                if(!question_warp.findElements(By.xpath("//div[@class=\"question-textarea\"]")).isEmpty()) return QuestionType.SHORTANSWER;
                if(!question_warp.findElements(By.xpath("//div[contains(@class, \"question-abs-material-banked-cloze\")]")).isEmpty()) return QuestionType.BLANKEDCLOZE;
                if(!question_warp.findElements(By.xpath("//div[@class=\"sortable-list-wrapper\"]")).isEmpty()) return QuestionType.MATCH;
                if(!question_warp.findElements(By.xpath("//div[@class=\"question-multi-file-upload\"]")).isEmpty()) return QuestionType.UNITPROJECT;

            } else {
                if(!question_warp.findElements(By.xpath("//div[@class=\"question-rich-text-read\"]")).isEmpty()) return QuestionType.READING;
                if(!question_warp.findElements(By.xpath("//div[@class=\"question-common-abs-material\"]")).isEmpty()) return QuestionType.READING;
            }
        } else if (classes.contains("has-reply")) {
            if(!question_warp.findElements(By.xpath("//div[@class=\"question-common-abs-choice\"]")).isEmpty()) return QuestionType.CHOOSING;
            if(!question_warp.findElements(By.xpath("//div[@class=\"question-inputbox\"]")).isEmpty()) return QuestionType.SHORTANSWER;
            if(!question_warp.findElements(By.xpath("//div[@class=\"item\"]")).isEmpty()) return QuestionType.FILLINGBLANKS;
        } else {
            if(!question_warp.findElements(By.xpath("//div[@class=\"question-rich-text-read\"]")).isEmpty()) return QuestionType.READING;
            if(!question_warp.findElements(By.xpath("//div[@class=\"wrapper\"]")).isEmpty()) return QuestionType.READING;
            if(!question_warp.findElements(By.xpath("//div[@class=\"video\"]")).isEmpty()) return QuestionType.WATCHING;
            if(!question_warp.findElements(By.xpath("//div[@class=\"question-vocabulary\"]")).isEmpty()) return QuestionType.VOCABULARY;
            if(!question_warp.findElements(By.xpath("//div[@class=\"sortable-list-wrapper\"]")).isEmpty()) return QuestionType.MATCH;
            if(!question_warp.findElements(By.xpath("//div[@class=\"ticket-view\"]")).isEmpty()) return QuestionType.REVIEWANDCHECK;
        }
        return QuestionType.UNKNOWN;
    }

    public String QueryName(){
        WebElement name = WaitForHTML.waitForFindElementAppear(client, 10000, By.className("header_avatar__tbjHo"));
        return name.getDomProperty("innerText");
    }
}
