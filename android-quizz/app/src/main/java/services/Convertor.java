package services;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

/**
 * Created by Florian on 01/02/2018.
 */

public class Convertor {
    public static HashMap LinkedTreeMapToHashMap(LinkedTreeMap linkedTreeMap) {
        HashMap hashMap = new HashMap();
        for (Object key : linkedTreeMap.keySet()) {
            hashMap.put(key, linkedTreeMap.get(key));
        }
        return hashMap;
    }
}
