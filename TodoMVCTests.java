package com.launchable.demo.tests;

import com.launchable.demo.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class TodoMVCTests extends BaseTest {
    private static final String TODO_URL = "https://todomvc.com/examples/react/";
    private WebDriverWait wait;
    
    @BeforeMethod
    public void navigateToTodoMVC() {
        driver.get(TODO_URL);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @Test(priority = 1, groups = {"smoke", "critical"})
    public void testAddSingleTodo() {
        String todoText = "Learn Selenium WebDriver";
        
        WebElement todoInput = driver.findElement(By.className("new-todo"));
        todoInput.sendKeys(todoText);
        todoInput.sendKeys(Keys.ENTER);
        
        WebElement todoItem = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//label[text()='" + todoText + "']")));
        
        Assert.assertTrue(todoItem.isDisplayed());
        
        WebElement todoCount = driver.findElement(By.className("todo-count"));
        Assert.assertTrue(todoCount.getText().contains("1"));
    }
    
    @Test(priority = 2, groups = {"smoke", "critical"})
    public void testAddMultipleTodos() {
        String[] todos = {"Task 1", "Task 2", "Task 3"};
        
        WebElement todoInput = driver.findElement(By.className("new-todo"));
        
        for (String todo : todos) {
            todoInput.clear();
            todoInput.sendKeys(todo);
            todoInput.sendKeys(Keys.ENTER);
        }
        
        List<WebElement> todoItems = driver.findElements(By.cssSelector(".todo-list li"));
        Assert.assertEquals(todoItems.size(), 3);
        
        WebElement todoCount = driver.findElement(By.className("todo-count"));
        Assert.assertTrue(todoCount.getText().contains("3"));
    }
    
    @Test(priority = 3, groups = {"functional"})
    public void testCompleteTodo() {
        String todoText = "Complete this task";
        
        // Add todo
        WebElement todoInput = driver.findElement(By.className("new-todo"));
        todoInput.sendKeys(todoText);
        todoInput.sendKeys(Keys.ENTER);
        
        // Complete todo
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".todo-list li .toggle")));
        checkbox.click();
        
        // Verify completion
        WebElement completedTodo = driver.findElement(By.cssSelector(".todo-list li.completed"));
        Assert.assertTrue(completedTodo.isDisplayed());
        
        WebElement todoCount = driver.findElement(By.className("todo-count"));
        Assert.assertTrue(todoCount.getText().contains("0"));
    }
    
    @Test(priority = 4, groups = {"functional"})
    public void testFilterTodos() {
        // Add multiple todos
        String[] todos = {"Active Task", "Completed Task"};
        WebElement todoInput = driver.findElement(By.className("new-todo"));
        
        for (String todo : todos) {
            todoInput.clear();
            todoInput.sendKeys(todo);
            todoInput.sendKeys(Keys.ENTER);
        }
        
        // Complete one todo
        List<WebElement> checkboxes = driver.findElements(By.cssSelector(".todo-list li .toggle"));
        checkboxes.get(1).click(); // Complete second todo
        
        // Test Active filter
        WebElement activeFilter = driver.findElement(By.linkText("Active"));
        activeFilter.click();
        
        List<WebElement> activeTodos = driver.findElements(By.cssSelector(".todo-list li:not(.completed)"));
        Assert.assertEquals(activeTodos.size(), 1);
        
        // Test Completed filter
        WebElement completedFilter = driver.findElement(By.linkText("Completed"));
        completedFilter.click();
        
        List<WebElement> completedTodos = driver.findElements(By.cssSelector(".todo-list li.completed"));
        Assert.assertEquals(completedTodos.size(), 1);
    }
    
    @Test(priority = 5, groups = {"functional"})
    public void testDeleteTodo() {
        String todoText = "Delete this task";
        
        // Add todo
        WebElement todoInput = driver.findElement(By.className("new-todo"));
        todoInput.sendKeys(todoText);
        todoInput.sendKeys(Keys.ENTER);
        
        // Hover over todo to reveal delete button
        WebElement todoItem = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".todo-list li")));
        
        // Trigger hover and click delete
        WebElement deleteButton = todoItem.findElement(By.className("destroy"));
        ((org.openqa.selenium.interactions.Actions) new org.openqa.selenium.interactions.Actions(driver))
            .moveToElement(todoItem)
            .click(deleteButton)
            .perform();
        
        // Verify todo is deleted
        List<WebElement> remainingTodos = driver.findElements(By.cssSelector(".todo-list li"));
        Assert.assertEquals(remainingTodos.size(), 0);
    }
    
    @Test(priority = 6, groups = {"edge-case"})
    public void testEmptyTodoInput() {
        WebElement todoInput = driver.findElement(By.className("new-todo"));
        todoInput.sendKeys("");
        todoInput.sendKeys(Keys.ENTER);
        
        List<WebElement> todoItems = driver.findElements(By.cssSelector(".todo-list li"));
        Assert.assertEquals(todoItems.size(), 0, "Empty todo should not be added");
    }
}