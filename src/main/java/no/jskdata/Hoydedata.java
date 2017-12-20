package no.jskdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Java wrapper around https://hoydedata.no/Laserinnsyn
 */
public class Hoydedata extends Downloader {

    private String utmzone = "33"; // default fallback UTM Zone (EUREF-89)
    private final Set<String> datasetIds = new HashSet<>();
    private final String baseUrl = "https://hoydedata.no/LaserInnsyn/";

    public Hoydedata() {
    }

    public void setUtmzone(String utmzone) {
        this.utmzone = utmzone;
    }

    public void dataset(String datasetId) {
        datasetIds.add(datasetId);
    }
    public void clear() {
        datasetIds.clear();
    }

    @Override
    public void download(Receiver receiver) throws IOException {
        for (String datasetId : datasetIds) {
            if (receiver.shouldStop()) {
                break;
            }
            for (String url : getFilesforDataset(datasetId, utmzone)) {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                if (conn.getResponseCode() == 200) {
                    String fileName = conn.getHeaderField("Content-Disposition");
                    if (fileName != null) {
                        int p = fileName.lastIndexOf('=');
                        if (p > 0) {
                            fileName = fileName.substring(p + 1);
                        }
                    }
                    receiver.receive(fileName, conn.getInputStream());
                    continue;
                }
            }
        }
    }

    public List<String> getFilesforDataset(String datasetId) throws IOException {
        return getFilesforDataset(datasetId,this.utmzone);
    }

    public List<String> getFilesforDataset(String datasetId,String utmzone) throws IOException {
        List<String> files = new ArrayList<>();
        Connection.Response r = Jsoup.connect(baseUrl).execute();
        Document d = r.parse();
        Elements dlContent = d.getElementsByClass("dlContent sone"+utmzone);
        for(Element row:dlContent) {
            Elements cols = row.getElementsByTag("td");
            if (cols.size() > 1) {
                Elements links = cols.get(0).select("a[href]");
                if (links.size() <1) {
                    continue;
                }
                String fileId = links.first().text();
                // Exact match for DTM10,DOM10,DTM50,DOM50
                boolean found=false;
                if (datasetId.equals(fileId)) {
                    found=true;
                } else if (fileId.substring(0,5).equals(datasetId+" ")) {
                    found = true;
                }
                if (found) {
                    files.add(links.first().attr("abs:href").trim());
                }
            } else {
                continue;
            }
        }
        return files;
    }

}
