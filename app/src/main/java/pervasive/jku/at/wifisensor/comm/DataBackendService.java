package pervasive.jku.at.wifisensor.comm;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;

/**
 * Created by Michael Hansal on 20.06.2016.
 */
public class DataBackendService implements Listener {
    private MQTT mqtt;
    private CallbackConnection connection;
    private String locationURI = null;
    private static final String uriPrefix = "at.jku.pervasive.es16.Test1_alpha.";
    private IRawMessageConsumer tgt;

    public DataBackendService(IRawMessageConsumer tgt) {
        mqtt = new MQTT();
        this.tgt = tgt;
        try {
            mqtt.setHost("iot.soft.uni-linz.ac.at", 1883);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        connection = mqtt.callbackConnection();
        connection.listener(this).connect(failureCb);
    }

    @Override
    public void onConnected() {
        System.out.println("connected to " + mqtt.getHost());
    }

    @Override
    public void onDisconnected() {
        System.out.println("disconnect from " + mqtt.getHost());
    }

    @Override
    public void onFailure(Throwable arg0) {
        System.out.println("Failure");
        System.out.println(arg0.getMessage());
        arg0.printStackTrace();
        System.exit(0);
    }

    @Override
    public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
        if (topic.toString().equals(locationURI) && tgt != null)
            tgt.accept(payload.toByteArray());
        ack.run();
    }

    public void publish(byte[] data) {
        if (locationURI != null)
            connection.publish(locationURI, data, QoS.AT_LEAST_ONCE, false, failureCb);
    }

    public void setLocation(String loc) {
        if (locationURI != null) {
            UTF8Buffer buff = new UTF8Buffer(locationURI);
            connection.unsubscribe(new UTF8Buffer[] { buff }, failureCb);
            locationURI = null;
        }

        if (loc != null) {
            locationURI = uriPrefix + loc;
            Topic[] list = new Topic[] { new Topic(locationURI, QoS.AT_LEAST_ONCE) };
            connection.subscribe(list, failureCb2);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (connection != null)
            connection.disconnect(null);
        super.finalize();
    }

    private Callback<Void> failureCb = new Callback<Void>() {
        public void onSuccess(Void arg0) {
        }

        @Override
        public void onFailure(Throwable arg0) {
            if(connection != null)
                connection.failure();
        }
    };

    private Callback<byte[]> failureCb2 = new Callback<byte[]>() {
        public void onSuccess(byte[] arg0) {
        }

        @Override
        public void onFailure(Throwable arg0) {
            if(connection != null)
                connection.failure();
        }
    };
}
