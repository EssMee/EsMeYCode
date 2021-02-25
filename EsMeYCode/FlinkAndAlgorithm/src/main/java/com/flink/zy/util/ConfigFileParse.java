package com.flink.zy.util;

import java.io.*;
import java.util.Properties;

public class ConfigFileParse {
    public String confPath;

    public ConfigFileParse(String confpath) {
        InputStream is = null;
        BufferedReader br = null;
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(confpath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
