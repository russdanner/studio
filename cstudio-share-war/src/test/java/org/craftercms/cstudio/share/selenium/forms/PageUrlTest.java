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

public class PageUrlTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_page_url(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_page_url(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_page_url(new ChromeDriver());
  }

  private void test_page_url(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_page_url(driver);

    // Wait until last widget is rendered
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("xf-4$xf-24$ContentExistingFormNameId$xf-242"));

    // Test 'Page URL' field
    // Validate green required mark
    WebElement required = driver.findElement(By.id("xf-4$xf-24$ContentFormNameId$page-url$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-active"));

    // Try wrong url characters
    WebElement url = driver.findElement(By.id("xf-4$xf-24$ContentFormNameId$page-url$xforms-input-1"));
    url.sendKeys("+= @#$%");
    assertTrue(url.getAttribute("value").equals(""));

    // Try a well-formed URL page
    url.clear();
    url.sendKeys("well-formed-url-page");
    assertTrue(url.getAttribute("value").equals("well-formed-url-page"));

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("xf-4$xf-24$ContentFormNameId$xf-217"));
        WebElement r = d.findElement(By.id("xf-4$xf-24$ContentFormNameId$page-url$$a"));
        return e != null && e.getText().equals("20 / 50") && r != null && r.getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    WebElement counter = driver.findElement(By.id("xf-4$xf-24$ContentFormNameId$xf-217"));
    assertTrue(counter.getText().equals("20 / 50"));

    // Test 'Pre populated Page URL' field
    // Validate green required mark
    required = driver.findElement(By.id("xf-4$xf-24$ContentExistingFormNameId$page-url$$a"));
    assertTrue(required.getAttribute("class").contains("xforms-alert-inactive"));

    // Try wrong url characters
    url = driver.findElement(By.id("xf-4$xf-24$ContentExistingFormNameId$page-url$xforms-input-1"));
    assertTrue(url.getAttribute("value").equals("index"));
    url.clear();
    url.sendKeys("+= @#$%");
    assertTrue(url.getAttribute("value").equals(""));

    // Try a well-formed url page
    url.sendKeys("well-formed-url-page2");
    assertTrue(url.getAttribute("value").equals("well-formed-url-page2"));

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("xf-4$xf-24$ContentExistingFormNameId$xf-242"));
        WebElement r = d.findElement(By.id("xf-4$xf-24$ContentExistingFormNameId$page-url$$a"));
        return e != null && e.getText().equals("21 / 50") && r != null && r.getAttribute("class").contains("xforms-alert-inactive");
      }
    });

    counter = driver.findElement(By.id("xf-4$xf-24$ContentExistingFormNameId$xf-242"));
    assertTrue(counter.getText().equals("21 / 50"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
