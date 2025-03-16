package api.modules.admin;

import api.base.setup.endpoints.AdminEndpoints;
import api.modules.generic.RestUtilities;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class AdminOperations extends RestUtilities {
  
  /**
   * Set Password Policy.
   *
   * @param value password_policy_enabled
   * @return response
   */
  public Response setPasswordPolicy(RequestSpecification requestSpec, ResponseSpecification responseSpec, boolean value) {
    setEndPoint(AdminEndpoints.PASSWORD_POLICY);
    String payload = "{\"password_policy_enabled\": " + value + "}";
    return getResponse(createBodyParameters(requestSpec, payload), "put", responseSpec);
  }

}
