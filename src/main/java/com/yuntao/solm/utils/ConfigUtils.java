package com.yuntao.solm.utils;

import java.util.*;

/**
 * Created by shengshan.tang on 2015/11/24 at 16:46
 */
public class ConfigUtils {

    private static ResourceBundle resourceBundle;

    public  static void init(String model){
        resourceBundle = ResourceBundle.getBundle("config-"+model);
    }

    public static String getString(String key){
        return resourceBundle.getString(key);
    }
}
