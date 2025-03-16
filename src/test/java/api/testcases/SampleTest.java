package api.testcases;

import api.base.setup.ApiBaseSetup;
import api.base.setup.Auth;
import api.base.setup.reporting.Author;
import api.modules.admin.AdminOperations;
import api.modules.objects.ExternalUserRegistrationDetails;
import api.modules.user.UserOperations;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SampleTest extends ApiBaseSetup {
  
  AdminOperations ao;
  UserOperations uo;
  
  @BeforeClass
  public void beforeClass() {
    ao = new AdminOperations();
    uo = new UserOperations();
  }
    
  // disabled the password policy by default.
  
  @Author("User1")
  @Test(priority = 1, description = "disabled the password policy by default")
  public void setPasswordPolicyDisableByDefault() {
    requestSpec.auth().basic(Auth.ADMIN_USERNAME, Auth.ADMIN_PASSWORD);
    ao.setPasswordPolicy(requestSpec, responseSpec, false);
  }
  
  
  @Author("User1")
  @Test(priority = 2, description = " self-registration",
          dependsOnMethods = { "setPasswordPolicyDisableByDefault"},
          dataProvider = "phonenumberenabled")
  public void extUserRegisterPhNumEnforced(String name, String firstname, String lastname,
                                              String password, String phonenumber, String euet) {



    String emailAddress = "user1@test.com";

    ru.setAuth(requestSpec, Auth.TEST_USERNAME, Auth.TEST_PASSWORD);

    ExternalUserRegistrationDetails extDetails = null;

    extDetails = new ExternalUserRegistrationDetails(emailAddress,
        firstname, lastname, password, euet);

    Response response = uo.registerExternalUser(requestSpec, extDetails, responseSpec);

  }
  
}
