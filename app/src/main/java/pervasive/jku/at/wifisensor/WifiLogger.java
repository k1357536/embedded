package pervasive.jku.at.wifisensor;

import android.net.wifi.ScanResult;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import pervasive.jku.at.wifisensor.wifi.WifiLocator;
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

    public static void load(String filename, WifiLocator locator) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename + ".txt");

            BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file)));
            String s = br.readLine();
            while(s != null && !s.isEmpty()) {
                String[] strs = s.split(",");

                HashMap<String, Integer> values = new HashMap<>();
                for(int i=1; i < strs.length && !strs[i].isEmpty(); i+=2)
                    values.put(strs[i], Integer.parseInt(strs[i+1]));

                WifiLocator.ReferenceLocation rl = new WifiLocator.ReferenceLocation(strs[0], values);
                locator.add(rl);
            }
            br.close();
        } catch (Exception e) {
            Log.e(TAG_LOG, "error reading log: " + e.getMessage());
        }
    }
}
