package no.jskdata;

import no.jskdata.Downloader;
import no.jskdata.data.geonorge.File;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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

    private HttpURLConnection openConnectionWithoutCookies(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    @Override
    public void download(Receiver receiver) throws IOException {
        for (String datasetId : datasetIds) {
            if (receiver.shouldStop()) {
                break;
            }
            List<File> datasetFiles = getFilesforDataset(datasetId,utmzone);
            if (datasetFiles.isEmpty()) {
                break;
            }
            for (File file:datasetFiles) {
                HttpURLConnection conn = openConnectionWithoutCookies(file.downloadUrl);
                if (conn.getResponseCode() == 200) {
                    receiver.receive(file.name, conn.getInputStream());
                    continue;
                }
            }


        }
    }

    public List<File> getFilesforDataset(String datasetId) throws IOException {
        return getFilesforDataset(datasetId,this.utmzone);
    }

    public List<File> getFilesforDataset(String datasetId,String utmzone) throws IOException {
        List<File> files = new ArrayList<File>();
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
                String downloadUrl = null;
                String fileSize = null;
                String fileName = null;
                if (datasetId.equals(fileId)) {
                    found=true;
                    fileName = datasetId+"-"+utmzone+".zip";
                } else if (fileId.substring(0,5).equals(datasetId+" ")) {
                    found = true;
                    fileName = fileId.replace(" ","_")+"-"+utmzone+".zip";
                }
                if (found) {
                    File fil = new File();
                    fil.downloadUrl = links.first().attr("abs:href").trim();;
                    fil.name = fileName;
                    if (cols.size() == 2) {
                        fil.fileSize = cols.get(1).text();
                    }
                    files.add(fil);
                }
            } else {
                continue;
            }
        }
        return files;
    }

}
