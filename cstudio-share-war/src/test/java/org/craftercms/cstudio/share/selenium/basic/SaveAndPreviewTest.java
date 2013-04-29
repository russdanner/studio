/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.share.selenium.basic;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.support.ui.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SaveAndPreviewTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_save_and_preview(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_save_and_preview(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_save_and_preview(new ChromeDriver());
  }

  private void test_save_and_preview(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.MANAGER_USER, CStudioSeleniumUtil.MANAGER_PASSWORD, true);

    // Navigate to Dashboard
    CStudioSeleniumUtil.navigate_to_dashboard(driver);

    // Edit 'index' page
    CStudioSeleniumUtil.edit_index_page(driver);

    // Switch to 'Entry' window
    CStudioSeleniumUtil.switch_to_window(driver, "Entry");

    // Open 'Sliders'
    WebElement element = driver.findElement(By.linkText("Sliders"));
    element.click();

    // Change slider caption
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("accordion$sliders$xf-567$caption$textarea-counted·1"));
    element = driver.findElement(By.id("accordion$sliders$xf-567$caption$textarea-counted·1"));
    element.clear();
    element.sendKeys("To remain competitive and functioning at peak efficiency, organizations like " +
      "yours can't afford to waste valuable resources on outdated, inefficient, or improperly<br/>" +
      "implemented communications equipment. - Updated");

    // Preview changes
    element = driver.findElement(By.id("formPreviewButton"));
    element.click();

    // Switch to 'Crafter Studio' window
    CStudioSeleniumUtil.switch_to_window(driver, "Crafter Studio");

    // Wait for the preview to load
    new WebDriverWait(driver, CStudioSeleniumUtil.LONG_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.getTitle().startsWith("Acme Demo");
      }
    });

    // Verify changes
    assertTrue(driver.getTitle().startsWith("Acme Demo"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
