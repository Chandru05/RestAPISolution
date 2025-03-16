package api.base.setup.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

/**
 * Merge JSON files to create HTML report.
 */
public class MergeReport {
    
  private Logger log;
  
  @Test
  public void mergeHtml() {
    
    log = LogManager.getLogger(MergeReport.class);
    
    String filePath = null;
    if (System.getProperty("testIp") == null
        || System.getProperty("testIp").isEmpty()) {
      filePath = System.getProperty("user.dir") + File.separator + "extentReports" + File.separator 
          + "jsonFolder";
    } else if (System.getProperty("jenkinsIp").trim().equals("164.x.x.x")) {
      filePath = "/home/jenkins/jsonReports";
    } else {
      log.error("Path is incorrect");
    }
    
    log.info("FilePath set as : " + filePath);
    ExtentSparkReporter mergedSpark = new ExtentSparkReporter(System.getProperty("user.dir") + File.separator + "extentReports"
        + File.separator + "ConsolidatedExtentReport.html");
    ExtentReports extentMerged = new ExtentReports();
    
    File jsonDir = new File(filePath); 
    
    if (jsonDir.exists()) {
      log.info("Directory exists");
      Arrays.stream(jsonDir.listFiles()).forEach(jsonFile -> {
        log.info("JsonFileName: " + jsonFile.getName());
        try {
          extentMerged.createDomainFromJsonArchive(jsonFile.getPath());
        } catch (IOException e) {
          log.warn("Check Any other file types other than json is present in the same folder");
          e.printStackTrace();
        }
      });
    }
    
    mergedSpark.viewConfigurer().viewOrder()
    .as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST,
        ViewName.EXCEPTION, ViewName.AUTHOR, ViewName.CATEGORY})
    .apply();
    mergedSpark.config().setTimelineEnabled(true);
    mergedSpark.config().setTheme(Theme.DARK);
    mergedSpark.config().setProtocol(Protocol.HTTPS);

    extentMerged.attachReporter(mergedSpark);
    extentMerged.flush();
  }
  


}
