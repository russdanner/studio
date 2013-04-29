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

import javax.annotation.*;
import java.text.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DateTimeTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_date_time(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_date_time(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_date_time(new ChromeDriver());
  }

  private void test_date_time(WebDriver driver) {
    DateFormat df = new SimpleDateFormat("M/dd/yyyy");
    String date = df.format(new Date());

    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_date_time_widget(driver);

    // Wait until last widget is rendered
    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.tagName("body")).getText().contains("Date Required - Time Optional:");
      }
    });

    // Test 'Date' widget
    WebElement element = driver.findElement(By.id("xf-6$xf-13$xf-32$date$xf-227$xforms-input-1"));
    element.click();
    WebElement calendar = driver.findElement(By.id("orbeon-calendar-div"));
    assertTrue(calendar.isDisplayed());
    element.sendKeys(date);
    assertEquals(date, element.getAttribute("value"));

    // Test 'Date Time' widget
    element = driver.findElement(By.id("xf-6$xf-13$xf-33$date$xf-296$xforms-input-1"));
    element.click();
    assertTrue(calendar.isDisplayed());
    element.sendKeys(date);
    assertEquals(date, element.getAttribute("value"));
    element = driver.findElement(By.id("xf-6$xf-13$xf-33$date$xf-296$xforms-input-2"));
    element.sendKeys("10:00:00 a.m.");
    assertEquals("10:00:00 a.m.", element.getAttribute("value"));

    // Test 'Time' widget
    element = driver.findElement(By.id("xf-6$xf-13$xf-34$date$xf-365$xforms-input-1"));
    element.sendKeys("10:00:00 a.m.");
    assertEquals("10:00:00 a.m.", element.getAttribute("value"));

    // Test 'Date with mindate' widget
    element = driver.findElement(By.id("xf-6$xf-13$xf-35$date$xf-434$xforms-input-1"));
    element.click();
    assertTrue(calendar.isDisplayed());
    element.sendKeys(date);
    assertEquals(date, element.getAttribute("value"));

    // Test 'Date with maxdate' widget
    element = driver.findElement(By.id("xf-6$xf-13$xf-36$date$xf-503$xforms-input-1"));
    element.click();
    assertTrue(calendar.isDisplayed());
    element.sendKeys(date);
    assertEquals(date, element.getAttribute("value"));

    // Test 'Date Required - Time Optional' widget
    element = driver.findElement(By.id("xf-6$xf-13$xf-37$date$xf-575$xforms-input-1"));
    element.click();
    assertTrue(calendar.isDisplayed());
    element.sendKeys(date);
    assertEquals(date, element.getAttribute("value"));
    element = driver.findElement(By.id("xf-6$xf-13$xf-37$time$xf-585$xforms-input-1"));
    element.sendKeys("10:00:00 a.m.");
    assertEquals("10:00:00 a.m.", element.getAttribute("value"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
