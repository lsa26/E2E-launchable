package com.launchable.demo.tests;

import com.launchable.demo.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class SauceDemoTests extends BaseTest {
    private static final String SAUCE_DEMO_URL = "https://www.saucedemo.com/";
    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    private WebDriverWait wait;
    
    @BeforeMethod
    public void navigateToSauceDemo() {
        driver.get(SAUCE_DEMO_URL);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @Test(priority = 1, groups = {"smoke", "critical"})
    public void testValidLogin() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        WebElement inventoryContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("inventory_container")));
        Assert.assertTrue(inventoryContainer.isDisplayed());
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("inventory.html"));
    }
    
    @Test(priority = 2, groups = {"smoke", "security"})
    public void testInvalidLogin() {
        performLogin("invalid_user", "invalid_password");
        
        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-test='error']")));
        Assert.assertTrue(errorMessage.isDisplayed());
        Assert.assertTrue(errorMessage.getText().contains("Username and password do not match"));
    }
    
    @Test(priority = 3, groups = {"functional", "critical"})
    public void testAddProductToCart() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Add first product to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")));
        addToCartButton.click();
        
        // Verify cart badge
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "1");
        
        // Verify button text changed
        WebElement removeButton = driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-backpack']"));
        Assert.assertTrue(removeButton.isDisplayed());
    }
    
    @Test(priority = 4, groups = {"functional"})
    public void testAddMultipleProductsToCart() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Add multiple products
        List<WebElement> addButtons = driver.findElements(By.cssSelector("[data-test^='add-to-cart']"));
        for (int i = 0; i < Math.min(3, addButtons.size()); i++) {
            addButtons.get(i).click();
        }
        
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "3");
    }
    
    @Test(priority = 5, groups = {"functional"})
    public void testViewCartContents() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Add product to cart
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        
        // Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();
        
        // Verify cart page
        WebElement cartItem = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("cart_item")));
        Assert.assertTrue(cartItem.isDisplayed());
        
        WebElement itemName = driver.findElement(By.className("inventory_item_name"));
        Assert.assertEquals(itemName.getText(), "Sauce Labs Backpack");
    }
    
    @Test(priority = 6, groups = {"functional", "critical"})
    public void testCompleteCheckoutProcess() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Add product and go to cart
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        driver.findElement(By.className("shopping_cart_link")).click();
        
        // Proceed to checkout
        driver.findElement(By.cssSelector("[data-test='checkout']")).click();
        
        // Fill checkout form
        driver.findElement(By.cssSelector("[data-test='firstName']")).sendKeys("John");
        driver.findElement(By.cssSelector("[data-test='lastName']")).sendKeys("Doe");
        driver.findElement(By.cssSelector("[data-test='postalCode']")).sendKeys("12345");
        driver.findElement(By.cssSelector("[data-test='continue']")).click();
        
        // Complete order
        WebElement finishButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-test='finish']")));
        finishButton.click();
        
        // Verify completion
        WebElement confirmationMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("complete-header")));
        Assert.assertEquals(confirmationMessage.getText(), "Thank you for your order!");
    }
    
    @Test(priority = 7, groups = {"functional"})
    public void testProductSorting() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Test sorting by price (low to high)
        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByValue("lohi");
        
        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));
        Assert.assertTrue(prices.size() > 0);
        
        // Verify first price is lower than last price
        String firstPrice = prices.get(0).getText().replace("$", "");
        String lastPrice = prices.get(prices.size() - 1).getText().replace("$", "");
        
        double firstPriceValue = Double.parseDouble(firstPrice);
        double lastPriceValue = Double.parseDouble(lastPrice);
        
        Assert.assertTrue(firstPriceValue <= lastPriceValue);
    }
    
    @Test(priority = 8, groups = {"functional"})
    public void testRemoveProductFromCart() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Add product
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        
        // Remove product
        driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-backpack']")).click();
        
        // Verify cart is empty
        List<WebElement> cartBadge = driver.findElements(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.size(), 0, "Cart should be empty after removing item");
    }
    
    @Test(priority = 9, groups = {"functional"})
    public void testLogout() {
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        
        // Open menu
        driver.findElement(By.id("react-burger-menu-btn")).click();
        
        // Click logout
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("logout_sidebar_link")));
        logoutLink.click();
        
        // Verify return to login page
        WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-test='login-button']")));
        Assert.assertTrue(loginButton.isDisplayed());
    }
    
    private void performLogin(String username, String password) {
        driver.findElement(By.cssSelector("[data-test='username']")).sendKeys(username);
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
    }
}