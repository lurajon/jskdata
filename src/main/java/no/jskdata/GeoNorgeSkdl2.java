package no.jskdata;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection.KeyVal;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

/**
 * A way to access https://download.geonorge.no/skdl2/ from java
 */
public class GeoNorgeSkdl2 extends Downloader {

    private final String username;
    private final String password;

    private final String baseUrl = "https://download.geonorge.no";
    private final String ngisnl2 = baseUrl + "/skdl2/nl2prot/ngisnl2";

    private final Map<String, String> cookies = new HashMap<>();

    private final Table<String, Integer, String> lookup = HashBasedTable.create();

    private final Multimap<String, Integer> selection = HashMultimap.create();

    private final static int TIMEOUT_MS = 1000 * 60;

    public GeoNorgeSkdl2(String username, String password) {
        this.username = username;
        this.password = password;
        try {
            login();
        } catch (IOException e) {
            throw new IllegalStateException("could not log in", e);
        }
    }

    private void login() throws IOException {

        Connection.Response r1 = Jsoup.connect(baseUrl + "/skdl2/").execute();
        addCookies(r1.cookies());

        Map<String, String> params = new HashMap<>();
        params.put("userId", username);
        params.put("password", password);
        Connection.Response r2 = Jsoup.connect(baseUrl + "/skdl2/nl2unprot/logon1").cookies(cookies).data(params)
                .method(Method.POST).execute();
        addCookies(r2.cookies());
        Document d2 = r2.parse();

        // extract selects
        for (Element select : d2.select("select")) {
            String name = select.attr("name");
            for (Element option : select.select("option")) {
                Integer value = Integer.valueOf(option.attr("value"));
                String text = option.text();
                lookup.put(name, value, text);
            }
        }

        if (lookup.isEmpty()) {
            throw new IOException("could not download lookup tables");
        }
    }

    private void checkLookupKey(String key) {
        if (!lookup.containsRow(key)) {
            throw new IllegalArgumentException("unknown key: " + key + ". must be one of: " + lookup.rowKeySet());
        }
    }

    public Map<Integer, String> lookup(String key) {
        checkLookupKey(key);
        return Collections.unmodifiableMap(lookup.row(key));
    }

    public void select(String key, Integer value) throws IllegalArgumentException {
        checkLookupKey(key);
        if (!lookup.contains(key, value)) {
            throw new IllegalArgumentException(
                    "unknown value: " + value + " for key: " + key + ". must be one of: " + lookup.row(key));
        }
        // remove [all] selection
        selection.remove(key, Integer.valueOf(0));
        selection.put(key, value);
    }

    public void select(String key, String label) throws IllegalArgumentException {
        checkLookupKey(key);
        boolean found = false;
        for (Map.Entry<Integer, String> e : lookup.row(key).entrySet()) {
            if (e.getValue().equals(label)) {
                found = true;
                selection.remove(key, Integer.valueOf(0));
                selection.put(key, e.getKey());
            }
        }
        if (!found) {
            throw new IllegalArgumentException(
                    "unknown label: " + label + " for key: " + key + ". should be one of: " + lookup.row(key).values());
        }
    }

    @Override
    public void dataset(String dataset) {
        select("datasett", dataset);
    }

    public Collection<Integer> selection(String key) throws IllegalArgumentException {
        checkLookupKey(key);
        Set<Integer> s = new HashSet<>(selection.get(key));
        s.remove(Integer.valueOf(0));
        return Collections.unmodifiableCollection(s);
    }

    public void clearSelection(String key) throws IllegalArgumentException {
        checkLookupKey(key);
        selection.removeAll(key);
    }

    public HttpURLConnection download() throws IOException {

        // clear previous file list
        ngisnl2("TOMNEDLL");

        // search
        ngisnl2("SUBSOK1");

        // select files matching filter
        Set<String> selectedFileNames = new HashSet<>();
        Connection.Response r = ngisnl2("OPENALL");
        Document d = r.parse();
        for (Element fileElement : d.select("input.sokres")) {
            String name = fileElement.attr("name");
            if (!name.startsWith("fcb_")) {
                continue;
            }
            String fileName = fileElement.nextSibling().toString().trim();
            if (!fileNameFilter.test(fileName)) {
                continue;
            }
            Map<String, String> fileSelectionParams = new HashMap<>();
            fileSelectionParams.put(name + ".x", "1");
            fileSelectionParams.put(name + ".y", "1");
            ngisnl2("", fileSelectionParams);
            selectedFileNames.add(fileName);
        }

        if (selectedFileNames.isEmpty()) {
            return null;
        }

        // add selected to download list
        ngisnl2("OVFNEDLL");

        // download
        HttpURLConnection conn = (HttpURLConnection) new URL(ngisnl2).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            conn.setRequestProperty("Cookie", cookie.getKey() + "=" + cookie.getValue());
        }
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);

        StringBuilder param = new StringBuilder();
        for (Map.Entry<String, Integer> e : selection.entries()) {
            param.append(e.getKey());
            param.append('=');
            param.append(e.getValue().intValue());
            param.append('&');
        }
        param.append("f1action=LASTNED&FILBANE=FAKTISK");

        OutputStream out = conn.getOutputStream();
        out.write(param.toString().getBytes("UTF-8"));
        out.flush();
        out.close();

        if (!"application/octet-stream".equals(conn.getContentType())) {
            throw new IOException("got content type: " + conn.getContentType());
        }

        return conn;

    }

    @Override
    public void download(Receiver receiver) throws IOException {
        Set<Integer> kommuner = new HashSet<>(selection.get("kommune"));
        kommuner.remove(Integer.valueOf(0));

        if (kommuner.isEmpty()) {
            kommuner.addAll(lookup.row("kommune").keySet());
            kommuner.remove(Integer.valueOf(0));
        }

        for (Integer kommune : kommuner) {
            if (receiver.shouldStop()) {
                break;
            }

            // unselect and select a single one for each download
            clearSelection("kommune");
            select("kommune", kommune);

            HttpURLConnection conn = download();
            if (conn == null) {
                continue;
            }

            ZipInputStream zis = new ZipInputStream(conn.getInputStream());
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (receiver.shouldStop()) {
                    break;
                }
                String path = entry.getName();
                String fileName = path.substring(path.lastIndexOf('\\') + 1);
                receiver.receive(fileName, zis);
            }

        }
    }

    public void clear() {
        selection.clear();
    }

    private Connection.Response ngisnl2(String action) throws IOException {
        return ngisnl2(action, Collections.emptyMap());
    }

    private Connection.Response ngisnl2(String action, Map<String, String> extraParams) throws IOException {
        return Jsoup.connect(ngisnl2).timeout(TIMEOUT_MS).data(toKeyVals(selection)).data("f1action", action)
                .data("FILBANE", "FAKTISK").data(extraParams).method(Method.POST).cookies(cookies).execute();
    }

    private List<Connection.KeyVal> toKeyVals(Multimap<String, ?> params) {
        List<Connection.KeyVal> keyVals = new ArrayList<>();
        for (Map.Entry<String, Integer> e : selection.entries()) {
            keyVals.add(KeyVal.create(e.getKey(), e.getValue().toString()));
        }
        return Collections.unmodifiableList(keyVals);
    }

    private void addCookies(Map<String, String> cookiesToAdd) {
        cookies.putAll(cookiesToAdd);
    }

}
