package api.modules.generic;

import api.base.setup.Constants;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.Logger;

public class DbQueries {
  
  private Logger log;
  
  public DbQueries(Logger log1) {
    this.log = log1;
  }
  
  private String getResult(String query) {
    String unifiedId = null;
    String[] res = query.split("[ ]");
    try {
      if (Constants.con != null) {
        log.info("Connection is Valid!!!");
        log.info("The Database Query to be executed: {}", query);
        PreparedStatement pst;
        pst = Constants.con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
          if (res[1].equalsIgnoreCase("count(*)")) {
            unifiedId = rs.getString(res[3]);
          } else {
            unifiedId = rs.getString(res[1]);
          }
          //log.info("UnifiedId: " + unifiedId);
        }
      } else {
        log.info("Failed to Connect");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    log.info("Output of the executed DB Query: " + unifiedId);
    return unifiedId;
  }
  
  /**
   * Get OAuth Token.
   *
   * @param userName UserName
   * @param zoneId zoneId
   * @return result value
   */
  public String getOauthToken(String userName, String zoneId) {
    log.info("Getting OAuth Token....");
    return getResult("SELECT accesstoken FROM public.ss_oauthtoken where username='" + userName + "' and zoneid='" + zoneId
        + "' order by ID desc limit 1");
  }

}
