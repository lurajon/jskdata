package no.jskdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
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

}
