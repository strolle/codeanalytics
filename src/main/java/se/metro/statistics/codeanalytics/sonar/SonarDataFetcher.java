package se.metro.statistics.codeanalytics.sonar;

import org.json.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

/**
 * Created by stefantrolle on 2017-10-06.
 */
public class SonarDataFetcher {
    private static final String RESOURCES_URL = "http://sonar.metro.se/api/resources?format=json&metrics=";

    public static final List<String> metricList = Arrays.asList(
            "complexity",
            "ncloc",
            "coverage",
            "classes",
            "duplicated_lines",
            "duplicated_lines_density",
            "branch_coverage",
            "vulnerabilities",
            "bugs",
            "code_smells"
    );

    public static Map<String, SonarProjectStat> fetchCodeMetrics() throws IOException {
        JSONArray resources = readJsonFromUrl(RESOURCES_URL + String.join(",", metricList));

        Map<String, SonarProjectStat> result = new HashMap<>();
        for (Object obj : resources) {
            JSONObject jsonObject = (JSONObject) obj;
            SonarProjectStat stat = new SonarProjectStat();

            stat.setName(jsonObject.getString("key"));
            stat.setDate(jsonObject.getString("date").substring(0, 10));

            JSONArray metrics = jsonObject.getJSONArray("msr");
            for (Object metricObj : metrics) {
                JSONObject metricJson = (JSONObject) metricObj;
                double value = metricJson.getDouble("val");
                String type = metricJson.getString("key");
                stat.getData().put(type, value);
            }
            result.put(stat.getName(), stat);

        }

        return result;
    }

    private static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(BufferedReader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
