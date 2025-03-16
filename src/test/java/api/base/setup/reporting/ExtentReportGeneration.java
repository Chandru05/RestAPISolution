package api.base.setup.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * logic to generate Extent Report for every test cases executed in Framework.
 */
public class ExtentReportGeneration {

  static ExtentReports extent = null;
  static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
  
  /**
   * Creation of Extent Report HTML file and configurations.
   *
   * @param name Gets the test case name.
   * @return extent Generated Extent Report.
   */
  
  public static synchronized ExtentReports reportSetup(String name) {

    ExtentSparkReporter reporter = null;

    if (extent == null) {
        reporter = new ExtentSparkReporter(
            System.getProperty("user.dir") + File.separator + "extentReports"
                + File.separator + "ExtentReportResults.html");
        reporter.config().setReportName("Rest API Automation");
      } 

      extent = new ExtentReports();

      reporter.viewConfigurer().viewOrder()
          .as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST,
              ViewName.EXCEPTION, ViewName.AUTHOR, ViewName.CATEGORY})
          .apply();
      reporter.config().setTimelineEnabled(true);
      reporter.config().setTheme(Theme.DARK);
      reporter.config().setProtocol(Protocol.HTTPS);
      
      JsonFormatter jsonReporter = new JsonFormatter(System.getProperty("user.dir")
          + File.separator + "extentReports"
          + File.separator + "jsonFolder"
          + File.separator + "extentreport.json");

      extent.attachReporter(jsonReporter, reporter);

      extent.setSystemInfo("Host name", "IP Address");
      extent.setSystemInfo("Environment", "QA");
      extent.setSystemInfo("user", "Chandru");

    return extent;

  }
  
  /**
   * Staring the Test in Extent Report.
   *
   * @param st1 Test Name
   * @param st2 Method Name
   * @param st3 Author Name
   * @return Test
   */
  public static synchronized ExtentTest startTest(String st1, String st2,
      String st3) {

    ExtentTest test = reportSetup(st1).createTest(st2).assignAuthor(st3);

    extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
    return test;
  }
  
  public static synchronized ExtentTest getTest() {
    return (ExtentTest) extentTestMap.get((int) (long) (Thread.currentThread().getId()));
  }
  
  
  /**
   * Clears the report.
   */
  public static void tearDown() {
    if (extent != null) {
      extent.flush();
    }
  }

}
