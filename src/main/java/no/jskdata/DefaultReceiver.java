package no.jskdata;

public abstract class DefaultReceiver implements Receiver {
    
    @Override
    public boolean shouldStop() {
        return false;
    }

}
