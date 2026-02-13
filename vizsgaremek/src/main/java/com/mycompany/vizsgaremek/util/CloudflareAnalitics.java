package com.mycompany.vizsgaremek.util;

import com.mycompany.vizsgaremek.config.KvFetcher;
import com.mycompany.vizsgaremek.config.base64Converters;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CloudflareAnalitics {

    private static final String API_TOKEN = base64Converters.base64Converter(KvFetcher.getDataFromKV("AnaliticsAPIToken"));
    private static final String ZONE_TAG = KvFetcher.getDataFromKV("ZoneId"); // carcomps.hu zone ID

    /**
     * GraphQL query a Cloudflare Analytics API-hoz
     */
    public static JSONObject getAnalytics(int days) {
        try {
            // Dátumok számítása
            LocalDate today = LocalDate.now();
            LocalDate since = today.minusDays(days);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            String sinceStr = since.format(formatter);
            String untilStr = today.format(formatter);

            // GraphQL query
            // GraphQL query
            String query = String.format(
                    "{"
                    + "  viewer {"
                    + "    zones(filter: {zoneTag: \"%s\"}) {"
                    + "      httpRequests1dGroups("
                    + "        limit: 1000,"
                    + "        filter: {"
                    + "          date_geq: \"%s\","
                    + "          date_leq: \"%s\""
                    + "        }"
                    + "      ) {"
                    + "        sum {"
                    + "          requests"
                    + "          pageViews"
                    + "          bytes"
                    + "        }"
                    + "        uniq {"
                    + "          uniques"
                    + "        }"
                    + "      }"
                    + "    }"
                    + "  }"
                    + "}",
                    ZONE_TAG, sinceStr, untilStr
            );

            // API request
            String url = "https://api.cloudflare.com/client/v4/graphql";

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);

            // Headers
            request.addHeader("Authorization", "Bearer " + API_TOKEN);
            request.addHeader("Content-Type", "application/json");

            // Body
            JSONObject body = new JSONObject();
            body.put("query", query);

            StringEntity entity = new StringEntity(body.toString(), "UTF-8");
            request.setEntity(entity);

            // Execute
            CloseableHttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            httpClient.close();

            return new JSONObject(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Egyedi látogatók száma
     */
    public static int getUniqueVisitors(int days) {
        JSONObject analytics = getAnalytics(days);

        if (analytics != null) {
            try {
                JSONObject data = analytics.getJSONObject("data");
                JSONObject viewer = data.getJSONObject("viewer");
                JSONArray zones = viewer.getJSONArray("zones");

                if (zones.length() > 0) {
                    JSONObject zone = zones.getJSONObject(0);
                    JSONArray groups = zone.getJSONArray("httpRequests1dGroups");

                    int totalUniques = 0;
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject group = groups.getJSONObject(i);
                        JSONObject uniq = group.getJSONObject("uniq");
                        totalUniques += uniq.getInt("uniques");
                    }

                    return totalUniques;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * Összes kérés száma
     */
    public static long getTotalRequests(int days) {
        JSONObject analytics = getAnalytics(days);

        if (analytics != null) {
            try {
                JSONObject data = analytics.getJSONObject("data");
                JSONObject viewer = data.getJSONObject("viewer");
                JSONArray zones = viewer.getJSONArray("zones");

                if (zones.length() > 0) {
                    JSONObject zone = zones.getJSONObject(0);
                    JSONArray groups = zone.getJSONArray("httpRequests1dGroups");

                    long totalRequests = 0;
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject group = groups.getJSONObject(i);
                        JSONObject sum = group.getJSONObject("sum");
                        totalRequests += sum.getLong("requests");
                    }

                    return totalRequests;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * Pageviews száma
     */
    public static long getPageViews(int days) {
        JSONObject analytics = getAnalytics(days);

        if (analytics != null) {
            try {
                JSONObject data = analytics.getJSONObject("data");
                JSONObject viewer = data.getJSONObject("viewer");
                JSONArray zones = viewer.getJSONArray("zones");

                if (zones.length() > 0) {
                    JSONObject zone = zones.getJSONObject(0);
                    JSONArray groups = zone.getJSONArray("httpRequests1dGroups");

                    long totalPageViews = 0;
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject group = groups.getJSONObject(i);
                        JSONObject sum = group.getJSONObject("sum");
                        totalPageViews += sum.getLong("pageViews");
                    }

                    return totalPageViews;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * Bandwidth (bytes)
     */
    public static long getBandwidth(int days) {
        JSONObject analytics = getAnalytics(days);

        if (analytics != null) {
            try {
                JSONObject data = analytics.getJSONObject("data");
                JSONObject viewer = data.getJSONObject("viewer");
                JSONArray zones = viewer.getJSONArray("zones");

                if (zones.length() > 0) {
                    JSONObject zone = zones.getJSONObject(0);
                    JSONArray groups = zone.getJSONArray("httpRequests1dGroups");

                    long totalBytes = 0;
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject group = groups.getJSONObject(i);
                        JSONObject sum = group.getJSONObject("sum");
                        totalBytes += sum.getLong("bytes");
                    }

                    return totalBytes;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        System.out.println("Last Day Statistics" + "\n"
                + "Analitics " + getAnalytics(0)+ "\n"
                + "UniqueVisitors " + getUniqueVisitors(30) + "\n"
                + "Bandwith " + getBandwidth(2) + "\n"
                + "pageViews " + getPageViews(30) + "\n"
                + "TotalRequest " + getTotalRequests(2) + "\n"
        );
    }
}
