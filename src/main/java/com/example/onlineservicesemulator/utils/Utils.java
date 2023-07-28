package com.example.onlineservicesemulator.utils;

public class Utils {
    private static final String ROOT_DIRECTORY = System.getProperty("user.dir");
    public static String getFileDestination(String fileName) {
        String fileFormat = fileName.substring(fileName.length() - 3);
        String relativePath = "/uploadedfiles/" + fileName;
        return ROOT_DIRECTORY + relativePath;
    }
}
