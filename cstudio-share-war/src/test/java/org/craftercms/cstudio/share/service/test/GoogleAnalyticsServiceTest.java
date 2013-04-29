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
package org.craftercms.cstudio.share.service.test;

import org.craftercms.cstudio.share.service.impl.*;
import org.craftercms.cstudio.share.to.*;
import org.junit.*;

import java.util.*;

public class GoogleAnalyticsServiceTest {
  private static final String CLIENT_USERNAME = "randy.saborio.rl@gmail.com";
  private static final String CLIENT_PASSWORD = "r1v3tl0g1c";
  private static final String TABLE_ID = "ga:48988421";
  private static final String START_DATE = "today-15";
  private static final String END_DATE = "today";

  private GoogleAnalyticsServiceImpl gas;

//  @Before
//  public void before_testing() {
//    AnalyticsConfigTO.Site.Report.Query query1 = new AnalyticsConfigTO.Site.Report.Query();
//    query1.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
//    query1.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
//    query1.addMetric(GoogleAnalyticsServiceImpl.METRIC_BOUNCES);
//
//    AnalyticsConfigTO.Site.Report.Query query2 = new AnalyticsConfigTO.Site.Report.Query();
//    query2.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
//    query2.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
//    query2.addMetric(GoogleAnalyticsServiceImpl.METRIC_BOUNCES);
//    query2.addSort(GoogleAnalyticsServiceImpl.METRIC_VISITS);
//
//    AnalyticsConfigTO.Site.Report.Query query3 = new AnalyticsConfigTO.Site.Report.Query();
//    query3.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
//    query3.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
//    query3.addMetric(GoogleAnalyticsServiceImpl.METRIC_BOUNCES);
//    query3.addFilter("ga:browser!@Explorer");
//
//    AnalyticsConfigTO.Site.Report.Query query4 = new AnalyticsConfigTO.Site.Report.Query();
//    query4.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
//    query4.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER_VERSION);
//    query4.addMetric(GoogleAnalyticsServiceImpl.METRIC_PAGEVIEWS);
//
//
//    AnalyticsConfigTO.Site.Report report1 = new AnalyticsConfigTO.Site.Report();
//    report1.setId("r1");
//    report1.setStartDate(START_DATE);
//    report1.setEndDate(END_DATE);
//    report1.setTableId(TABLE_ID);
//    report1.setQuery(query1);
//
//    AnalyticsConfigTO.Site.Report report2 = new AnalyticsConfigTO.Site.Report();
//    report2.setId("r2");
//    report2.setStartDate(START_DATE);
//    report2.setEndDate(END_DATE);
//    report2.setTableId(TABLE_ID);
//    report2.setQuery(query2);
//
//    AnalyticsConfigTO.Site.Report report3 = new AnalyticsConfigTO.Site.Report();
//    report3.setId("r3");
//    report3.setStartDate(START_DATE);
//    report3.setEndDate(END_DATE);
//    report3.setTableId(TABLE_ID);
//    report3.setQuery(query3);
//
//    AnalyticsConfigTO.Site.Report report4 = new AnalyticsConfigTO.Site.Report();
//    report4.setId("r4");
//    report4.setStartDate(START_DATE);
//    report4.setEndDate(END_DATE);
//    report4.setTableId(TABLE_ID);
//    report4.setQuery(query4);
//
//    AnalyticsConfigTO.Site site = new AnalyticsConfigTO.Site();
//    site.setId("Acme.com");
//    site.addReport(report1);
//    site.addReport(report2);
//    site.addReport(report3);
//    site.addReport(report4);
//
//    AnalyticsConfigTO AnalyticsConfigTO = new AnalyticsConfigTO();
//    AnalyticsConfigTO.setUsername(CLIENT_USERNAME);
//    AnalyticsConfigTO.setPassword(CLIENT_PASSWORD);
//    AnalyticsConfigTO.addSites(site);
//
//    gas = new GoogleAnalyticsServiceImpl(AnalyticsConfigTO);
//  }

  @Before
  public void before_testing() {
    gas = new GoogleAnalyticsServiceImpl();
  }

  @Test
  public void test_query() {
    GoogleAnalyticsQueryImpl query1 = new GoogleAnalyticsQueryImpl();
    query1.setUsername(CLIENT_USERNAME);
    query1.setPassword(CLIENT_PASSWORD);
    query1.setStartDate(START_DATE);
    query1.setEndDate(END_DATE);
    query1.setTableId(TABLE_ID);
    query1.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
    query1.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
    query1.addMetric(GoogleAnalyticsServiceImpl.METRIC_BOUNCES);

    AnalyticsReportTO report1 = gas.query(query1);
    List<AnalyticsReportTO.Entry> entries1 = report1.getEntries();
    for (AnalyticsReportTO.Entry entry : entries1) {
      for (Map.Entry<String, String> e : entry.getData().entrySet())
        System.out.println(e.getKey() + ": " + e.getValue());
    }

    GoogleAnalyticsQueryImpl query2 = new GoogleAnalyticsQueryImpl();
    query2.setUsername(CLIENT_USERNAME);
    query2.setPassword(CLIENT_PASSWORD);
    query2.setStartDate(START_DATE);
    query2.setEndDate(END_DATE);
    query2.setTableId(TABLE_ID);
    query2.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
    query2.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
    query2.addMetric(GoogleAnalyticsServiceImpl.METRIC_BOUNCES);
    query2.setSort(GoogleAnalyticsServiceImpl.METRIC_VISITS);

    AnalyticsReportTO report2 = gas.query(query2);
    List<AnalyticsReportTO.Entry> entries2 = report2.getEntries();
    for (AnalyticsReportTO.Entry entry : entries2) {
      for (Map.Entry<String, String> e : entry.getData().entrySet())
        System.out.println(e.getKey() + ": " + e.getValue());
    }

    GoogleAnalyticsQueryImpl query3 = new GoogleAnalyticsQueryImpl();
    query3.setUsername(CLIENT_USERNAME);
    query3.setPassword(CLIENT_PASSWORD);
    query3.setStartDate(START_DATE);
    query3.setEndDate(END_DATE);
    query3.setTableId(TABLE_ID);
    query3.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
    query3.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
    query3.addMetric(GoogleAnalyticsServiceImpl.METRIC_BOUNCES);
    query3.setFilters("ga:browser!@Explorer;ga:visits>1");

    AnalyticsReportTO report3 = gas.query(query3);
    List<AnalyticsReportTO.Entry> entries3 = report3.getEntries();
    for (AnalyticsReportTO.Entry entry : entries3) {
      for (Map.Entry<String, String> e : entry.getData().entrySet())
        System.out.println(e.getKey() + ": " + e.getValue());
    }

    GoogleAnalyticsQueryImpl query4 = new GoogleAnalyticsQueryImpl();
    query4.setUsername(CLIENT_USERNAME);
    query4.setPassword(CLIENT_PASSWORD);
    query4.setStartDate(START_DATE);
    query4.setEndDate(END_DATE);
    query4.setTableId(TABLE_ID);
    query4.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER);
    query4.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER_VERSION);
    query4.addMetric(GoogleAnalyticsServiceImpl.METRIC_PAGEVIEWS);

    AnalyticsReportTO report4 = gas.query(query4);
    List<AnalyticsReportTO.Entry> entries4 = report4.getEntries();
    for (AnalyticsReportTO.Entry entry : entries4) {
      for (Map.Entry<String, String> e : entry.getData().entrySet())
        System.out.println(e.getKey() + ": " + e.getValue());
    }

    GoogleAnalyticsQueryImpl query5 = new GoogleAnalyticsQueryImpl();
    query5.setUsername(CLIENT_USERNAME);
    query5.setPassword(CLIENT_PASSWORD);
    query5.setStartDate(START_DATE);
    query5.setEndDate(END_DATE);
    query5.setTableId(TABLE_ID);
    query5.addDimension(GoogleAnalyticsServiceImpl.DIMENSION_COUNTRY);
    query5.addMetric(GoogleAnalyticsServiceImpl.METRIC_VISITS);
    query5.setSort(GoogleAnalyticsServiceImpl.DIMENSION_COUNTRY);

    AnalyticsReportTO report5 = gas.query(query5);
    List<AnalyticsReportTO.Entry> entries5 = report5.getEntries();
    for (AnalyticsReportTO.Entry entry : entries5) {
      for (Map.Entry<String, String> e : entry.getData().entrySet())
        System.out.println(e.getKey() + ": " + e.getValue());
    }
  }

//  @Test
//  public void test_login() {
//    assertTrue(gas.isLoggedIn());
//  }

//  @Test
//  public void test_available_accounts() {
//    AccountFeed af = gas.getAvailableAccounts();
//    assertNotNull(af);
//  }

//  @Test
//  public void test_available_profiles() {
//    List<AccountEntry> l = gas.getAvailableProfiles();
//    assertNotNull(l);
//    assertFalse(l.isEmpty());
//    System.out.println("TABLEID=" + l.get(0).getTableId().getValue());
//  }

//  @Test
//  public void test_query1() {
//    AnalyticsReportTO report = gas.query("Acme.com", "r1");
//    List<AnalyticsReportTO.Entry> entries = report.getEntries();
//    for (AnalyticsReportTO.Entry entry : entries) {
//      for (Map.Entry<String, String> e : entry.getData().entrySet())
//        System.out.println(e.getKey() + ": " + e.getValue());
//    }
//
//    report = gas.query("Acme.com", "r2");
//    entries = report.getEntries();
//    for (AnalyticsReportTO.Entry entry : entries) {
//      for (Map.Entry<String, String> e : entry.getData().entrySet())
//        System.out.println(e.getKey() + ": " + e.getValue());
//    }
//
//    report = gas.query("Acme.com", "r3");
//    entries = report.getEntries();
//    for (AnalyticsReportTO.Entry entry : entries) {
//      for (Map.Entry<String, String> e : entry.getData().entrySet())
//        System.out.println(e.getKey() + ": " + e.getValue());
//    }
//
//    report = gas.query("Acme.com", "r4");
//    entries = report.getEntries();
//    for (AnalyticsReportTO.Entry entry : entries) {
//      for (Map.Entry<String, String> e : entry.getData().entrySet())
//        System.out.println(e.getKey() + ": " + e.getValue());
//    }
//  }
//
//  @Test
//  public void test_query2() {
//    List<AccountEntry> l = gas.getAvailableProfiles();
//    assertNotNull(l);
//    assertFalse(l.isEmpty());
//
//    String tableId = l.get(0).getTableId().getValue();
//
//    // basic query
//    DataQuery dq = gas.createQuery(tableId, START_DATE, END_DATE, GoogleAnalyticsServiceImpl.DIMENSION_BROWSER, "ga:visits,ga:bounces");
//    assertNotNull(dq);
//    DataFeed df = gas.getFeed(dq);
//    assertNotNull(df);
//    for (DataEntry de : df.getEntries())
//      System.out.println("Browser: " + de.stringValueOf(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER) +
//        " Visits: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_VISITS) +
//        " Bounces: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_BOUNCES));
//    System.out.println();
//
//    // sorted query
//    dq = gas.createSortedQuery(tableId, START_DATE, END_DATE, GoogleAnalyticsServiceImpl.DIMENSION_BROWSER, "ga:visits,ga:bounces", "ga:visits");
//    assertNotNull(dq);
//    df = gas.getFeed(dq);
//    assertNotNull(df);
//    for (DataEntry de : df.getEntries())
//      System.out.println("Browser: " + de.stringValueOf(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER) +
//        " Visits: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_VISITS) +
//        " Bounces: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_BOUNCES));
//    System.out.println();
//
//    // filtered query
//    dq = gas.createFilteredQuery(tableId, START_DATE, END_DATE, GoogleAnalyticsServiceImpl.DIMENSION_BROWSER, "ga:visits,ga:bounces", "ga:browser!@Explorer");
//    assertNotNull(dq);
//    df = gas.getFeed(dq);
//    assertNotNull(df);
//    for (DataEntry de : df.getEntries())
//      System.out.println("Browser: " + de.stringValueOf(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER) +
//        " Visits: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_VISITS) +
//        " Bounces: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_BOUNCES));
//    System.out.println();
//
//    dq = gas.createQuery(tableId, START_DATE, END_DATE, GoogleAnalyticsServiceImpl.DIMENSION_BROWSER + "," + GoogleAnalyticsServiceImpl.DIMENSION_BROWSER_VERSION,
//      GoogleAnalyticsServiceImpl.METRIC_PAGEVIEWS);
//    assertNotNull(dq);
//    df = gas.getFeed(dq);
//    assertNotNull(df);
//    for (DataEntry de : df.getEntries())
//      System.out.println("Browser: " + de.stringValueOf(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER) +
//        " Version: " + de.stringValueOf(GoogleAnalyticsServiceImpl.DIMENSION_BROWSER_VERSION) +
//        " Pageviews: " + de.stringValueOf(GoogleAnalyticsServiceImpl.METRIC_PAGEVIEWS));
//    System.out.println();
//
//
//    String[] dimensions = new String[]{
//      GoogleAnalyticsServiceImpl.DIMENSION_BROWSER,
//      GoogleAnalyticsServiceImpl.DIMENSION_BROWSER_VERSION};
//    String[] metrics = new String[]{
//      GoogleAnalyticsServiceImpl.METRIC_PAGEVIEWS};
//    AnalyticsReportTO report = gas.query(tableId, START_DATE, END_DATE, dimensions, metrics);
//    List<AnalyticsReportTO.Entry> entries = report.getEntries();
//    for (AnalyticsReportTO.Entry entry : entries) {
//      for (Map.Entry<String, String> e : entry.getData().entrySet())
//        System.out.println(e.getKey() + ": " + e.getValue());
//    }
//    System.out.println();
//    System.out.println();
//
//    System.out.println("Visitors: " + gas.getVisitors(tableId, START_DATE, END_DATE));
//    System.out.println("New Visits: " + gas.getNewVisits(tableId, START_DATE, END_DATE));
//    System.out.println("Percent New Visits: " + gas.getPercentNewVisits(tableId, START_DATE, END_DATE));
//    System.out.println();
//
//    System.out.println("Visits: " + gas.getVisits(tableId, START_DATE, END_DATE));
//    System.out.println("Time On Site: " + gas.getTimeOnSite(tableId, START_DATE, END_DATE));
//    System.out.println("Avg Time On Site: " + gas.getAvgTimeOnSite(tableId, START_DATE, END_DATE));
//    System.out.println();
//
//    System.out.println("Entrances: " + gas.getEntrances(tableId, START_DATE, END_DATE));
//    System.out.println("Entrances Rate: " + gas.getEntranceRate(tableId, START_DATE, END_DATE));
//    System.out.println("Bounces: " + gas.getBounces(tableId, START_DATE, END_DATE));
//    System.out.println("Bounces Rate: " + gas.getBounceRate(tableId, START_DATE, END_DATE));
//    System.out.println("Visit Bounce Rate: " + gas.getVisitBounceRate(tableId, START_DATE, END_DATE));
//    System.out.println("Pageviews: " + gas.getPageviews(tableId, START_DATE, END_DATE));
//    System.out.println("Pageviews Per Visit: " + gas.getPageviewsPerVisit(tableId, START_DATE, END_DATE));
//    System.out.println("Unique Pageviews: " + gas.getUniquePageviews(tableId, START_DATE, END_DATE));
//    System.out.println("Time On Page: " + gas.getTimeOnPage(tableId, START_DATE, END_DATE));
//    System.out.println("Avg Time On Page: " + gas.getAvgTimeOnPage(tableId, START_DATE, END_DATE));
//  }
}