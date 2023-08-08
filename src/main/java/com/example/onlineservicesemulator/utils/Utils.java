package com.example.onlineservicesemulator.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String ROOT_DIRECTORY = System.getProperty("user.dir");

    public static String getFileDestination(String fileName) {
        String relativePath = "/uploadedfiles/" + fileName;
        return ROOT_DIRECTORY + relativePath;
    }

    public static String extractValue(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static short calculateLongestServiceString(List<String> servicesNames) {
        short max = 0;
        for (String serviceName : servicesNames) {
            if (serviceName.length() > max) {
                max = (short) serviceName.length();
            }
        }
        return max;
    }
}
