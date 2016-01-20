package hr.fer.hmo.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {

  public static <K, V> Map<K, V> shallowCopy(Map<K, V> map) {
    Map<K, V> copiedMap = new HashMap<>(map.size());
    for (K key : map.keySet()) {
      copiedMap.put(key, map.get(key));
    }
    return copiedMap;
  }

}
