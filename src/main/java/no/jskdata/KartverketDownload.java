package no.jskdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * A way to access http://data.kartverket.no/download/ from java
 */
public class KartverketDownload extends Downloader {

    private final String username;
    private final String password;

    private Map<String, String> cookies;

    private final Set<String> datasetIds = new HashSet<>();

    private final String baseUrl = "http://data.kartverket.no";

    private final Gson gson = new Gson();

    // the big jobs are slow and 30s is the same as server php timeout.. for big
    // downloads, this is not enough
    private static final int TIMEOUT_MILLIS = 1000 * 30;

    private static final int MAX_CHART_SIZE = 50;

    private static final Map<String, String> jsonUrlByServiceName;

    static {
        Map<String, String> sn = new HashMap<>();
        sn.put("fylker", "http://www.norgeskart.no/json/norge/fylker.json");
        sn.put("fylker-utm32", "http://www.norgeskart.no/json/norge/fylker-utm32.json");
        sn.put("fylker-utm33", "http://www.norgeskart.no/json/norge/fylker-utm33.json");
        sn.put("fylker-utm35", "http://www.norgeskart.no/json/norge/fylker-utm35.json");
        sn.put("kommuner", "http://www.norgeskart.no/json/norge/kommuner.json");
        sn.put("kommuner-utm32", "http://www.norgeskart.no/json/norge/kommuner-utm32.json");
        sn.put("kommuner-utm33", "http://www.norgeskart.no/json/norge/kommuner-utm33.json");
        sn.put("kommuner-utm35", "http://www.norgeskart.no/json/norge/kommuner-utm35.json");
        jsonUrlByServiceName = Collections.unmodifiableMap(sn);
    }

    public KartverketDownload(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void login() throws IOException {

        Connection.Response res1 = Jsoup.connect(baseUrl + "/download/").execute();
        Document document = res1.parse();

        Element loginForm = document.getElementById("user-login-form");
        String loginUrl = baseUrl + loginForm.attr("action");
        Map<String, String> formParameters = new HashMap<>();
        formParameters.putAll(formInputParameters(loginForm));
        formParameters.put("name", username);
        formParameters.put("pass", password);

        Connection.Response res2 = Jsoup.connect(loginUrl).timeout(TIMEOUT_MILLIS).cookies(res1.cookies())
                .method(Method.POST).data(formParameters).execute();
        cookies = res2.cookies();

    }

    public void logout() throws IOException {
        if (!hasCookies()) {
            return;
        }
        Jsoup.connect(baseUrl + "/download/user/logout").cookies(cookies).execute();
        cookies = Collections.emptyMap();
    }

    public void dataset(String datasetId) throws IOException {
        datasetIds.add(datasetId);
    }

    private Map<String, String> formInputParameters(Element form) {
        Map<String, String> parameters = new HashMap<>();
        for (Element input : form.select("input")) {
            parameters.put(input.attr("name"), input.attr("value"));
        }
        return Collections.unmodifiableMap(parameters);
    }

    private String firstLineWithMatch(String lines, String... match) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(lines));
            String line = null;
            while ((line = reader.readLine()) != null) {
                boolean allMatch = true;
                ;
                for (String m : match) {
                    if (line.indexOf(m) < 0) {
                        allMatch = false;
                        break;
                    }
                }
                if (allMatch) {
                    return line;
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean hasCookies() {
        return cookies != null && !cookies.isEmpty();
    }

    public void clear() {
        datasetIds.clear();
    }

    private HttpURLConnection openConnection(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            conn.setRequestProperty("Cookie", cookie.getKey() + "=" + cookie.getValue());
        }
        return conn;
    }

    private HttpURLConnection openConnectionWithRetry(String url) throws IOException {
        IOException lastException = null;
        int lastStatus = -1;
        for (int i = 0; i < 10; i++) {
            try {
                lastException = null;
                HttpURLConnection conn = openConnection(url);
                lastStatus = conn.getResponseCode();
                if (lastStatus == 200) {
                    return conn;
                }
                Thread.sleep(200 * i);
            } catch (IOException e) {
                lastException = e;
            } catch (InterruptedException e) {
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        throw new IOException("url " + url + " returned status code " + lastStatus);
    }

    @Override
    public void download(Receiver receiver) throws IOException {

        for (String datasetId : datasetIds) {

            Set<String> restFileNames = new HashSet<>();

            Connection.Response r = Jsoup.connect(baseUrl + "/download/content/" + datasetId).cookies(cookies)
                    .execute();
            Document d = r.parse();

            String line = firstLineWithMatch(d.data(), "kms_widget", "service_name");
            if (line == null) {
                throw new IOException("could not find kms_widget with service_name");
            }
            String json = line.substring(line.indexOf('{'), line.lastIndexOf('}') + 1);

            @SuppressWarnings("unchecked")
            Map<String, Object> m = gson.fromJson(json, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, String> conf = (Map<String, String>) m.get("kms_widget");

            String serviceName = conf.get("service_name");
            boolean isSingleFile = serviceName == null;

            if (isSingleFile) {
                String filename = conf.get("selection_details");
                if (!fileNameFilter.test(filename)) {
                    return;
                }
                restFileNames.add(filename);
            } else {
                String jsonUrl = jsonUrlByServiceName.get(serviceName);
                if (jsonUrl == null) {
                    throw new IOException("unknown service name: " + serviceName + " for dataset: " + datasetId);
                }

                HttpURLConnection conn = (HttpURLConnection) new URL(jsonUrl).openConnection();
                FeatureCollection featureCollection = gson.fromJson(new InputStreamReader(conn.getInputStream()),
                        FeatureCollection.class);
                for (Feature feature : featureCollection.features) {

                    // norgeskart-xdm.js ported to java
                    String name = feature.get("n");
                    String filename;
                    if (feature.id == null) {
                        filename = name + conf.get("selection_details");
                    } else if (feature.get("f") != null) {
                        filename = feature.get("f") + conf.get("selection_details");
                    } else {
                        filename = feature.id + "_" + name + conf.get("selection_details");
                    }

                    filename = conf.get("service_layer") + "_" + filename.replace(' ', '_') + '_'
                            + conf.get("dataformat");
                    filename = filename + ("MrSID".equals(conf.get("dataformat")) ? ".sid" : ".zip");

                    filename = filename.replace('æ', 'e');
                    filename = filename.replace('Æ', 'E');
                    filename = filename.replace('ø', 'o');
                    filename = filename.replace('Ø', 'O');
                    filename = filename.replace('å', 'a');
                    filename = filename.replace('Å', 'A');

                    if (!fileNameFilter.test(filename)) {
                        continue;
                    }

                    restFileNames.add(filename);
                }
            }

            // check if some files can be downloaded without ordering. typically
            // when already ordered.
            for (String fileName : new ArrayList<>(restFileNames)) {
                String url = DatasetUrl.createUrl(datasetId, fileName);
                if (url == null) {
                    continue;
                }
                HttpURLConnection conn = openConnection(url);
                // give 403 if not ordered. 200 if ordered.
                if (conn.getResponseCode() == 200) {
                    receiver.receive(fileName, conn.getInputStream());
                    restFileNames.remove(fileName);
                }
            }

            // order if there are anything left
            for (List<String> someFileNames : Lists.partition(new ArrayList<>(restFileNames), MAX_CHART_SIZE)) {

                // click add to chart
                Element addToChartForm = d.select("form").get(0);
                String addToChartUrl = baseUrl + addToChartForm.attr("action");
                Map<String, String> formParameters = new HashMap<>();
                formParameters.putAll(formInputParameters(addToChartForm));
                formParameters.put("line_item_fields[field_selection][und][0][value]", gson.toJson(someFileNames));
                formParameters.put("line_item_fields[field_selection_text][und][0][value]",
                        isSingleFile ? "1 samlet fil" : someFileNames.size() + " filer");
                Connection.Response res2 = Jsoup.connect(addToChartUrl).timeout(TIMEOUT_MILLIS).cookies(cookies)
                        .method(Method.POST).data(formParameters).execute();
                if (!res2.body().contains("ble lagt i")) {
                    throw new IOException("not in chart");
                }

                // click checkout
                Connection.Response checkoutResult = Jsoup.connect(baseUrl + "/download/checkout").cookies(cookies)
                        .execute();
                Document checkoutDocument = checkoutResult.parse();

                // click continue
                Element continueForm = checkoutDocument.select("form").get(0);
                String continueUrl = baseUrl + continueForm.attr("action");
                Map<String, String> continueParameters = new HashMap<>();
                continueParameters.putAll(formInputParameters(continueForm));
                continueParameters.put("op", "Fortsett");
                Jsoup.connect(continueUrl).timeout(TIMEOUT_MILLIS).cookies(cookies).data(continueParameters)
                        .method(Method.POST).execute();

                // try to figure out url without going to checkout list
                for (String fileName : someFileNames) {
                    String url = DatasetUrl.createUrl(datasetId, fileName);
                    if (url == null) {
                        continue;
                    }

                    HttpURLConnection conn = openConnectionWithRetry(url);
                    if (conn.getResponseCode() != 200) {
                        continue;
                    }

                    receiver.receive(fileName, conn.getInputStream());
                    restFileNames.remove(fileName);
                }

            }

            // check that we got all files
            if (!restFileNames.isEmpty()) {
                throw new IOException("did not find urls for " + restFileNames);
            }

        }
    }

    private static class FeatureCollection {
        private List<Feature> features;
    }

    private static class Feature {

        private Integer id;
        private Map<String, String> properties;

        public String get(String key) {
            return get(key, null);
        }

        public String get(String key, String defaultValue) {
            return properties == null ? defaultValue : properties.get(key);
        }

    }

}
