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

public class SubmitToGoLiveTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_submit_to_go_live(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_submit_to_go_live(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_submit_to_go_live(new ChromeDriver());
  }

  private void test_submit_to_go_live(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Dashboard
    CStudioSeleniumUtil.navigate_to_dashboard(driver);

    // Edit 'index' page
    CStudioSeleniumUtil.edit_index_page(driver);

    // Switch to 'Entry' window
    CStudioSeleniumUtil.switch_to_window(driver, "Entry");

    // Change title
    WebElement element = driver.findElement(By.id("accordion$pageSettings$internalTitle$input-counted$xforms-input-1"));
    element.clear();
    element.sendKeys("Acme Home - Updated");

    // Save changes
    CStudioSeleniumUtil.click_on(driver, By.id("formSaveButton"));

    // Wait for page to refresh
    new WebDriverWait(driver, CStudioSeleniumUtil.LONG_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.getWindowHandles().size() == 1;
      }
    });

    // Switch to 'Crafter Studio' window
    CStudioSeleniumUtil.switch_to_window(driver, "Crafter Studio");

    // Check updated item
    CStudioSeleniumUtil.click_on(driver, By.id("component-1-4-/site/website/index.xml"));

    // Open confirm 'Submit to Go Live' dialog
    CStudioSeleniumUtil.click_on(driver, By.xpath("//a[text()='Submit to Go Live']"));

    // Confirm submission (on dialog)
    CStudioSeleniumUtil.click_on(driver, By.id("golivesubmitButton"));

    // Close dialog
    CStudioSeleniumUtil.click_on(driver, By.id("acnOKButton"));

    // As admin user in separate browser (or log-out and log in), refresh dashboard
    CStudioSeleniumUtil.logout_from_cs(driver);
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.ADMIN_USER, CStudioSeleniumUtil.ADMIN_PASSWORD, true);

    // Navigate to Dashboard
    CStudioSeleniumUtil.navigate_to_dashboard(driver);

    // Click on 'Show "In-Progress" Items'
    CStudioSeleniumUtil.click_on(driver, By.id("widget-expand-state-component-1-1"));

    // Wait for In-Progress items to load
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.id("/site/website/index.xml"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
