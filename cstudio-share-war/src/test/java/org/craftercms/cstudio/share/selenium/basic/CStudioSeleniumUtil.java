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

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class CStudioSeleniumUtil {
  private static Properties properties = new Properties();
  static {
    try {
      properties.load(CStudioSeleniumUtil.class.getClassLoader().getResourceAsStream("selenium.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static final int SHORT_TIMEOUT = Integer.parseInt(properties.getProperty("short.timeout"));
  public static final int LONG_TIMEOUT = Integer.parseInt(properties.getProperty("long.timeout"));

  public static final String BASE_WIDGET_URL = properties.getProperty("base.widget.url");
  public static final String IMAGE_PICKER_WIDGET = "image-picker";
  public static final String CHECKBOX_GROUP_SIMPLE_WIDGET = "checkbox-group-simple";
  public static final String DATE_TIME_WIDGET = "date-time";
  public static final String ENABLE_DISABLE_DROPDOWN_WIDGET = "enable-disable-dropdown";
  public static final String FLASH_PICKER_WIDGET = "flash-picker";
  public static final String GROUP_CHECKBOXES_WIDGET = "group-checkboxes";
  public static final String HEAVY_RTE_WIDGET = "heavy-rte";
  public static final String INPUT_COUNTED_WIDGET = "input-counted";
  public static final String NODE_SELECTOR_WIDGET = "node-selector";
  public static final String PAGE_URL_WIDGET = "page-url";
  public static final String SELECT_ONE_EX_WIDGET = "select1ex";
  public static final String SIMPLE_CHECKBOX_WIDGET = "simple-checkbox";
  public static final String WATERFALL_SELECTOR_WIDGET = "waterfall-selector";
  public static final String YES_NO_DROPDOWN_WIDGET = "yes-no-dropdown";

  public static final String ADMIN_USER = properties.getProperty("admin.username");
  public static final String ADMIN_PASSWORD = properties.getProperty("admin.password");
  public static final String MANAGER_USER = properties.getProperty("manager.username");
  public static final String MANAGER_PASSWORD = properties.getProperty("manager.password");
  public static final String AUTHOR_USER = properties.getProperty("author.username");
  public static final String AUTHOR_PASSWORD = properties.getProperty("author.password");
  public static final String JOE_USER = properties.getProperty("joe.username");
  public static final String JOE_PASSWORD = properties.getProperty("joe.password");

  public static void try_login(WebDriver driver, String username, String password, boolean expected) {
    driver.get(properties.getProperty("share.login.url"));
    WebElement element = driver.findElement(By.name("username"));
    element.sendKeys(username);
    element = driver.findElement(By.name("password"));
    element.sendKeys(password);
    element = driver.findElement(By.id("btn-login"));
    element.click();
    assertEquals(driver.getCurrentUrl().equals(String.format(properties.getProperty("user.dashboard.url"), username)), expected);
  }

  public static void logout_from_share(WebDriver driver) {
    WebElement element = driver.findElement(By.linkText("Logout"));
    element.click();
    driver.get(properties.getProperty("share.logout.url"));
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("btn-login"));
  }

  public static void logout_from_cs(WebDriver driver) {
    WebElement element = driver.findElement(By.id("acn-logout-link"));
    element.click();
    driver.get(properties.getProperty("share.logout.url"));
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("btn-login"));
  }

  public static void navigate_to_dashboard(WebDriver driver) {
    driver.navigate().to(properties.getProperty("acme.dashboard.url"));
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("acn-wcm-logo-image"));
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("acn-dropdown-toggler"));
    assertTrue(driver.getCurrentUrl().equals(properties.getProperty("acme.dashboard.url")));
  }

  public static void navigate_to_unauthorized_url(WebDriver driver, String username) {
    driver.navigate().to(properties.getProperty("acme.dashboard.url"));
    assertTrue(driver.getCurrentUrl().equals(String.format(properties.getProperty("user.dashboard.url"), username)));
  }

  public static void exit(WebDriver driver) {
    driver.close();
    driver.quit();
  }

  public static void wait_until_not_displayed(WebDriver driver, int timeout, final By by) {
    if (driver.findElement(by).isDisplayed())
      new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver d) {
          return !d.findElement(by).isDisplayed();
        }
      });
  }

  public static void wait_until_displayed(WebDriver driver, int timeout, final By by) {
    if (!driver.findElement(by).isDisplayed())
      new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver d) {
          return d.findElement(by).isDisplayed();
        }
      });
  }

  public static void wait_until_enabled(WebDriver driver, int timeout, final By by) {
    if (!driver.findElement(by).isEnabled())
      new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver d) {
          return d.findElement(by).isEnabled();
        }
      });
  }

  public static boolean switch_to_window(WebDriver driver, String window) {
    Set<String> handles = driver.getWindowHandles();
    for (String h : handles) {
      driver.switchTo().window(h);
      if (driver.getTitle().equals(window))
        break;
    }
    return driver.getTitle().equals(window);
  }

  public static void verify_context_nav(WebDriver driver) {
    // Check context nav exists
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("authoringContextNavHeader"));

    // Check logo link exists
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("acn-wcm-logo-link"));

    // Check site drop down exists and is working
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("acn-dropdown-toggler"));
    WebElement element = driver.findElement(By.id("acn-dropdown-toggler"));
    assertTrue(element.getText().equals("Site Content"));
    element.click();

    new WebDriverWait(driver, SHORT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElement(By.id("acn-dropdown-menu-wrapper")).isDisplayed();
      }
    });

    // Check drop down is displayed correctly
    element = driver.findElement(By.id("acn-dropdown-menu-wrapper"));
    assertTrue(element.isDisplayed());
  }

  public static void verify_site_title(WebDriver driver) {
    WebElement element = driver.findElement(By.id("pageTitle"));
    assertTrue(element.isDisplayed());
  }

  public static void verify_recent_activity(WebDriver driver) {
    // Verify 'My Recent Activity' component is working
    verify_component(driver, "1-4");
    verify_count(driver, "1-4");
    // Check 'My Recent Activity' exists
    WebElement element = driver.findElement(By.tagName("body"));
    assertTrue(element.getText().contains("My Recent Activity"));
  }

  public static void verify_recently_made_live(WebDriver driver) {
    // Verify 'Recently Made Live' component is working
    verify_component(driver, "1-3");
    // Check 'Recently Made Live' exists
    WebElement element = driver.findElement(By.tagName("body"));
    assertTrue(element.getText().contains("Recently Made Live"));
  }

  public static void verify_approved_scheduled_items(WebDriver driver) {
    // Verify 'Approved Scheduled Items' component is working
    verify_component(driver, "1-2");
    verify_count(driver, "1-2");
    // Check 'Approved Scheduled Items' exists
    WebElement element = driver.findElement(By.tagName("body"));
    assertTrue(element.getText().contains("Approved Scheduled Items"));
  }

  public static void verify_go_live_queue(WebDriver driver) {
    // Verify 'Go Live Queue' component is working
    verify_component(driver, "1-1");
    verify_count(driver, "1-1");
    // Check 'Go Live Queue' exists
    WebElement element = driver.findElement(By.tagName("body"));
    assertTrue(element.getText().contains("Go Live Queue"));
  }

  private static void verify_component(WebDriver driver, String number) {
    // Check toggle is working
    WebElement element = driver.findElement(By.id("widget-toggle-component-" + number));
    assertTrue(element.getAttribute("class").equals("ttClose"));
    element.click();
    assertTrue(element.getAttribute("class").equals("ttOpen"));
    element.click();
    assertTrue(element.getAttribute("class").equals("ttClose"));
  }

  private static void verify_count(WebDriver driver, String number) {
    // Check count exists
    WebElement element = driver.findElement(By.id("component-" + number + "-total-count"));
    if (element.getText() != null && !element.getText().trim().isEmpty())
      Integer.parseInt(element.getText()); // is a valid count?
  }

  public static void verify_footer(WebDriver driver) {
    driver.findElement(By.id("footer"));
  }

  public static void verify_icon_guide(WebDriver driver) {
    WebElement element = driver.findElement(By.id("widget-toggle-icon-guide"));
    assertTrue(element.isDisplayed());
    element = driver.findElement(By.tagName("body"));
    assertTrue(element.getText().contains("Icon Guide"));
  }

  public static void open_site_context(WebDriver driver) {
    click_on(driver, By.id("acn-dropdown-toggler"));
    // Wait for the dropdown menu to load
    wait_until_displayed(driver, SHORT_TIMEOUT, By.id("acn-dropdown-menu-wrapper"));
  }

  public static void open_site_context_node(WebDriver driver, String id) {
    click_on(driver, By.id(id));
  }

  public static void open_site_context_pages_node(WebDriver driver) {
    open_site_context_node(driver, "pages-tree");
  }

  public static void click_on(WebDriver driver, By by) {
    wait_until_displayed(driver, SHORT_TIMEOUT, by);
    wait_until_enabled(driver, SHORT_TIMEOUT, by);
    driver.findElement(by).click();
  }

  public static void edit_index_page(WebDriver driver) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
      "CStudioAuthoring.Operations.editContent(" +
        "'/acme-com/page/entry', " +
        "'acmecom', " +
        "'/site/website/index.xml', " +
        "'', " +
        "'/site/website/index.xml', " +
        "false, " +
        "{" +
        "  success: function() { " +
        "  this.callingWindow.location.reload(true); " +
        "}," +
        "  failure: function() {" +
        "}," +
        "  callingWindow: window" +
        "}, " +
        "'');");

    // Wait for the window to load
    new WebDriverWait(driver, CStudioSeleniumUtil.LONG_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.getWindowHandles().size() > 1;
      }
    });
  }

  private static void navigate_to(WebDriver driver, String url) {
    driver.navigate().to(url);
    new WebDriverWait(driver, SHORT_TIMEOUT);
    assertTrue(driver.getCurrentUrl().equals(url));
  }

  public static void navigate_to_image_picker_widget(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, IMAGE_PICKER_WIDGET));
  }

  public static void navigate_to_checkbox_group_simple_widget(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, CHECKBOX_GROUP_SIMPLE_WIDGET));
  }

  public static void navigate_to_checkbox_simple_widget(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, SIMPLE_CHECKBOX_WIDGET));
  }

  public static void navigate_to_date_time_widget(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, DATE_TIME_WIDGET));
  }

  public static void navigate_to_enable_disable_dropdown(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, ENABLE_DISABLE_DROPDOWN_WIDGET));
  }

  public static void navigate_to_group_checkboxes(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, GROUP_CHECKBOXES_WIDGET));
  }

  public static void navigate_to_input_counted(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, INPUT_COUNTED_WIDGET));
  }

  public static void navigate_to_node_selector(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, NODE_SELECTOR_WIDGET));
  }

  public static void navigate_to_waterfall_selector(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, WATERFALL_SELECTOR_WIDGET));
  }

  public static void navigate_to_page_url(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, PAGE_URL_WIDGET));
  }

  public static void navigate_to_select1_ex(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, SELECT_ONE_EX_WIDGET));
  }

  public static void navigate_to_yes_no_dropdown(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, YES_NO_DROPDOWN_WIDGET));
  }

  public static void navigate_to_heavy_rte(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, HEAVY_RTE_WIDGET));
  }

  public static void navigate_to_flash_picker_widget(WebDriver driver) {
    navigate_to(driver, String.format(BASE_WIDGET_URL, FLASH_PICKER_WIDGET));
  }

  public static void navigate_to_analytics_dashboard(WebDriver driver) {
    navigate_to(driver, properties.getProperty("acme.analytics.dashboard.url"));

    // Verify URL
    assertTrue(driver.getCurrentUrl().equals(properties.getProperty("acme.analytics.dashboard.url")));

    // Wait for the page to load
    new WebDriverWait(driver, LONG_TIMEOUT).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return d.findElements(By.tagName("iframe")).size() == 7;
      }
    });
  }
}
