package org.unipus.unipus;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.unipus.utils.WaitForHTML;

import java.util.Objects;

public class Login {

    WebDriver client;
    public Login(WebDriver client){
        this.client = client;
    }

    public boolean login(String username, String password){
        client.get("https://uai.unipus.cn/");
        WaitForHTML.waitForBackgroundJavaScript(client, 10000);
        if (Objects.equals(client.getCurrentUrl(), "https://uai.unipus.cn/home")) return true;

        WebElement loginbtn = client.findElements(By.className("index_text_5__7GwmT")).get(1);
        loginbtn.click();
        WaitForHTML.waitForBackgroundJavaScript(client, 10000);

        WebElement usernameText = client.findElement(By.id("username"));
        usernameText.clear();
        usernameText.sendKeys(username);
        WebElement passwordText = client.findElement(By.id("password"));
        passwordText.clear();
        passwordText.sendKeys(password);

        WebElement agreement = client.findElement(By.id("agreement"));
        agreement.click();

        WebElement login = client.findElement(By.className("usso-login-btn"));
        login.click();

        return Objects.equals(client.getCurrentUrl(), "https://uai.unipus.cn/home");
    }

}
