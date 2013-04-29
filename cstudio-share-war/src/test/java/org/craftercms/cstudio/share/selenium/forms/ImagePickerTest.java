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

import java.io.*;
import java.net.*;

import static org.junit.Assert.*;

public class ImagePickerTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_image_picker(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_image_picker(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_image_picker(new ChromeDriver());
  }

  private void test_image_picker(WebDriver driver) {
    // Check both files exist
    File f1 = null;
    File f2 = null;
    try {
      ClassLoader cl = ImagePickerTest.class.getClassLoader();
      f1 = new File(cl.getResource("image_picker_1.png").toURI());
      assertTrue(f1.exists());
      f2 = new File(cl.getResource("image_picker_2.png").toURI());
      assertTrue(f2.exists());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    assertNotNull(f1);
    assertNotNull(f2);

    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Widget
    CStudioSeleniumUtil.navigate_to_image_picker_widget(driver);

    // Upload right file size
    CStudioSeleniumUtil.click_on(driver, By.id("xf-4$xf-20$imageSrcAltTextTest-imageButton"));

    // Check popup dialog appears
    WebElement popup = driver.findElement(By.id("cstudio-wcm-popup-div_c"));
    assertTrue(popup.isDisplayed());

    WebElement input = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div/form/input"));
    input.sendKeys(f1.getAbsolutePath());

    WebElement upload = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div/form/input[2]"));
    upload.click();

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("xf-4$xf-20$imageSrcAltTextTest$xf-206$$a"));
        return e != null && e.getAttribute("class").contains("xforms-alert-inactive");
      }
    });
    WebElement mark = driver.findElement(By.id("xf-4$xf-20$imageSrcAltTextTest$xf-206$$a"));
    assertTrue(mark.getAttribute("class").contains("xforms-alert-inactive"));

    // Delete uploaded image
    CStudioSeleniumUtil.click_on(driver, By.id("xf-4$xf-20$imageSrcAltTextTest-deleteButton"));
    WebElement filename = driver.findElement(By.id("xf-4$xf-20$imageSrcAltTextTest$xf-206-filename"));
    assertTrue(filename.getText().equals("250W X 130H"));

    // Upload any image
    CStudioSeleniumUtil.click_on(driver, By.id("xf-4$xf-20$imageSrcTest-imageButton"));

    // Check popup dialog appears
    popup = driver.findElement(By.id("cstudio-wcm-popup-div_c"));
    assertTrue(popup.isDisplayed());

    input = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div/form/input"));
    input.sendKeys(f2.getAbsolutePath());

    upload = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div/form/input[2]"));
    upload.click();

    new WebDriverWait(driver, CStudioSeleniumUtil.SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        WebElement e = d.findElement(By.id("xf-4$xf-20$imageSrcTest$xf-237-filename"));
        return e != null && e.getText().equals("/static-assets/images/image_picker_2.png");
      }
    });

    // Delete uploaded image
    CStudioSeleniumUtil.click_on(driver, By.id("xf-4$xf-20$imageSrcTest-deleteButton"));
    filename = driver.findElement(By.id("xf-4$xf-20$imageSrcTest$xf-237-filename"));
    assertTrue(filename.getText().equals(""));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
