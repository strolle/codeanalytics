package se.metro.statistics.codeanalytics.google;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.*;
import com.google.api.client.http.*;
import com.google.api.client.json.*;
import com.google.api.client.json.jackson2.*;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import se.metro.statistics.codeanalytics.sonar.*;
import sun.security.krb5.internal.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SpreadsheetUtil {
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SPREADSHEET_SERVICE_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
    private static SpreadsheetService service = null;

    public static void initService() throws Exception {
        System.out.println(new File("jira-spread-0fcbed75c4ea.p12").getAbsolutePath());
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("account-1@jira-spread.iam.gserviceaccount.com")
                .setServiceAccountScopes(Arrays.asList("https://spreadsheets.google.com/feeds"))
                .setServiceAccountPrivateKeyFromP12File(new File("jira-spread-0fcbed75c4ea.p12"))
                .build();
        service = new SpreadsheetService("jira-spread");
        service.setOAuth2Credentials(credential);
    }

    public static SpreadsheetEntry getSpreadsheet(String sheetName) throws Exception {
        try {
            URL spreadSheetFeedUrl = new URL(SPREADSHEET_SERVICE_URL);

            SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(spreadSheetFeedUrl);
            spreadsheetQuery.setTitleQuery(sheetName);
            spreadsheetQuery.setTitleExact(true);
            SpreadsheetFeed spreadsheet = service.getFeed(spreadsheetQuery, SpreadsheetFeed.class);

            if (spreadsheet.getEntries() != null && spreadsheet.getEntries().size() == 1) {
                return spreadsheet.getEntries().get(0);
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static List<WorksheetEntry> getWorksheetFeed(SpreadsheetEntry spreadsheet) throws Exception {
        WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        return worksheetFeed.getEntries();
    }

    public static ListFeed getListFeed(WorksheetEntry worksheet) throws Exception {
        return service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
    }

    public static CellFeed getCellFeed(WorksheetEntry worksheet) throws Exception {
        return service.getFeed(worksheet.getCellFeedUrl(), CellFeed.class);
    }

    public static void addStat(WorksheetEntry workSheet, SonarProjectStat stat) throws Exception {
        ListFeed listFeed = SpreadsheetUtil.getListFeed(workSheet);

        if (listFeed.getEntries().size() > 0) {
            ListEntry lastRow = listFeed.getEntries().get(listFeed.getEntries().size() - 1);
            String date = lastRow.getCustomElements().getValue("Date");
            if (date.equals(stat.getDate())) {
                System.out.println("same date as last: " + date);
                return;
            }
            System.out.println("Date diff: " + date + " -> " + stat.getDate());
        } else {
            System.out.println("No previous data");
        }

        CellFeed cellFeed = SpreadsheetUtil.getCellFeed(workSheet);
        SpreadsheetUtil.setRow(cellFeed, listFeed.getEntries().size() + 2, stat.createSheetRow());
    }

    public static void setRow(CellFeed cellFeed, int row, List<String> values) throws Exception {
        for (int i = 0; i < values.size(); i++) {
            CellEntry cell = new CellEntry(row, i + 1, values.get(i));
            cellFeed.insert(cell);
        }
    }
}
