package api.base.setup.reporting;

import api.base.setup.ApiBaseSetup;
import api.modules.objects.KnownDefectList;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import java.util.Arrays;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Framework Test Listener class for Logging and Reporting.
 */
public class FrameworkListener extends ApiBaseSetup implements ITestListener {


  public String getClassName(ITestResult result) {
    return result.getTestClass().getName();
  }

  public String getTestName(ITestResult result) {
    // return result.getTestContext().getAttribute("testName").toString();
    return testName.get();
  }

  @Override
  public void onStart(ITestContext context) {
    log.info("onStart Test Started: " + context.getName());

  }

  @Override
  public void onTestStart(ITestResult result) {
    String className = getClassName(result);
    String methodName = getTestName(result);

    log.info(className + " ---------- " + methodName
        + "----------STARTED");
    String val = "default";
    if (result.getMethod().getConstructorOrMethod().getMethod()
        .isAnnotationPresent(Author.class)) {
      Author anno = result.getMethod().getConstructorOrMethod().getMethod()
          .getAnnotation(Author.class);
      val = anno.value();
    }

    ExtentTest test = ExtentReportGeneration.startTest(className,
        methodName, val);
    extentreportlog.set(test);

    String[] groups = result.getMethod().getGroups();
    if (groups != null && groups.length > 0) {
      extentreportlog.get().info("Test Groups: " + Arrays.toString(groups));
      extentreportlog.get().assignCategory(groups);
    }   
  }
  
  /**
   * Triggers on every Test Success.
   */
  public void onTestSuccess(ITestResult result) {
    
    String className = getClassName(result);
    String methodName = getTestName(result);
    ExtentTest test = extentreportlog.get();
    // ExtentTest test = ExtentReportGeneration.getTest();
    test.log(Status.PASS,
          MarkupHelper.createLabel(methodName, ExtentColor.GREEN));
    log.info(className + " ---------- " + methodName
        + "---------- PASSED");

    ExtentReportGeneration.tearDown();
  }
  
  /**
   * Triggers on every Test Failure.
   */
  public void onTestFailure(ITestResult result) {

    String className = getClassName(result);
    String methodName = getTestName(result);
    ExtentTest test = extentreportlog.get();

      test.log(Status.FAIL,
          MarkupHelper.createLabel(methodName, ExtentColor.RED));
      
      String value = KnownDefectList.getDefectByTestName(methodName);

      if (value != null && !value.isBlank()) {
        test.log(Status.FAIL, "Test Failed due to Known Defect: " + value);
        log.info("Test Case Name: " + className + " ---------- Test Method Name: " + methodName
            + "---------- FAILED due to known Defect: " + value);
        test.info("Known Defect Id: " + value);
        test.assignCategory("Valid Defects");
      } else {
        log.info("Test Case Name: " + className + " ---------- Test Method Name: " + methodName
            + "---------- FAILED");
        test.log(Status.FAIL, result.getThrowable());
      }

    ExtentReportGeneration.tearDown();
  }  
  
  
  /**
   * Triggers on every Test Skipped.
   */
  public void onTestSkipped(ITestResult result) {
    
    String className = getClassName(result);
    String methodName = getTestName(result);
    ExtentTest test = extentreportlog.get();
    test.log(Status.SKIP,
        MarkupHelper.createLabel(result.getName(), ExtentColor.ORANGE));
    log.info("Test Case Name: " + className + " ---------- Test Method Name: " + methodName
        + "---------- SKIPPED");

    ExtentReportGeneration.tearDown();
  }
  
  public void onTestFinish(ITestResult result) {
    extentreportlog.remove();
  }

}
