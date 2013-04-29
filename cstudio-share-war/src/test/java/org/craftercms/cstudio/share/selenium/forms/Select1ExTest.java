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

public class Select1ExTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_select1_ex(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_select1_ex(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_select1_ex(new ChromeDriver());
  }

  private void test_select1_ex(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_select1_ex(driver);

    // Test 'Not Required Empty' field
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("xf-4$xf-24$xf-45$xf-275"));
    WebElement element = driver.findElement(By.id("xf-4$xf-24$xf-43$xf-214"));
    Select languages = new Select(element);
    languages.selectByVisibleText("English");
    assertTrue(languages.getFirstSelectedOption().getText().equals("English"));

    // Test 'Required Empty' field
    element = driver.findElement(By.id("xf-4$xf-24$xf-44$xf-245"));
    languages = new Select(element);
    languages.selectByVisibleText("English");
    assertTrue(languages.getFirstSelectedOption().getText().equals("English"));
    languages.selectByVisibleText("Select a Language");
    assertTrue(languages.getFirstSelectedOption().getText().equals("Select a Language"));

    // Test 'Not Required, Default value' field
    element = driver.findElement(By.id("xf-4$xf-24$xf-45$xf-275"));
    languages = new Select(element);
    languages.selectByVisibleText("English");
    assertTrue(languages.getFirstSelectedOption().getText().equals("English"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
