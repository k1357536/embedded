package pervasive.jku.at.wifisensor.comm;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Michael Hansal on 20.06.2016.
 */
public class SurveyEncoderService extends Service implements IRawMessageConsumer {
    private ServiceConnection dataBackendCon;
    private DataBackendService dataBackend;
    private Survey currentSurvey;
    private ISurveyConsumer client;

    private static final byte TYPE_REQUEST = 0;
    private static final byte TYPE_RESPONSE = 1;

    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public SurveyEncoderService getServerInstance() {
            return SurveyEncoderService.this;
        }
    }

    public SurveyEncoderService() {
        bindDataBackendService();
    }

    public void setSurveyConsumer(ISurveyConsumer client){
        this.client = client;
    }

    @Override
    public void accept(byte[] t) {
        try {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(t));
            switch (is.readInt()) {
                case TYPE_REQUEST:
                    if (client == null || currentSurvey != null)
                        return;
                    Survey s = new Survey();
                    s.id = UUID.fromString(is.readUTF());
                    s.question = is.readUTF();
                    s.options = new Survey.Entry[is.readInt()];
                    for (int i = 0; i < s.options.length; i++)
                        s.options[i] = new Survey.Entry(is.readUTF());
                    client.accept(s);
                    break;
                case TYPE_RESPONSE:
                    UUID rxId = UUID.fromString(is.readUTF());
                    if (currentSurvey != null && rxId.equals(currentSurvey.id)) {
                        int index = is.readInt();
                        if (index <= currentSurvey.options.length)
                            currentSurvey.options[index].votes++;
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startSurvey(Survey s) {
        try {
            currentSurvey = s;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);

            os.writeInt(TYPE_REQUEST);
            os.writeUTF(s.id.toString());
            os.writeUTF(s.question);
            os.writeInt(s.options.length);
            for (Survey.Entry op : s.options)
                os.writeUTF(op.text);
            dataBackend.publish(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Survey finishSurvey() {
        Survey s = currentSurvey;
        currentSurvey = null;
        return s;
    }

    public void answerSurvery(Survey s, int anserId) {
        try {
            if (anserId >= s.options.length)
                return;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);

            os.writeInt(TYPE_RESPONSE);
            os.writeUTF(s.id.toString());
            os.writeInt(anserId);
            dataBackend.publish(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLocation(String loc) {
        dataBackend.setLocation(loc);
    }

    private void bindDataBackendService() {
        Intent mIntent = new Intent(this, DataBackendService.class);
        dataBackendCon = new ServiceConnection() {

            public void onServiceDisconnected(ComponentName name) {
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                DataBackendService.LocalBinder mLocalBinder = (DataBackendService.LocalBinder) service;
                dataBackend = mLocalBinder.getServerInstance();
                dataBackend.setConsumer(this);
            }
        };
        bindService(mIntent, dataBackendCon, BIND_AUTO_CREATE);
        startService(mIntent);
    }
}
