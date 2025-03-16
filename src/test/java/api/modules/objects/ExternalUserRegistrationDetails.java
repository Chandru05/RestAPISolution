package api.modules.objects;

/**
 * External User Registration POJO Class.
 */
public class ExternalUserRegistrationDetails {
  private String emailAddress;
  private String firstName;
  private String lastName;
  private String phonenumber;
  private String password;

  private String euet;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  private String type;
  
  /**
   * External User Registration details initialization when Phone Number is not disabled.
   * Phone Number is Enabled or Optional
   */
  public ExternalUserRegistrationDetails(String emailAddress, String firstName,
          String lastName, String phonenumber, String password, String euet) {
    setEmailAddress(emailAddress);
    setFirstName(firstName);
    setLastName(lastName);
    setPhonenumber(phonenumber);
    setPassword(password);
    setEuet(euet);
  }
  
  /**
   * External User Registration details initialization when Phone Number is disabled.
   */
  public ExternalUserRegistrationDetails(String emailAddress, String firstName,
          String lastName, String password, String euet) {
    setEmailAddress(emailAddress);
    setFirstName(firstName);
    setLastName(lastName);
    setPassword(password);
    setEuet(euet);
    setType("Disabled");

  }


  public String getEuet() {
    return euet;
  }

  public void setEuet(String euet) {
    this.euet = euet;
  }
  
  public String getemailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getfirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getlastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getphonenumber() {
    return phonenumber;
  }

  public void setPhonenumber(String phonenumber) {
    this.phonenumber = phonenumber;
  }

  public String getpassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
