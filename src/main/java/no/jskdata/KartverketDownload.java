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
import java.util.logging.Level;

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

    private final Set<String> restFileNames = new HashSet<>();

    private final Set<String> urls = new HashSet<>();

    private final String baseUrl = "http://data.kartverket.no";

    private final Gson gson = new Gson();

    // the big jobs are slow and 30s is the same as server php timeout.. for big
    // downloads, this is not enough
    private static final int TIMEOUT_MILLIS = 1000 * 30;

    private static final int MAX_CHART_SIZE = 50;

    private static final Map<String, String> jsonUrlByServiceName;

    private static final Map<String, String> urlPrefixByDatasetName;

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

        Map<String, String> udn = new HashMap<>();
        udn.put("n50-kartdata-utm-33-kommunevis-inndeling",
                "http://data.kartverket.no/download/system/files/kartdata/n50/kommuner/");
        for (int utm : new int[] { 32, 33, 35 }) {
            udn.put("administrative-fylker-utm-" + utm + "-fylkesinndeling",
                    "http://data.kartverket.no/download/system/files/grensedata/fylker/");
            udn.put("vbase-utm-" + utm, "http://data.kartverket.no/download/system/files/vegdata/vbase/kommuner/");
            udn.put("vbase-utm-" + utm + "-fylkesinndeling",
                    "http://data.kartverket.no/download/system/files/vegdata/vbase/fylker/");
        }
        urlPrefixByDatasetName = Collections.unmodifiableMap(udn);
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
        Connection.Response r = Jsoup.connect(baseUrl + "/download/content/" + datasetId).cookies(cookies).execute();
        Document d = r.parse();

        String line = firstLineWithMatch(d.data(), "kms_widget");
        String json = line.substring(line.indexOf('{'), line.lastIndexOf('}') + 1);

        @SuppressWarnings("unchecked")
        Map<String, Object> m = gson.fromJson(json, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> conf = (Map<String, String>) m.get("kms_widget");

        String serviceName = conf.get("service_name");
        String jsonUrl = jsonUrlByServiceName.get(serviceName);
        if (jsonUrl == null) {
            throw new IOException("unknown service name: " + serviceName);
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

            if ("MrSID".equals(conf.get("dataformat"))) {
                filename = conf.get("service_layer") + "_" + filename.replace(' ', '_') + '_' + conf.get("dataformat")
                        + ".sid";
            } else {
                filename = conf.get("service_layer") + "_" + filename.replace(' ', '_') + '_' + conf.get("dataformat")
                        + ".zip";
            }
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

        // check the download list for previous downloads that are still valid
        downloadList();

        // use the basket for the rest. hopefully no need for this as of
        // createUrl step above
        for (List<String> someFileNames : Lists.partition(new ArrayList<>(restFileNames), MAX_CHART_SIZE)) {

            // click add to chart
            Element addToChartForm = d.select("form").get(0);
            String addToChartUrl = baseUrl + addToChartForm.attr("action");
            Map<String, String> formParameters = new HashMap<>();
            formParameters.putAll(formInputParameters(addToChartForm));
            formParameters.put("line_item_fields[field_selection][und][0][value]", gson.toJson(someFileNames));
            formParameters.put("line_item_fields[field_selection_text][und][0][value]",
                    someFileNames.size() + " filer");
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
                String url = createUrl(datasetId, fileName);
                if (url != null) {
                    restFileNames.remove(fileName);
                    urls.add(url);
                }
            }

        }

        // get the rest of the urls
        downloadList();

        // check that we got all files
        if (!restFileNames.isEmpty()) {
            throw new IOException("did not find urls for " + restFileNames);
        }

    }

    private void downloadList() throws IOException {

        if (restFileNames.isEmpty()) {
            return;
        }

        try {

            Connection.Response downloadResult = Jsoup.connect(baseUrl + "/download/mine/downloads")
                    .timeout(TIMEOUT_MILLIS).cookies(cookies).execute();
            Document downloadDocument = downloadResult.parse();
            for (Element a : downloadDocument.select("a[href]")) {
                String url = a.attr("href");
                if (!url.startsWith("http")) {
                    continue;
                }
                String filename = url.substring(url.lastIndexOf('/') + 1);
                if (!restFileNames.remove(filename)) {
                    continue;
                }
                urls.add(url);
            }

        } catch (IOException e) {
            getLogger().log(Level.INFO, "ignoring download list exception", e);
        }
    }

    private Map<String, String> formInputParameters(Element form) {
        Map<String, String> parameters = new HashMap<>();
        for (Element input : form.select("input")) {
            parameters.put(input.attr("name"), input.attr("value"));
        }
        return Collections.unmodifiableMap(parameters);
    }

    private String firstLineWithMatch(String lines, String match) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(lines));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf(match) >= 0) {
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

    public Set<String> urls() {
        return Collections.unmodifiableSet(urls);
    }

    public void clear() {
        restFileNames.clear();
        urls.clear();
    }

    public HttpURLConnection openConnection(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            conn.setRequestProperty("Cookie", cookie.getKey() + "=" + cookie.getValue());
        }
        return conn;
    }

    @Override
    public void download(Receiver receiver) throws IOException {
        for (String url : urls()) {
            if (receiver.shouldStop()) {
                break;
            }
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            HttpURLConnection conn = openConnection(url);
            receiver.receive(fileName, conn.getInputStream());
        }
    }

    static String createUrl(String datasetId, String fileName) {
        String urlPrefix = urlPrefixByDatasetName.get(datasetId);
        return urlPrefix == null ? null : urlPrefix + fileName;
    }

    private static class FeatureCollection {
        private List<Feature> features;
    }

    private static class Feature {

        private Integer id;
        private Map<String, String> properties;

        public String get(String key) {
            return properties == null ? null : properties.get(key);
        }
    }

}
