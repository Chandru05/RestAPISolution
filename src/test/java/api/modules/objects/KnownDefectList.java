package api.modules.objects;

import api.base.setup.JsonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Read the valid defect details present in JSON File.
 *
 */

public class KnownDefectList {

    private static Map<String, String> defectMap = new HashMap<String, String>();
    private String testName;
    private String defectId;

    static {
      try {
        KnownDefectList[] defectList = JsonUtil.deserializeJson("data/DefectList.json", KnownDefectList[].class);
        
        for (KnownDefectList dl : defectList) {
          defectMap.put(dl.getTestName(), dl.getDefectId());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }    
    }
    
    public static Map<String, String> getDefectMap() {
      return defectMap;
    }

    public static String getDefectByTestName(String testName) {
      return defectMap.get(testName);
    }

    public String getTestName() {
      return testName;
    }

    public void setTestName(String testName) {
      this.testName = testName;
    }

    public String getDefectId() {
      return defectId;
    }

    public void setDefectId(String defectId) {
      this.defectId = defectId;
    }


}
