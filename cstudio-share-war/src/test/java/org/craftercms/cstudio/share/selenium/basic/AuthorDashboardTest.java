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

public class AuthorDashboardTest {
  @Ignore
  @Test
  public void test_on_internet_explorer() {
    test_author_dashboard(new InternetExplorerDriver());
  }

  @Test
  public void test_on_firefox() {
    test_author_dashboard(new FirefoxDriver());
  }

  @Ignore
  @Test
  public void test_on_chrome() {
    test_author_dashboard(new ChromeDriver());
  }

  private void test_author_dashboard(WebDriver driver) {
    // Login
    CStudioSeleniumUtil.try_login(driver, CStudioSeleniumUtil.AUTHOR_USER, CStudioSeleniumUtil.AUTHOR_PASSWORD, true);

    // Navigate to Acme.com Dashboard
    CStudioSeleniumUtil.navigate_to_dashboard(driver);

    // Verify Contextual Nav / Site drop-down
    CStudioSeleniumUtil.verify_context_nav(driver);

    // Verify Site title / header
    CStudioSeleniumUtil.verify_site_title(driver);

    // Verify 'My Recent Activity'
    CStudioSeleniumUtil.verify_recent_activity(driver);

    // Verify 'Icon Guide'
    CStudioSeleniumUtil.verify_icon_guide(driver);

    // Verify 'Site footer'
    CStudioSeleniumUtil.verify_footer(driver);

    // Close driver
    CStudioSeleniumUtil.exit(driver);
  }
}
