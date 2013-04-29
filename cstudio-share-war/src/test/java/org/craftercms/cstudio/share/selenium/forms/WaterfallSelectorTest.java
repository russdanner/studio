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

public class WaterfallSelectorTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_waterfall_selector(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_waterfall_selector(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_waterfall_selector(new ChromeDriver());
  }

  private void test_waterfall_selector(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_waterfall_selector(driver);

    // Wait until last widget gets rendered
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-43$xf-249-whitebox"));

    // Add 3 items (first 'Add' button)
    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-42$xf-200-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div/div/span/div/span/div/div[2]/div/ul/ul/li/span"));
    CStudioSeleniumUtil.wait_until_not_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-42$xf-200-actionDropdown"));

    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-42$xf-200-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div/div/span/div/span/div/div[2]/div/ul/ul/li[2]/span"));
    CStudioSeleniumUtil.wait_until_not_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-42$xf-200-actionDropdown"));

    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-42$xf-200-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div/div/span/div/span/div/div[2]/div/ul/ul/li[3]/span"));
    CStudioSeleniumUtil.wait_until_not_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-42$xf-200-actionDropdown"));

    // Adding 1 more must fail
    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-42$xf-200-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div/div/span/div/span/div/div[2]/div/ul/ul/li[4]/span"));

    // Popup error message should appear
    WebElement popup = driver.findElement(By.id("cstudio-wcm-popup-div_c"));
    assertTrue(popup.isDisplayed());
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div[3]/div/div[2]/div/div/div/input"));

    // Validate 'required' mark
    WebElement required = driver.findElement(By.id("accordion$waterfallSelector$xf-43$xf-249$xf-251$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("accordion$waterfallSelector$xf-42$xf-200-counter"));
        return e != null && e.getText().equals("3 / 3");
      }
    });

    // Test delete
    WebElement counter = driver.findElement(By.id("accordion$waterfallSelector$xf-42$xf-200-counter"));
    assertTrue(counter.getText().equals("3 / 3"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div/div/span/div/span/div/div[2]/input[2]"));
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("accordion$waterfallSelector$xf-42$xf-200-counter"));
        return e != null && e.getText().equals("2 / 3");
      }
    });

    // Add 3 items (second 'Add' button)
    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-43$xf-249-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div[2]/div/span/div/span/div/div[2]/div/ul/ul/li/span"));
    CStudioSeleniumUtil.wait_until_not_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-43$xf-249-actionDropdown"));

    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-43$xf-249-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div[2]/div/span/div/span/div/div[2]/div/ul/ul/li[2]/span"));
    CStudioSeleniumUtil.wait_until_not_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-43$xf-249-actionDropdown"));

    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-43$xf-249-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div[2]/div/span/div/span/div/div[2]/div/ul/ul/li[3]/span"));
    CStudioSeleniumUtil.wait_until_not_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$waterfallSelector$xf-43$xf-249-actionDropdown"));

    // Adding 1 more must fail
    CStudioSeleniumUtil.click_on(driver, By.id("accordion$waterfallSelector$xf-43$xf-249-addButton"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div[2]/div/span/div/span/div/div[2]/div/ul/ul/li[4]/span"));

    // Popup error message should appear
    popup = driver.findElement(By.id("cstudio-wcm-popup-div_c"));
    assertTrue(popup.isDisplayed());
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div[3]/div/div[2]/div/div/div/input"));

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("accordion$waterfallSelector$xf-43$xf-249-counter"));
        return e != null && e.getText().equals("3 / 3");
      }
    });

    // Test delete
    counter = driver.findElement(By.id("accordion$waterfallSelector$xf-43$xf-249-counter"));
    assertTrue(counter.getText().equals("3 / 3"));
    CStudioSeleniumUtil.click_on(driver, By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[5]/span/div[2]/ul/div/span/span[2]/li/div[3]/div/span/div[2]/div/span/div/span/div/div[2]/input[2]"));
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("accordion$waterfallSelector$xf-43$xf-249-counter"));
        return e != null && e.getText().equals("2 / 3");
      }
    });

    // Validate 'required' mark
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
