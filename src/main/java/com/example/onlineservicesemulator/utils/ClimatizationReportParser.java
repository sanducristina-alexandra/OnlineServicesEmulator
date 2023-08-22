package com.example.onlineservicesemulator.utils;

public class ClimatizationReportParser {

    public static String getId(String reportData) {
        String reportIDPattern = "ReportId: (\\d+)";
        return Utils.extractValue(reportData, reportIDPattern);
    }

    public static String getDate(String reportData) {
        String reportDatePattern = "Date: (\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})";
        return Utils.extractValue(reportData, reportDatePattern);
    }

    public static String getAcPower(String reportData) {
        String acPowerPattern = "Air Conditioning Power: (\\d+)";
        return Utils.extractValue(reportData, acPowerPattern);
    }

    public static String getActionCode(String reportData) {
        String actionCodePattern = "Action Code: (\\d+)";
        return Utils.extractValue(reportData, actionCodePattern);
    }
}
