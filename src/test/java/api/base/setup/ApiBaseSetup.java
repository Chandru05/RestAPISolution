package api.base.setup;

import api.base.setup.reporting.FrameworkListener;
import api.modules.generic.DbQueries;
import api.modules.generic.RestUtilities;
import api.modules.objects.KnownDefectList;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.asserts.SoftAssert;

/**
 * Runs based on FrameListener.class. 
 */

@Listeners(FrameworkListener.class)
public class ApiBaseSetup {
  
  protected static Logger log;
  protected static RemoteSshConnection rsc;
  protected SoftAssert softAssert;
  protected static DbQueries dbConnect;
  
  
  protected RequestSpecification requestSpec;
  protected ResponseSpecification responseSpec;
  protected RestUtilities ru;


  // protected static ExtentTest test;
  protected static ThreadLocal<ExtentTest> extentreportlog  = new ThreadLocal<>();
  
  public static void log(Status status, String message) {
    extentreportlog.get().log(status, message);
  }

  protected static ThreadLocal<String> testName = new ThreadLocal<>();
  
  /**
   * Basic setup before starting the suite.
   */
  @BeforeSuite
  public void automationSetup() {
    log = LogManager.getLogger(ApiBaseSetup.class);

    log.info("Before Test - Base Setup");
    rsc = new RemoteSshConnection();
    
    getSetupDetails();
    
    getDbDetails();
 
    createDbConn();
    
    getBaseUrl();
    
    setCertificate();
    
    KnownDefectList.getDefectMap();
    

  }
  
  /**
   * Open DB connection before the test class starts.
   */
  @BeforeClass
  public void beforeTestCase() {
    ru = new RestUtilities();
  }
  
  
  
  /**
   * It appends test method name with parameter value when using Data provider.
   * It helps to differentiate the tests names in the report & Octane. 
   */
  @BeforeMethod(alwaysRun = true)
  public void generateMethodName(Method method, Object[] testData,
      ITestContext ctx) {

    String methodName = method.getName();
    if (testData.length > 0) {
      methodName += "_" + testData[0];
    }

    testName.set(methodName);
    ctx.setAttribute("testName", testName.get());
    log.info("Setting the Method Name as " + testName.get());
    
    requestSpec = ru.getRequestSpecification();
    responseSpec = ru.getReponseSpecification();
  }
  
  /**
   * Closing the DB Connection.
   */
  @AfterSuite
  public void closeConnection() {
    
    if (Constants.con != null) {
      try {
        if (!Constants.con.isClosed()) {
          Constants.con.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
  
  
  /**
   * Get baseUrl.
   *
   * @return  Host Url
   */
  public static String getBaseUrl() {

    if (Constants.TEST_IP != null || !Constants.TEST_IP.isEmpty()) {
      String[] s = Constants.TEST_IP.split("\\.");
      Constants.TEST_URL = String.format(Constants.TEST_URL, s[2], s[3], Constants.TEST_PORT);
      Constants.TEST_HOST = String.format(Constants.TEST_HOST, s[2], s[3]);
    }
    log.info("BASE URL ----------" + Constants.TEST_URL);
    return Constants.TEST_URL;
  }
  
  /**
   * Get Test Application Setup Details.
   */
  public void getSetupDetails() {
    
    try {
      Constants.TEST_IP = System.getProperty("TestIp");
      log.info("Test IP from Jenkins:" + Constants.TEST_IP);
      if (Constants.TEST_IP == null || Constants.TEST_IP.isEmpty()) {
        Constants.TEST_IP = LocalConstants.LOCAL_TEST_IP;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      Constants.TEST_USER = System.getProperty("TestUser");
      log.info("Test UserName from Jenkins:" + Constants.TEST_USER);
      if (Constants.TEST_USER == null || Constants.TEST_USER.isEmpty()) {
        Constants.TEST_USER = Constants.Default_TEST_USER;
        log.info("SET Test Username as:" + LocalConstants.TEST_USER);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      Constants.TEST_PWD = System.getProperty("Testpwd");
      log.info("Test Password from Jenkins:" + Constants.TEST_PWD);
      if (Constants.TEST_PWD == null || Constants.TEST_PWD.isEmpty()) {
        Constants.TEST_PWD = Constants.Default_TEST_PWD;
        log.info("SET Test Password as:" + LocalConstants.TEST_PWD);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  /**
   * Extract DB Details from Test Server.
   */
  private void getDbDetails() {

    String resp = rsc.jschConnection(Constants.TEST_IP, Constants.TEST_USER,
        Constants.TEST_PWD,
        "db information source");
   
    Constants.DB_USER_NAME = "";
 
    log.info("DataBase TYPE ---- " + Constants.DB_TYPE);
    log.info("DataBase IP ---- " + Constants.DB_IP);
    log.info("DataBase PORT---- " + Constants.DB_PORT);
    log.info("DataBase Name ---- " + Constants.DB_NAME);
    log.info("DataBase UserName ---- " + Constants.DB_USER_NAME);
  }
  
  
  /**
   * Creating DB Connection.
   */
  public static void createDbConn() {

    Properties props = new Properties();

    String databaseUrl = null;

    try {
      Constants.DB_PWD = System.getProperty("DBPassword");
      log.info("Database Password from Jenkins:" + Constants.DB_PWD);
      if (Constants.DB_PWD == null || Constants.DB_PWD.isEmpty()) {
        Constants.DB_PWD = Constants.Default_DB_PWD;
        log.info("Database Password Identified as:" + Constants.DB_PWD);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    props.setProperty("user", Constants.DB_USER_NAME);
    props.setProperty("password", Constants.DB_PWD);
    if (Constants.DB_TYPE.equalsIgnoreCase("sqlserver")) {
      try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      databaseUrl = "jdbc:sqlserver://" + Constants.DB_IP + ":" + Constants.DB_PORT
          + ";encrypt=false;database=" + Constants.DB_NAME + ";"; // integratedSecurity=true;
      log.info("Connecting to MSSQL DB........... ");
    } else if (Constants.DB_TYPE.equalsIgnoreCase("postgresql")) {

      databaseUrl = "jdbc:postgresql://" + Constants.DB_IP + ":" + Constants.DB_PORT
          + "/" + Constants.DB_NAME + "";
      log.info("Connecting to PostgreSql DB........... ");
    } else if (Constants.DB_TYPE.equalsIgnoreCase("mysql")
        || Constants.DB_TYPE.equalsIgnoreCase("mariadb")) {
      databaseUrl = "jdbc:mysql://" + Constants.DB_IP + ":" + Constants.DB_PORT + "/"
          + Constants.DB_NAME + "";
      log.info("Connecting to MySql DB........... ");
    }

    
    try {
      Constants.con = DriverManager.getConnection(databaseUrl, props);
      log.info("DB Connection Successfull");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    dbConnect = new DbQueries(log);

  }
  
  /**
   * Set Root Certificate to validate APAC Certificate.
   */
  public void setCertificate() {
    
    try {
      Constants.Jenkins_IP = System.getProperty("jenkinsIp").trim();
    } catch (Exception e) {
      Constants.Jenkins_IP = null;
    }
    log.info("JENKINS IP -- " + Constants.Jenkins_IP);
    
    if (Constants.Jenkins_IP == null
        || Constants.Jenkins_IP.isEmpty()) {
      System.setProperty("javax.net.ssl.trustStore", "local path cert location");
      System.setProperty("javax.net.ssl.trustStorePassword", "cert password");
    } else if (Constants.Jenkins_IP.equals("164.99.xx.xx")) {
      System.setProperty("javax.net.ssl.trustStore",
          "/usr/lib64/jvm/java-17-openjdk-17/lib/security/cacerts");
      System.setProperty("javax.net.ssl.trustStorePassword", "cert password");
    } else {
      log.error(
          "Certificate Path is NOT SET correctly. PKIX Path building Failed Exception will be observed.");
    }
  }
  
  /**
   * Get Date and Time.
   */
  public String getDateTime(String timeZone, String format) {
    ZonedDateTime dt = ZonedDateTime.now(ZoneId.of(timeZone));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return dt.format(formatter);   
  }

}
