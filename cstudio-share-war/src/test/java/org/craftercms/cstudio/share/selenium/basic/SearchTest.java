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

public class SearchTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_search(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_search(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_search(new ChromeDriver());
  }

  private void test_search(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.ADMIN_USER, CStudioSeleniumUtil.ADMIN_PASSWORD, true);

    // Navigate to Dashboard
    CStudioSeleniumUtil.navigate_to_dashboard(driver);

    // Edit 'index' page
    CStudioSeleniumUtil.edit_index_page(driver);

    // Switch to 'Entry' window
    CStudioSeleniumUtil.switch_to_window(driver, "Entry");

    // Change title
    WebElement element = driver.findElement(By.id("accordion$pageSettings$internalTitle$input-counted$xforms-input-1"));
    element.clear();
    element.sendKeys("Acme Home - XYZ123");

    // Save changes
    CStudioSeleniumUtil.click_on(driver, By.id("formSaveButton"));

    // Switch to 'Crafter Studio' window
    CStudioSeleniumUtil.switch_to_window(driver, "Crafter Studio");

    element = driver.findElement(By.id("acn-searchtext"));
    element.clear();
    element.sendKeys("XYZ123");
    element.sendKeys(Keys.RETURN);
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.LONG_TIMEOUT, By.id("result-select--site-website-index-xml"));

    element = driver.findElement(By.id("acn-searchtext"));
    element.clear();
    element.sendKeys("rewrwr3445wdq23423423qfw");
    element.sendKeys(Keys.RETURN);

    new WebDriverWait(driver, CStudioSeleniumUtil.LONG_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.tagName("body")).getText().contains("Your search returned no results.");
      }
    });

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
