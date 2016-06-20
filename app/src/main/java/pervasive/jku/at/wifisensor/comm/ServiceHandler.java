package pervasive.jku.at.wifisensor.comm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Thomas on 20.06.2016.
 */
public class ServiceHandler {
    private static SurveyEncoderService encoderService;

    public static SurveyEncoderService GetSurveyEncoder() {
        return encoderService;
    }

    public static void SetSurveyEncoderForCurrentActivity(AppCompatActivity activity) {
        Intent intent = new Intent(activity, SurveyEncoderService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SurveyEncoderService.LocalBinder localBinder = (SurveyEncoderService.LocalBinder)service;
                encoderService = localBinder.getServerInstance();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        if (!activity.getApplicationContext().bindService(intent, serviceConnection, activity.BIND_AUTO_CREATE))
            Log.d("SV", "Bind failed");
        else if (activity.startService(intent) == null)
            Log.d("SV", "Start failed");
        else
            Log.d("SV", "Encoder bound to Backend");
    }
}
