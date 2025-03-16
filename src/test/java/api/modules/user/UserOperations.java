package api.modules.user;

import api.base.setup.endpoints.UserEndpoints;
import api.modules.generic.RestUtilities;
import api.modules.objects.ExternalUserRegistrationDetails;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONObject;
import java.io.File;

public class UserOperations extends RestUtilities {
  
  
  public Response selfLogin(RequestSpecification rspec, ResponseSpecification responseSpec) {
    setEndPoint(UserEndpoints.SELF_LOGIN);
    return getResponse(rspec, "get", responseSpec);
  }
  
  public Response getUser(RequestSpecification rspec, ResponseSpecification responseSpec, String name) {
    setEndPoint(String.format(UserEndpoints.SELF_LOGIN, name));
    return getResponse(rspec, "get", responseSpec);
  }
  
  /**
   * Upload profile Image in User Profile.
   *
   * @author chandru05
   * @param photoPath Photo file location
   * @return response
   */
  public Response uploadProfileImage(RequestSpecification rspec, ResponseSpecification responseSpec, String photoPath) {
    setEndPoint(UserEndpoints.UPLOAD_PROFILEPIC);
    rspec.contentType("multipart/json");
    rspec.multiPart(new File(photoPath));
    return getResponse(rspec, "post", responseSpec);
  }
  
  
  /**
   * External User Registration.
   *
   * @param extDetails External User Details
   */
  public Response registerExternalUser(RequestSpecification rspec, ExternalUserRegistrationDetails extDetails, ResponseSpecification responseSpec) {

    setEndPoint(UserEndpoints.EXTUSER_REG);

    JSONObject payload = new JSONObject();
    payload.put("emailAddress", extDetails.getemailAddress());
    payload.put("euet", extDetails.getEuet());
    payload.put("firstName", extDetails.getfirstName());
    payload.put("lastName", extDetails.getlastName());
    if (extDetails.getType() == null || extDetails.getType().isBlank()) {
      payload.put("phone", extDetails.getphonenumber());
    }
    payload.put("password", extDetails.getpassword());
    return getResponse(createBodyParameters(rspec, payload), "post", responseSpec);
  }

}
