package org.unipus;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.unipus.exceptions.LoginException;
import org.unipus.unipus.Learn;
import org.unipus.unipus.Login;
import org.unipus.unipus.Query;
import org.unipus.utils.WaitForHTML;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    /**
     * args:    --showbrowser/--debug   浏览器界面可见
     *          --nopropertiescreate    不创建配置文件
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> startparas = Arrays.stream(args).toList();

        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("unipushelperconfig.properties"));
            if (startparas.contains("--showbrowser") || startparas.contains("--debug")) {
                properties.setProperty("showbrowser", "true");
            }
        } catch (IOException e) {
            if (startparas.contains("--showbrowser") || startparas.contains("--debug")) {
                properties.setProperty("showbrowser", "true");
                Matcher matcher;
                while(true) {
                    System.out.println("请输入浏览器大小（WIDTHxHEIGHT or WIDTH:HEIGHT），默认1920x1080");
                    String size = scanner.nextLine();
                    if("".equals(size)) {
                        properties.setProperty("browser.width", "1920");
                        properties.setProperty("browser.height", "1080");
                        break;
                    }
                    matcher = Pattern.compile("^(\\d+)[xX:](\\d+)$").matcher(size.trim());
                    if(matcher.matches()) {
                        properties.setProperty("browser.width", matcher.group(1));
                        properties.setProperty("browser.height", matcher.group(2));
                        break;
                    } else {
                        System.err.println("无效的尺寸格式: " + size + "，请重新输入");
                    }
                }
            } else {
                properties.setProperty("showbrowser", "false");
                properties.setProperty("browser.width", "1920");
                properties.setProperty("browser.height", "1080");
            }

            System.out.println("请输入手机号/邮箱/用户名：");
            properties.setProperty("username", scanner.nextLine());
            System.out.println("请输入密码：");
            properties.setProperty("password", scanner.nextLine());
            String llm;
            while (true) {
                System.out.println("请输入LLM平台名称（deepseek或ollama）");
                llm = scanner.nextLine().toLowerCase();
                if ("deepseek".equals(llm) || "ollama".equals(llm)) {
                    break;
                } else {
                    System.out.println("输入错误，请重新输入");
                }
            }
            properties.setProperty("LLMPlatform", llm);
            switch (llm) {
                case "deepseek":
                    System.out.println("请输入" + llm + "平台的APIKey：");
                    properties.setProperty("APIKey", scanner.nextLine());
                    break;
                case "ollama":
                    if(!confirmOllamaWarning(scanner)) {
                        System.out.println("取消使用ollama，程序退出");
                        return;
                    }
                    System.out.println("请输入ollama API的地址（不带端口，默认localhost）");
                    properties.setProperty("APIAddress", scanner.nextLine());
                    System.out.println("请输入ollama API的端口（默认11434）");
                    properties.setProperty("APIPort", scanner.nextLine());
                    System.out.println("请输入模型名称（与命令ollama run后的参数一致）");
                    properties.setProperty("model", scanner.nextLine());
            }
            List<String> exceptURLs = new ArrayList<>();
            String exceptURL = "";
            do {
                System.out.println("请输入不想要学习章节的URL（一次一个），空字符串代表输入完成");
                exceptURL = scanner.nextLine();
                exceptURLs.add(exceptURL);
            } while (!"".equals(exceptURL));

            properties.setProperty("exceptURLs", String.join("|", exceptURLs));
            properties.setProperty("learnURLs", "");
            if (!startparas.contains("--nopropertiescreate")) {
                File file = new File("unipushelperconfig.properties");
                try {
                    file.createNewFile();
                    properties.store(new FileOutputStream(file), "");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
        new Main().mainStart(properties, startparas.contains("--debug"));
    }

    public void mainStart(Properties properties, boolean debugmode) {

        String name = properties.getProperty("username", "").trim();
        String password = properties.getProperty("password", "").trim();
        String APIKey = properties.getProperty("APIKey", "").trim();
        String LLMPlatform = properties.getProperty("LLMPlatform", "").trim().toLowerCase();
        if ("ollama".equals(LLMPlatform)) {
            if (!confirmOllamaWarning(new Scanner(System.in))) {
                System.out.println("取消使用ollama，程序退出");
                return;
            }
        }
        String addressValue = properties.getProperty("APIAddress", "localhost").trim();
        String APIAddress = addressValue.isEmpty() ? "localhost" : addressValue;
        String portValue = properties.getProperty("APIPort", "11434").trim();
        int APIPort = portValue.isEmpty() ? 11434 : Integer.parseInt(portValue);
        String model = properties.getProperty("model", "").trim();
        List<String> exceptURLs = Arrays.stream(properties.getProperty("exceptURLs", "").trim().split("\\|")).toList();
        ChromeOptions options = getDefaultChromeOptions();
        options.addArguments("--window-size=" + properties.getProperty("browser.width", "1920").trim() + "," + properties.getProperty("browser.width", "1080").trim());
        if (!properties.getProperty("showbrowser", "false").trim().equals("true")) {
            options.addArguments("--headless");
        }
        WebDriver client = new ChromeDriver(options);
        try {
            client.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            Query query = new Query(client);
            if (new Login(client).login(name, password)) {
                System.out.println("登录成功");
                WaitForHTML.waitForBackgroundJavaScript(client, 10000);
                System.out.println("你好，" + query.QueryName());
            } else throw new LoginException("登录失败，请检查账户密码");

            String learnURLs = properties.getProperty("learnURLs");
            if("".equals(learnURLs)) {
                switch (query.queryCourse()) {
                    case 0: {
                        int unit = 0;
                        do {
                            unit = query.queryQuestion(LLMPlatform, APIAddress, APIPort, model, APIKey, unit, exceptURLs);
                        } while (unit != -1);

                        System.out.println("已完成所有必修点，请自行登录核对。");
                        break;
                    }
                    case 1: {
                        System.out.println("已完成所有必修点，无需继续。");
                        break;
                    }
                    case -1: {
                        break;
                    }
                    default:
                        throw new AssertionError("Unreachable code reached");
                }
            } else {
                for (String learnURL : Arrays.stream(learnURLs.split("\\|")).toList()) {
                    client.get(learnURL);
                    new Learn(client).learn(LLMPlatform, APIAddress, APIPort, model, APIKey, new ArrayList<>());
                    client.get("https://uai.unipus.cn/home");
                }
            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            if(debugmode) printCurrentHtmlDOM(client);
        } finally {
            if(!debugmode) client.quit();
        }

    }

    public static boolean confirmOllamaWarning(Scanner s) {
        System.out.println("检测到您选择了ollama作为LLM平台。警告：如果您是在家用电脑上运行了ollama，您可能需要更换平台。");
        System.out.println("在运行软件之前，请您认真阅读对于ollama平台的警告：https://github.com/Duster-Cule/UnipusHelper/blob/main/src/doc/ollamaWarning.md");
        while (true) {
            System.out.println("您是否仍然使用ollama运行软件？(y/n)");
            String confirm = s.nextLine();
            switch (confirm.toLowerCase()) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    break;
            }
        }
    }

    private static ChromeOptions getDefaultChromeOptions() {
        return new ChromeOptions();
    }

    public static void printCurrentHtmlDOM(WebDriver client) {
        String dom = ((JavascriptExecutor) client)
                .executeScript("return document.documentElement.outerHTML;")
                .toString();
        try {
            Files.writeString(
                    Paths.get("page.html"),
                    dom,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            System.out.println("文件已成功写入page.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}