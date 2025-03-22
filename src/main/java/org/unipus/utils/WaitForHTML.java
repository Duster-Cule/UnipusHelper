package org.unipus.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitForHTML {
    public static void waitForBackgroundJavaScript(WebDriver driver, int time){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(time));
        wait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
        );
    }

    public static WebElement waitForFindElementAppear(WebDriver driver, int time, WebElement elements){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(time));
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements)).getFirst();
    }

    public static WebElement waitForFindElementAppear(WebDriver driver, int time, By by){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(time));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public static List<WebElement> waitForFindElementsAppear(WebDriver client, int time, By by) {
        WebDriverWait wait = new WebDriverWait(client, Duration.ofMillis(time));
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }
}
