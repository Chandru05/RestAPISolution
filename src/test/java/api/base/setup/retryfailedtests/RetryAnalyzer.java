package api.base.setup.retryfailedtests;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry Method to execute the failed Test Cases. 
 */
public class RetryAnalyzer implements IRetryAnalyzer {
  private int counter = 0;
  private final int retrylimit = 1;

  @Override
  public boolean retry(ITestResult result) {
    if (!result.isSuccess()) {
      if (counter < retrylimit) {
        counter++;
        return true;
      }
    }

    return false;
  }

}
