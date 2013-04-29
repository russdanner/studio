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

public class GroupCheckboxTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_group_checkbox(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_group_checkbox(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_group_checkbox(new ChromeDriver());
  }

  private void test_group_checkbox(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_group_checkboxes(driver);

    // Wait until last widget is rendered
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.tagName("body")).getText().contains("Item Group 8");
      }
    });

    // Test 'Expand All'
    CStudioSeleniumUtil.click_on(driver, By.id("xf-6$xf-20$roles$xf-220"));

    // Test required fields
    CStudioSeleniumUtil.click_on(driver, By.id("xf-6$xf-20$roles$booleanInput$$e0·8-3"));
    CStudioSeleniumUtil.click_on(driver, By.linkText("Group Checkboxes under Repeated-group Test"));
    WebElement element = driver.findElement(By.id("xf-6$xf-21$xf-274$xf-455$input-counted$xforms-input-1·1"));
    element.clear();
    element.sendKeys("Test 123");

    // Wait until green mark is rendered
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("xf-6$xf-21$xf-264"));
        return e != null && e.getAttribute("class").contains("cstudio-xforms-accordion-section-alert-inactive");
      }
    });

    // Validate help icon
    CStudioSeleniumUtil.click_on(driver, By.id("xf-6$xf-20$roles$xf-229$$i"));
    element = driver.findElement(By.id("yui-gen5_c"));
    assertTrue(element.isDisplayed());

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
