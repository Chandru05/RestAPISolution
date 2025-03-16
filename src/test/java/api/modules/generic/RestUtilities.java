package api.modules.generic;

import static io.restassured.RestAssured.given;

import api.base.setup.ApiBaseSetup;
import api.base.setup.Constants;
import api.base.setup.endpoints.UserEndpoints;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.specification.SpecificationQuerier;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.testng.Assert;

/**
 * Core Logics related to Rest Assured Rest API Implementation.
 *
 * @author Chandru05
 */
public class RestUtilities extends ApiBaseSetup{
  
  public String endpoint;

  public void setEndPoint(String epoint) {
    endpoint = epoint;
  }
  
  /**
   * Setting Request Specification for custom Host.
   *
   * @author chandru05
   * @param host Enter non default hostname.
   * @return requestBuilder
   */
  public RequestSpecification getRequestSpecification(String host) {
    RequestSpecBuilder requestBuilder = new RequestSpecBuilder();
    requestBuilder.setBaseUri(host); 
    requestBuilder.setBasePath(UserEndpoints.BASE_PATH);
    requestBuilder.addHeader("Content-Type", "application/json");
    requestBuilder.setAccept("application/json");

    return requestBuilder.build();
  }
  
  /**
   * Setting Request Specification for Default Host.
   *
   * @author chandru05
   * @return request Builder
   */
  public RequestSpecification getRequestSpecification() {
    RequestSpecBuilder requestBuilder = new RequestSpecBuilder();
    requestBuilder.setBaseUri(Constants.TEST_URL);
    requestBuilder.setBasePath(UserEndpoints.BASE_PATH);
    requestBuilder.addHeader("Content-Type", "application/json");
    requestBuilder.setAccept("application/json");
    // requestBuilder.log(LogDetail.ALL);
    // RestAssured.defaultParser = Parser.JSON;
    return requestBuilder.build();
  }
  
  /**
   * Generate request specification and setting Authorization for default zone.
   */
  public RequestSpecification setRequestSpec(String username, String pwd) {
    RequestSpecification rspec = getRequestSpecification();
    rspec.auth().basic(username, pwd);
    return rspec;
  }
  
  /**
   * Setting Authorization to already existing Request Specification for default zone.
   *
   * @param rspec Request Specification
   * @param username Login Username
   * @param pwd Login password
   * @return request Spec
   */
  public RequestSpecification setAuth(RequestSpecification rspec, String username, String pwd) {
    rspec.auth().basic(username, pwd);
    return rspec;
  }
  
  /**
  * Generate request specification and setting Authorization to custom zones. 
  *
  * @param host Enter non default hostname
  * @param username UserName
  * @param pwd Passsword
  * @return request Spec
  */
  public RequestSpecification setRequestSpecForZones(String host, String username, String pwd) {
    RequestSpecification rspec = getRequestSpecification(host);
    rspec.auth().basic(username, pwd);
    
    return rspec;
  }

  /**
   * Setting Response Specification for Default Host.
   *
   * @author chandru05
   * @return response Builder
   */
  public ResponseSpecification getReponseSpecification() {
    ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();
    responseBuilder.expectStatusCode(200);
    // responseBuilder.log(LogDetail.ALL);
    // responseBuilder.expectResponseTime(lessThan(3L),TimeUnit.SECONDS);
    return responseBuilder.build();
  }

  public RequestSpecification setQueryParameters(RequestSpecification rspec,
      String param, String value) {
    rspec.queryParam(param, value);
    return rspec;
  }

  public RequestSpecification setQueryParameters(RequestSpecification rspec,
      Map<String, Object> param) {
    rspec.queryParams(param);
    return rspec;
  }

  public RequestSpecification setFormParameters(RequestSpecification rspec,
      Map<String, String> param) {
    rspec.formParams(param);
    return rspec;
  }

  public RequestSpecification createPathParameters(RequestSpecification rspec,
      String param, String value) {
    rspec.pathParam(param, value);
    return rspec;
  }

  public RequestSpecification createBodyParameters(RequestSpecification rspec,
      JSONObject param) {
    rspec.body(param.toString());
    return rspec;
  }

  public RequestSpecification createBodyParameters(RequestSpecification rspec,
      ArrayList<?> param) {
    rspec.body(param);
    return rspec;
  }

  public RequestSpecification createBodyParameters(RequestSpecification rspec,
      String param) {
    rspec.body(param);
    return rspec;
  }
  
  /**
   * File Upload.
   *
   * @param rspec request Specification
   * @param filePath File path to upload
   * @return request Spec
   */
  public RequestSpecification fileUpload(RequestSpecification rspec,
      String filePath) {
    rspec.multiPart("file", new File(filePath)).formParam("file", filePath)
        .contentType("multipart/form-data");
    return rspec;
  }
  

  
  public Response getResponse() {
    return given().get(endpoint);
  }
  
  /**
   * Get Response for UserEndpoints.
   *
   * @author chandru05
   * @param requestspec request Specification
   * @param type Request Type
   * @param responsespec Response Specification
   * @return response
   */
  public Response getResponse(RequestSpecification requestspec, String type,
      ResponseSpecification responsespec) {

    Response resp = null;
    if (type.equalsIgnoreCase("get")) {
      resp = given().spec(requestspec).get(endpoint);
    } else if (type.equalsIgnoreCase("post")) {
      resp = given().spec(requestspec).post(endpoint);
    } else if (type.equalsIgnoreCase("put")) {
      resp = given().spec(requestspec).put(endpoint);
    } else if (type.equalsIgnoreCase("delete")) {
      resp = given().spec(requestspec).delete(endpoint);
    } else {
      log.error("Invalid Type");
    }
    
    try {
      resp.then().spec(responsespec);
    } catch (AssertionError e) {
      
      QueryableRequestSpecification qrs = SpecificationQuerier.query(requestspec);
      URI fullUri = null;
      try {
        URI baseUri = new URI(qrs.getURI());
        fullUri = baseUri.resolve(endpoint);
      } catch (URISyntaxException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      
      log.info("EndPoint is: " + fullUri.toString());
      log(Status.FAIL, "EndPoint is: " + fullUri.toString());

      log.info("Query Parameter is: " + qrs.getQueryParams());
      log.info("Path Parameter is: " + qrs.getPathParams());
      log.info("Form Parameter is: " + qrs.getFormParams());
      log.info("Request Parameter is: " + qrs.getRequestParams());
      log.info("Headers are: " + qrs.getHeaders().asList().toString());
      log.info("RequestBody is: " + qrs.getBody());
      log(Status.FAIL, "RequestBody is: " + qrs.getBody());
      
      log.info("Response Details: " + resp.getStatusCode() + " " + resp.getStatusLine() + " " + resp.asPrettyString());
      log(Status.FAIL, "Response Details: " + resp.getStatusCode() + " " + resp.getStatusLine() + " " + resp.asPrettyString());
      Assert.fail("Expected Response Code is not matching with Actual", e);
      
    } catch (Exception e1) {
      log.error("Failed to validate response code: " + e1.getMessage());
      log(Status.FAIL, "Failed to validate response code: " + e1.getMessage());
    }
    
    return resp;
  }

  public JsonPath getJsonPath(Response res) {
    String path = res.asString();
    return new JsonPath(path);
  }

  public XmlPath getXmlPath(Response res) {
    String path = res.asString();
    return new XmlPath(path);
  }

  public void resetBasePath() {
    RestAssured.basePath = null;
  }

  public void setContentType(ContentType type) {
    given().contentType(type);
  }
  
  /**
   * Clear Json Value for any JSON Object.
   *
   * @param jsonObj pass JsonObj value
   * @return jsonObj
   */
  public JSONObject clearJsonValue(JSONObject jsonObj) {
    while (jsonObj.length() > 0) {
      jsonObj.remove(jsonObj.keys().next());
    }
    return jsonObj;
  }

  public String getBasePath() {
    return "rest/";
  }
  
  /**
   * Get Checksum for File.
   *
   * @param file Enter File
   * @return checksumMd5 value
   */
  public String getFileChecksum(File file) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    byte[] byteArray = new byte[1024];
    int bytesCount = 0;
    try {
      while ((bytesCount = fis.read(byteArray)) != -1) {
        digest.update(byteArray, 0, bytesCount);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    ;
    try {
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] bytes = digest.digest();

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }
  
  /**
   * Get ISO8601String for Date.
   *
   * @param localDate Local Date
   * @return dateFormat
   */
  public String getIso8601StringForDate(Date localDate) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
        Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat.format(localDate);
  }
}
