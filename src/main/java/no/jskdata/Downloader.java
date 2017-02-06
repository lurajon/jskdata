package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class Downloader {

    protected Predicate<String> fileNameFilter = (String) -> true;
    
    protected String currentDownloadUrl;

    private Logger log;

    protected Logger getLogger() {
        if (log == null) {
            log = Logger.getLogger(getClass().getName());
        }
        return log;
    }

    public void setFileNameFilter(Predicate<String> fileNameFilter) {
        this.fileNameFilter = fileNameFilter;
    }

    public abstract void dataset(String dataset);

    public abstract void download(Receiver receiver) throws IOException;

    public void download(BiConsumer<String, InputStream> receiver) throws IOException {
        download(new Receiver() {

            @Override
            public boolean shouldStop() {
                return false;
            }

            @Override
            public void receive(String fileName, InputStream in) throws IOException {
                receiver.accept(fileName, in);
            }

        });
    }

    public abstract void clear();

    public static Downloader create(Map<String, String> options) {
        String type = options.get("type");
        if (type == null) {
            return new NoDownloader();
        }
        
        String username = options.get("username");
        String password = options.get("password");
        boolean hasUserAndPass = username != null && password != null;

        Downloader dl = new NoDownloader();
        if ("url".equals(type)) {
            dl = new URLDownloader();
        } else if (hasUserAndPass && "data.kartverket.no".equals(type)) {
            dl = new KartverketDownload(username, password);
        } else if (hasUserAndPass && type.equals("GeoNorgeSkdl2")) {
            dl = new GeoNorgeSkdl2(username, password);
        } else if (type.equals("GeoNorgeDownloadAPI")) {
            dl = new GeoNorgeDownloadAPI();
        }

        String dataset = options.get("dataset");
        if (dataset != null) {
            dl.dataset(dataset);
        }
        
        String fileNameSuffix = options.get("fileNameSuffix");
        if (fileNameSuffix != null) {
            dl.setFileNameFilter(n -> n.endsWith(fileNameSuffix));
        }

        return dl;
    }
    
    /**
     * The current download url used by {@link #download(Receiver)} and
     * {@link #download(BiConsumer)}. Can be useful for debugging.
     * 
     * @return
     */
    public String getCurrentDownloadUrl() {
        return currentDownloadUrl;
    }

}
