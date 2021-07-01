package StreamTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCopyTest {
    public static void main(String[] args) throws Exception {
        Map<String, List<Map<String, String>>> taskvar = new HashMap<String, List<Map<String, String>>>();
        Map<String, String> x = new HashMap<String, String>();
        x.put("x", "1");
        x.put("y", "1");
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        mapList.add(x);
        taskvar.put("xy", mapList);
        System.out.println(taskvar);
        Map<String, List<Map<String, String>>> taskvar2 = new HashMap<String, List<Map<String, String>>>();
        for(Map.Entry<String, List<Map<String, String>>> entry: taskvar.entrySet()){
            List<Map<String, String>> mapList2 = entry.getValue();
            List<Map<String, String>> mapList3 = new ArrayList<Map<String, String>>();
            for(int i = 0; i < mapList2.size(); i++){
                mapList3.add(new HashMap<String, String>(mapList2.get(i)));
            }
            taskvar2.put(entry.getKey(), mapList3);
        }
        taskvar2.get("xy").get(0).put("y", "2");
        System.out.println(taskvar);
        System.out.println(taskvar2);
    }
}
