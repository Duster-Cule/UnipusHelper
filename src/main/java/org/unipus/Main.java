package org.unipus;

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
            System.out.println("请输入AI平台的APIKey：");
            properties.setProperty("APIKey", scanner.nextLine());
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

        String name = properties.getProperty("username");
        String password = properties.getProperty("password");
        String APIKey = properties.getProperty("APIKey");
        List<String> exceptURLs = Arrays.stream(properties.getProperty("exceptURLs").split("\\|")).toList();
        ChromeOptions options = getDefaultChromeOptions();
        options.addArguments("--window-size=" + properties.getProperty("browser.width") + "," + properties.getProperty("browser.width"));
        if (properties.getProperty("showbrowser").equals("false")) {
            options.addArguments("--headless");
        }
        WebDriver client = new ChromeDriver(options);
        try {
            client.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            Query query = new Query(client);
            if (new Login(client).login(name, password)) {
                System.out.println("登陆成功");
                WaitForHTML.waitForBackgroundJavaScript(client, 10000);
                System.out.println("你好，" + query.QueryName());
            } else throw new LoginException("登录失败，请检查账户密码");

            String learnURLs = properties.getProperty("learnURLs");
            if("".equals(learnURLs)) {
                switch (query.queryCourse()) {
                    case 0: {
                        int unit = 0;
                        do {
                            unit = query.queryQuestion(APIKey, unit, exceptURLs);
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
                    new Learn(client).learn(APIKey, new ArrayList<>());
                }
            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            if(debugmode) printCurrentHtml(client);
        } finally {
            if(!debugmode) client.quit();
        }

    }

    private static ChromeOptions getDefaultChromeOptions() {
        return new ChromeOptions();
    }

    public void printCurrentHtml(WebDriver client) {
        try {
            Files.writeString(Paths.get("page.html"), client.getCurrentUrl()+"\n");
            Files.writeString(Paths.get("page.html"), client.getPageSource(), StandardOpenOption.APPEND);
            System.out.println("网页内容已成功写入 page.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}