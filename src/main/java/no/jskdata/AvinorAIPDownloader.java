package no.jskdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import no.jskdata.geojson.Feature;
import no.jskdata.geojson.FeatureCollection;
import no.jskdata.geojson.Point;

public class AvinorAIPDownloader extends Downloader {

    private Integer maxCount = null;

    void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public void dataset(String dataset) {
    }

    @Override
    public void download(Receiver receiver) throws IOException {

        // find latest AIP version number by navigating..
        String u = "https://ais.avinor.no/no/AIP/";
        Connection.Response r = Jsoup.connect(u).execute();
        Document d = r.parse();
        Elements elements = d.select("a.frontpage_language_link[href=main_en.html]");
        u = elements.get(0).absUrl("href");
        r = Jsoup.connect(u).execute();
        d = r.parse();

        // find all ICAOs
        FeatureCollection features = new FeatureCollection();
        for (Element element : d.select("option[value]")) {
            u = element.absUrl("value");

            String icao = element.text().substring(0, 4);
            Feature feature = new Feature();
            feature.setProperty("name", element.text());
            feature.setProperty("icao", icao);

            // find PDFs for ICAO
            List<String> attachments = new ArrayList<>();
            r = Jsoup.connect(u).execute();
            d = r.parse();
            for (Element pdfElement : d.select("a[href]")) {
                String href = pdfElement.attr("href");
                // https://ais.avinor.no/no/AIP/View/25/aip/ad/enno/enno_en.html
                // had a backslash to one of the PDFs
                href = href.replace('\\', '/');
                String url = StringUtil.resolve(pdfElement.baseUri(), href);
                // did not get select on href=.pdf to work..
                if (!url.toLowerCase().endsWith(".pdf")) {
                    continue;
                }
                if (!url.toLowerCase().contains(icao.toLowerCase())) {
                    continue;
                }
                attachments.add(url);
            }
            feature.setProperty("attachments", attachments);

            // parse first pdf and look for coordinate
            if (!attachments.isEmpty()) {
                String mainPdfUrl = attachments.get(0);
                HttpURLConnection conn = (HttpURLConnection) new URL(mainPdfUrl).openConnection();
                if (conn.getResponseCode() != 200) {
                    throw new IOException("could not get " + mainPdfUrl);
                }
                byte[] data = ByteStreams.toByteArray(conn.getInputStream());
                PDDocument document = PDDocument.load(data);
                PDFTextStripper pdfTextStripper = new PDFTextStripper();
                String text = pdfTextStripper.getText(document);

                // search for, extract and parse coordinate
                Pattern pattern = Pattern.compile("([0-9]{6}[N]{1}) ([0-9]{7}[E]{1})");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    double y = parseCoordinatePart(matcher.group(1), 2);
                    double x = parseCoordinatePart(matcher.group(2), 3);
                    feature.setGeometry(new Point(x, y));
                    break;
                }

                document.close();
            }

            features.add(feature);

            if (maxCount != null && maxCount.intValue() <= features.size()) {
                break;
            }
        }

        byte[] data = new Gson().toJson(features).getBytes("UTF-8"); 
        System.out.println(new Gson().toJson(features));
        receiver.receive("avinor_aip.geojson", new ByteArrayInputStream(data));
    }

    static double parseCoordinatePart(String encoded, int degreeDigits) {
        if (encoded.length() != (degreeDigits + 5)) {
            throw new IllegalArgumentException("wrong coordinate part: " + encoded);
        }

        double v = Double.parseDouble(encoded.substring(0, degreeDigits));
        double minutes = Double.parseDouble(encoded.substring(degreeDigits, degreeDigits + 2));
        double seconds = Double.parseDouble(encoded.substring(degreeDigits + 2, degreeDigits + 4));
        minutes = minutes + (seconds / 60.0);
        v = v + (minutes / 60.0);
        return v;
    }

    @Override
    public void clear() {
    }

}
