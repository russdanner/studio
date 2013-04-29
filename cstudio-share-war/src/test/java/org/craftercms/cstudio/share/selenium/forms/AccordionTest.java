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

import static org.junit.Assert.*;

public class AccordionTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_accordion(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_accordion(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_accordion(new ChromeDriver());
  }

  private void test_accordion(WebDriver driver) {
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

    // Expand/Collapse Single Widgets
    WebElement element = driver.findElement(By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[4]/span/div[2]/ul/div/span/span[2]/li/div/div/span"));
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-close"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-close"));

    // Expand/Collapse Widgets Relevance
    element = driver.findElement(By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[4]/span/div[2]/ul/div[2]/span/span[2]/li/div/div/span"));
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-close"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));

    // Expand/Collapse Widgets Repeat
    element = driver.findElement(By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[4]/span/div[2]/ul/div[3]/span/span[2]/li/div/div/span"));
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-close"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));

    // Expand/Collapse Readonly widgets
    element = driver.findElement(By.xpath("/html/body/div/div/div/div/div/div[2]/form/div[4]/span/div[2]/ul/div[4]/span/span[2]/li/div/div/span"));
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-close"));
    element.click();
    assertTrue(element.getAttribute("class").contains("cstudio-xforms-accordion-section-open"));


    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
