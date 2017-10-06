package se.metro.statistics.codeanalytics.sonar;

import java.util.*;

/**
 * Created by stefantrolle on 2017-10-06.
 */
public class SonarProjectStat {
    String name;
    String date;

    Map<String, Double> data = new HashMap<>();

    public List<String> createSheetRow(){
        List<String> result = new ArrayList<>();
        result.add(date);
        for(String metric : SonarDataFetcher.metricList) {
            result.add(("" + data.get(metric)).replace(".",","));
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getData() {
        return data;
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }
}
