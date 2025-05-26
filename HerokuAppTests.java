package com.launchable.demo.tests;

import com.launchable.demo.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class HerokuAppTests extends BaseTest {
    private static final String HEROKUAPP_BASE_URL = "https://the-internet.herokuapp.com";
    private WebDriverWait wait;
    
    @Test(priority = 1, groups = {"smoke", "auth"})
    public void testBasicAuth() {
        driver.get(HEROKUAPP_BASE_URL + "/basic_auth");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Navigate to basic auth with credentials in URL
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");
        
        WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//p[contains(text(), 'Congratulations')]")));
        Assert.assertTrue(successMessage.isDisplayed());
    }
    
    @Test(priority = 2, groups = {"functional", "alerts"})
    public void testJavaScriptAlerts() {
        driver.get(HEROKUAPP_BASE_URL + "/javascript_alerts");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Test JS Alert
        WebElement jsAlertButton = driver.findElement(By.xpath("//button[text()='Click for JS Alert']"));
        jsAlertButton.click();
        
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        Assert.assertEquals(alert.getText(), "I am a JS Alert");
        alert.accept();
        
        WebElement result = driver.findElement(By.id("result"));
        Assert.assertEquals(result.getText(), "You successfully clicked an alert");
    }
    
    @Test(priority = 3, groups = {"functional", "alerts"})
    public void testJavaScriptConfirm() {
        driver.get(HEROKUAPP_BASE_URL + "/javascript_alerts");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Test JS Confirm - Accept
        WebElement jsConfirmButton = driver.findElement(By.xpath("//button[text()='Click for JS Confirm']"));
        jsConfirmButton.click();
        
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        Assert.assertEquals(alert.getText(), "I am a JS Confirm");
        alert.accept();
        
        WebElement result = driver.findElement(By.id("result"));
        Assert.assertEquals(result.getText(), "You clicked: Ok");
        
        // Test JS Confirm - Dismiss
        jsConfirmButton.click();
        alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.dismiss();
        
        result = driver.findElement(By.id("result"));
        Assert.assertEquals(result.getText(), "You clicked: Cancel");
    }
    
    @Test(priority = 4, groups = {"functional", "alerts"})
    public void testJavaScriptPrompt() {
        driver.get(HEROKUAPP_BASE_URL + "/javascript_alerts");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement jsPromptButton = driver.findElement(By.xpath("//button[text()='Click for JS Prompt']"));
        jsPromptButton.click();
        
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String inputText = "Hello Launchable!";
        alert.sendKeys(inputText);
        alert.accept();
        
        WebElement result = driver.findElement(By.id("result"));
        Assert.assertEquals(result.getText(), "You entered: " + inputText);
    }
    
    @Test(priority = 5, groups = {"functional", "drag-drop"})
    public void testDragAndDrop() {
        driver.get(HEROKUAPP_BASE_URL + "/drag_and_drop");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement sourceElement = driver.findElement(By.id("column-a"));
        WebElement targetElement = driver.findElement(By.id("column-b"));
        
        String initialSourceText = sourceElement.getText();
        String initialTargetText = targetElement.getText();
        
        Actions actions = new Actions(driver);
        actions.dragAndDrop(sourceElement, targetElement).perform();
        
        // Wait for drag and drop to complete
        Thread.sleep(1000);
        
        // Verify elements have switched positions
        Assert.assertEquals(sourceElement.getText(), initialTargetText);
        Assert.assertEquals(targetElement.getText(), initialSourceText);
    }
    
    @Test(priority = 6, groups = {"functional", "dropdown"})
    public void testDropdown() {
        driver.get(HEROKUAPP_BASE_URL + "/dropdown");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement dropdown = driver.findElement(By.id("dropdown"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(dropdown);
        
        // Select Option 1
        select.selectByValue("1");
        Assert.assertEquals(select.getFirstSelectedOption().getText(), "Option 1");
        
        // Select Option 2
        select.selectByValue("2");
        Assert.assertEquals(select.getFirstSelectedOption().getText(), "Option 2");
    }
    
    @Test(priority = 7, groups = {"functional", "checkboxes"})
    public void testCheckboxes() {
        driver.get(HEROKUAPP_BASE_URL + "/checkboxes");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
        
        // First checkbox should be unchecked initially
        Assert.assertFalse(checkboxes.get(0).isSelected());
        
        // Second checkbox should be checked initially
        Assert.assertTrue(checkboxes.get(1).isSelected());
        
        // Toggle first checkbox
        checkboxes.get(0).click();
        Assert.assertTrue(checkboxes.get(0).isSelected());
        
        // Toggle second checkbox
        checkboxes.get(1).click();
        Assert.assertFalse(checkboxes.get(1).isSelected());
    }
    
    @Test(priority = 8, groups = {"functional", "tables"})
    public void testDataTables() {
        driver.get(HEROKUAPP_BASE_URL + "/tables");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Test sortable table
        WebElement table = driver.findElement(By.id("table1"));
        List<WebElement> headers = table.findElements(By.cssSelector("thead th"));
        
        Assert.assertTrue(headers.size() > 0);
        
        // Click on Last Name header to sort
        WebElement lastNameHeader = driver.findElement(By.xpath("//span[text()='Last Name']"));
        lastNameHeader.click();
        
        // Verify table data exists
        List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
        Assert.assertTrue(rows.size() > 0);
    }
    
    @Test(priority = 9, groups = {"functional", "forms"})
    public void testFormAuthentication() {
        driver.get(HEROKUAPP_BASE_URL + "/login");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Valid login
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".flash.success")));
        Assert.assertTrue(successMessage.getText().contains("You logged into a secure area!"));
        
        // Logout
        driver.findElement(By.cssSelector("a[href='/logout']")).click();
        
        WebElement logoutMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".flash.success")));
        Assert.assertTrue(logoutMessage.getText().contains("You logged out of the secure area!"));
    }
    
    @Test(priority = 10, groups = {"functional", "hovers"})
    public void testHovers() {
        driver.get(HEROKUAPP_BASE_URL + "/hovers");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        List<WebElement> figures = driver.findElements(By.cssSelector(".figure"));
        Actions actions = new Actions(driver);
        
        // Hover over first figure
        actions.moveToElement(figures.get(0)).perform();
        
        WebElement caption = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".figure:first-child .figcaption")));
        Assert.assertTrue(caption.isDisplayed());
        Assert.assertTrue(caption.getText().contains("user1"));
    }
    
    @Test(priority = 11, groups = {"edge-case", "invalid-forms"})
    public void testInvalidFormAuthentication() {
        driver.get(HEROKUAPP_BASE_URL + "/login");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Invalid login
        driver.findElement(By.id("username")).sendKeys("invaliduser");
        driver.findElement(By.id("password")).sendKeys("invalidpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".flash.error")));
        Assert.assertTrue(errorMessage.getText().contains("Your username is invalid!"));
    }
}