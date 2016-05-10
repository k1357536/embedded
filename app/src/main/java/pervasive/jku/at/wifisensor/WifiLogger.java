package pervasive.jku.at.wifisensor;

import android.net.wifi.ScanResult;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import pervasive.jku.at.wifisensor.wifi.WifiScanEvent;

/**
 * Created by Michael Hansal on 09.05.2016.
 */
public class WifiLogger {
    private static final String TAG_LOG = "log";

    public static void save(String filename, String name, WifiScanEvent lastEvent) {
        if (lastEvent == null)
            return;

        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename + ".txt");
            Log.d(TAG_LOG, "save path: " + file.getAbsolutePath());
            OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(file, true));
            bw.write(name + ",");
            for (ScanResult data : lastEvent.getResult()) {
                bw.write(data.BSSID + "," + data.level + ",");
            }
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            Log.e(TAG_LOG, "error writing log: " + e.getMessage());
        }
    }
}
