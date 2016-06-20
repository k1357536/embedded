package pervasive.jku.at.wifisensor.comm;

/**
 * Created by Michael Hansal on 20.06.2016.
 */
public interface IRawMessageConsumer {
    void accept(byte[] data);
}
