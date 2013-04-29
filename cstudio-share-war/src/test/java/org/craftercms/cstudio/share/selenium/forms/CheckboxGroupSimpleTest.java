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

import java.util.*;

import static org.junit.Assert.*;

public class CheckboxGroupSimpleTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_checkbox_group_simple(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_checkbox_group_simple(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_checkbox_group_simple(new ChromeDriver());
  }

  private void test_checkbox_group_simple(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_checkbox_group_simple_widget(driver);

    // Wait until checkboxes are rendered
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.tagName("body")).getText().contains("From the Inside");
      }
    });

    // Verify checkboxes are working fine
    List<WebElement> elements = driver.findElements(By.className("cstudio-xforms-checkbox-simple-checkbox"));
    assertEquals(6, elements.size());
    for (WebElement e : elements) {
      e.click();
      assertTrue(e.isSelected());
      e.click();
      assertFalse(e.isSelected());
    }

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
