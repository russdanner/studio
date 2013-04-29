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

public class DropdownEnableDisableTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_dropdown(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_dropdown(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_dropdown(new ChromeDriver());
  }

  private void test_dropdown(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_enable_disable_dropdown(driver);

    // Wait until last widget is rendered
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.tagName("body")).getText().contains("Status Required");
      }
    });

    // Test 'Status' widget
    WebElement element = driver.findElement(By.id("xf-3$xf-23$status-enabledisable$xf-196"));
    Select select = new Select(element);
    assertTrue(select.getFirstSelectedOption().getText().equals("Enabled"));

    // Test 'Status Required' widget
    element = driver.findElement(By.id("xf-3$xf-23$status-enabledisable-req$xf-205"));
    select = new Select(element);
    assertTrue(select.getFirstSelectedOption().getText().equals("Enabled"));

    // Test green 'OK' mark
    element = driver.findElement(By.id("xf-3$xf-23$status-enabledisable-req$xf-205$$a"));
    assertTrue(element.getAttribute("class").contains("xforms-alert-inactive"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
