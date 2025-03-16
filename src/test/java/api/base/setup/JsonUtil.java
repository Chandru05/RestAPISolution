package api.base.setup;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonUtil {
  
  static Logger log = LogManager.getLogger(ApiBaseSetup.class);
  
  /**
   *  Deserialize JSON from JSONObjects.
   *
   * @param <T> Generic
   * @param fileName JSON File reference
   * @param t Class
   * @return jsonObjects
   * @throws StreamReadException Exception
   * @throws DatabindException Exception
   * @throws IOException Exception
   */
  public static <T> T deserializeJson(String fileName, Class<T> t)
      throws StreamReadException, DatabindException, IOException {
    ObjectMapper objMapper = new ObjectMapper();
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream is = classloader.getResourceAsStream(fileName);
    // InputStream is = RestBaseClass.class.getClassLoader().getResourceAsStream(fileName);
    if (is == null) {
      log.error("Input Stream is NULL");
      return null;
    } else {
      return objMapper.readValue(is, t);
    }
  }

}
