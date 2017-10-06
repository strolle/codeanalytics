package se.metro.statistics.codeanalytics;

import com.google.gdata.data.spreadsheet.*;
import org.json.*;
import se.metro.statistics.codeanalytics.google.*;
import se.metro.statistics.codeanalytics.sonar.*;

import java.io.*;
import java.util.*;

public class UpdateCodeAnalytics {
    private static List<SheetInfo> allSheets = Arrays.asList(
            new SheetInfo(1,"com.metro.jobs:metro-jobs-webapp"),
            new SheetInfo(2,"se.metro:metro-write-service"),
            new SheetInfo(3,"se.metro:metro-static-service"),
            new SheetInfo(4,"se.metro:metro-tt-import"),
            new SheetInfo(5,"metro-webfront:metro-webfront:metro-webfront-server"),
            new SheetInfo(6,"se.metro:metro-statistic-service"),
            new SheetInfo(7,"se.metro:metro-layout-service")
    );


    public static void main(String[] args) {
        try {
            Map<String, SonarProjectStat> currentMetrics = SonarDataFetcher.fetchCodeMetrics();

            SpreadsheetUtil.initService();
            SpreadsheetEntry spreadsheet = SpreadsheetUtil.getSpreadsheet("sonar");
            List<WorksheetEntry> worksheets = SpreadsheetUtil.getWorksheetFeed(spreadsheet);

            for(SheetInfo sheetInfo : allSheets) {
                System.out.println("Process " + sheetInfo.name + " (" + sheetInfo.sheetPos + ")");
                WorksheetEntry workSheet = worksheets.get(sheetInfo.sheetPos);
                SpreadsheetUtil.addStat(workSheet, currentMetrics.get(sheetInfo.name));
            }
            System.out.println("Aaaand we're done...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
