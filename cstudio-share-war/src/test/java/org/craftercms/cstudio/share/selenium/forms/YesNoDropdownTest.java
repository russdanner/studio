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

public class YesNoDropdownTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_yes_no_dropdown(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_yes_no_dropdown(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_yes_no_dropdown(new ChromeDriver());
  }

  private void test_yes_no_dropdown(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_yes_no_dropdown(driver);

    // Test 'Status' field
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("xf-3$xf-23$status-enabledisable-req$xf-205"));
    WebElement element = driver.findElement(By.id("xf-3$xf-23$status-enabledisable$xf-196"));
    Select languages = new Select(element);
    assertTrue(languages.getFirstSelectedOption().getText().equals("Yes"));
    languages.selectByVisibleText("No");
    assertTrue(languages.getFirstSelectedOption().getText().equals("No"));

    // Test 'Status Required' field
    element = driver.findElement(By.id("xf-3$xf-23$status-enabledisable-req$xf-205"));
    languages = new Select(element);
    assertTrue(languages.getFirstSelectedOption().getText().equals("Yes"));
    languages.selectByVisibleText("No");
    assertTrue(languages.getFirstSelectedOption().getText().equals("No"));

    // Test green mark
    element = driver.findElement(By.id("xf-3$xf-23$status-enabledisable-req$xf-205$$a"));
    assertTrue(element.getAttribute("class").contains("xforms-alert-inactive"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
