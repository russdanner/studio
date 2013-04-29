/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.cstudio.share.selenium.forms;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.support.ui.*;
import org.craftercms.cstudio.share.selenium.basic.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InputCountedTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_input_counted(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_input_counted(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_input_counted(new ChromeDriver());
  }

  private void test_input_counted(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_input_counted(driver);

    // Wait until last widget is rendered
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.tagName("body")).getText().contains("Required Populated");
      }
    });

    // Single Widgets

    // Test 'Not Required Empty' field
    WebElement counter = driver.findElement(By.id("xf-16$xf-36$notRequiredEmpty-counter"));
    assertTrue(counter.getText().equals("0"));

    WebElement input = driver.findElement(By.id("xf-16$xf-36$notRequiredEmpty$input-counted$xforms-input-1"));
    input.sendKeys("123");

    assertTrue(counter.getText().equals("3"));

    // Test 'Required Empty' field
    counter = driver.findElement(By.id("xf-16$xf-36$requiredEmpty-counter"));
    assertTrue(counter.getText().equals("0"));

    WebElement required = driver.findElement(By.id("xf-16$xf-36$requiredEmpty$input-counted$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    input = driver.findElement(By.id("xf-16$xf-36$requiredEmpty$input-counted$xforms-input-1"));
    input.sendKeys("123");

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-36$requiredEmpty$input-counted$$a")).getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    assertTrue(counter.getText().equals("3"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Test 'Not Required Populated' field
    counter = driver.findElement(By.id("xf-16$xf-36$notRequiredPopulated-counter"));
    assertTrue(counter.getText().equals("21"));

    input = driver.findElement(By.id("xf-16$xf-36$notRequiredPopulated$input-counted$xforms-input-1"));
    input.clear();
    input.sendKeys("123");

    assertTrue(counter.getText().equals("3"));

    // Test 'Required Populated' field
    counter = driver.findElement(By.id("xf-16$xf-36$requiredPopulated-counter"));
    assertTrue(counter.getText().equals("21"));

    required = driver.findElement(By.id("xf-16$xf-36$requiredPopulated$input-counted$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    input = driver.findElement(By.id("xf-16$xf-36$requiredPopulated$input-counted$xforms-input-1"));
    input.clear();

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-36$requiredPopulated$input-counted$$a")).getAttribute("class").contains("xforms-alert-active");
      }
    });

    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    input.sendKeys("123");

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-36$requiredPopulated$input-counted$$a")).getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    assertTrue(counter.getText().equals("3"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Widgets Relevance
    CStudioSeleniumUtil.click_on(driver, By.linkText("Widgets Relevance"));
    CStudioSeleniumUtil.click_on(driver, By.id("xf-16$xf-37$xf-265$$e0"));

    // Test 'Relevant Required' field
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("xf-16$xf-37$xf-267-counter"));
    counter = driver.findElement(By.id("xf-16$xf-37$xf-267-counter"));
    assertTrue(counter.getText().equals("0"));

    required = driver.findElement(By.id("xf-16$xf-37$xf-267$input-counted$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    input = driver.findElement(By.id("xf-16$xf-37$xf-267$input-counted$xforms-input-1"));
    input.sendKeys("123");

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-37$xf-267$input-counted$$a")).getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    assertTrue(counter.getText().equals("3"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Test 'Relevant Not Required' field
    counter = driver.findElement(By.id("xf-16$xf-37$xf-268-counter"));
    assertTrue(counter.getText().equals("0"));

    required = driver.findElement(By.id("xf-16$xf-37$xf-268$input-counted$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    input = driver.findElement(By.id("xf-16$xf-37$xf-268$input-counted$xforms-input-1"));
    input.sendKeys("123");

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-37$xf-268$input-counted$$a")).getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    assertTrue(counter.getText().equals("3"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Widgets repeat
    CStudioSeleniumUtil.click_on(driver, By.linkText("Widgets Repeat"));

    // Test 'Not Required Empty' field
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("xf-16$xf-38$xf-458$xf-639·1-counter"));
    counter = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-639·1-counter"));
    assertTrue(counter.getText().equals("0"));

    input = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-639$input-counted$xforms-input-1·1"));
    input.sendKeys("123");

    assertTrue(counter.getText().equals("3"));

    // Test 'Required Empty' field
    counter = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-640·1-counter"));
    assertTrue(counter.getText().equals("0"));

    required = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-640$input-counted$$a·1"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    input = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-640$input-counted$xforms-input-1·1"));
    input.sendKeys("123");

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-38$xf-458$xf-640$input-counted$$a·1")).getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    assertTrue(counter.getText().equals("3"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Test 'Not Required Populated' field
    counter = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-641·1-counter"));
    assertTrue(counter.getText().equals("21"));

    input = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-641$input-counted$xforms-input-1·1"));
    input.clear();
    input.sendKeys("123");

    assertTrue(counter.getText().equals("3"));

    // Test 'Required Populated' field
    counter = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-642·1-counter"));
    assertTrue(counter.getText().equals("21"));

    required = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-642$input-counted$$a·1"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    input = driver.findElement(By.id("xf-16$xf-38$xf-458$xf-642$input-counted$xforms-input-1·1"));
    input.clear();

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-38$xf-458$xf-642$input-counted$$a·1")).getAttribute("class").contains("xforms-alert-active");
      }
    });

    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    input.sendKeys("123");

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("xf-16$xf-38$xf-458$xf-642$input-counted$$a·1")).getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    assertTrue(counter.getText().equals("3"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Readonly widgets
    CStudioSeleniumUtil.click_on(driver, By.linkText("Readonly widgets"));

    // Test 'Not Required Empty Readonly' field
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("xf-16$xf-39$requiredEmptyReadonly-counter"));
    counter = driver.findElement(By.id("xf-16$xf-39$notRequiredEmptyReadonly-counter"));
    assertTrue(counter.getText().equals("27"));

    input = driver.findElement(By.id("xf-16$xf-39$notRequiredEmptyReadonly$input-counted$xforms-input-1"));
    assertTrue(input.getAttribute("disabled").equals("true"));
    assertTrue(input.getAttribute("value").equals("Readonly Field Not Required"));

    // Test 'Required Empty Readonly' field
    counter = driver.findElement(By.id("xf-16$xf-39$requiredEmptyReadonly-counter"));
    assertTrue(counter.getText().equals("23"));

    required = driver.findElement(By.id("xf-16$xf-39$requiredEmptyReadonly$input-counted$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    input = driver.findElement(By.id("xf-16$xf-39$requiredEmptyReadonly$input-counted$xforms-input-1"));
    assertNotNull(input);
    assertTrue(input.getAttribute("disabled").equals("true"));
    assertTrue(input.getAttribute("value").equals("Readonly Field Required"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
