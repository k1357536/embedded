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
    public final static String DEFAULT_LOG_NAME = "wifiLogfile";
    private static final String TAG_LOG = "log";
    private String logFileName = DEFAULT_LOG_NAME;

    public WifiScanEvent getLastEvent() {
        return lastEvent;
    }

    private WifiScanEvent lastEvent = null;

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void log(WifiScanEvent event) {
        lastEvent = event;
    }

    public void save(String name) {
        if (lastEvent == null)
            return;

        try {
            File file = new File(Environment.getExternalStorageDirectory(), logFileName + ".txt");
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
