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

import java.util.*;

import static org.junit.Assert.*;

public class AnalyticsTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_analytics(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_analytics(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_analytics(new ChromeDriver());
  }

  private void test_analytics(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.MANAGER_USER, CStudioSeleniumUtil.MANAGER_PASSWORD, true);

    // Navigate to Analytics Dashboard
    CStudioSeleniumUtil.navigate_to_analytics_dashboard(driver);

    // Verify Charts
    List<WebElement> elements = driver.findElements(By.tagName("iframe"));
    assertTrue(elements.size() == 7); // 7 graphic charts

    // Wait until at least 1 geo chart is displayed
    CStudioSeleniumUtil.wait_until_displayed(driver, CStudioSeleniumUtil.SHORT_TIMEOUT, By.tagName("embed"));

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
