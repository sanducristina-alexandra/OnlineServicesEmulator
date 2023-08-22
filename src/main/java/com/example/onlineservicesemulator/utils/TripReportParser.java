package com.example.onlineservicesemulator.utils;

public class TripReportParser {

    public static String getId(String reportData) {
        String reportIDPattern = "ReportId: (\\d+)";
        return Utils.extractValue(reportData, reportIDPattern);
    }

    public static String getStatus(String reportData) {
        String reportStatusPattern = "status: ([^ ]+)";
        return Utils.extractValue(reportData, reportStatusPattern);
    }

    public static String getStartTripDate(String reportData) {
        String reportStartDatePattern = "Date: (\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})";
        return Utils.extractValue(reportData, reportStartDatePattern);
    }

    public static String getEndTripDate(String reportData) {
        String reportEndDatePattern = "Date: (\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})";
        return Utils.extractValue(reportData, reportEndDatePattern);
    }

    public static String getCoordinates(String reportData) {
        String reportCoordinatesPattern = "coordinates: \\[([^\\]]+)\\]";
        return Utils.extractValue(reportData, reportCoordinatesPattern);
    }

    public static String getSosEmail(String reportData) {
        String reportSosEmailPattern = "sosEmailSent: (true|false)";
        return Utils.extractValue(reportData, reportSosEmailPattern);
    }
}
