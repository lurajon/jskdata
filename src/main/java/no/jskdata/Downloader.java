package no.jskdata;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class Downloader {

    protected Predicate<String> fileNameFilter = (String) -> true;

    private Logger log;

    protected Logger getLogger() {
        if (log == null) {
            log = Logger.getLogger(getClass().getName());
        }
        return log;
    }

    public abstract void login() throws IOException;

    public void setFileNameFilter(Predicate<String> fileNameFilter) {
        this.fileNameFilter = fileNameFilter;
    }

    public abstract void dataset(String dataset) throws IOException;

}
