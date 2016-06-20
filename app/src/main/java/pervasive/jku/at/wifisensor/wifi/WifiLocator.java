package pervasive.jku.at.wifisensor.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Michael Hansal on 09.05.2016.
 */
public class WifiLocator {
    public static final float MAX_SIGNAL = 100;
    public static final float MIN_SIGNAL = 0;

    public class ReferenceLocation {
        public String name;
        public HashMap<String, Integer> readings;

        public ReferenceLocation(String n, HashMap<String, Integer> r) {
            name = n;
            readings = r;
        }
    }

    private ArrayList<ReferenceLocation> references = new ArrayList<>();

    public void add(String newName, WifiScanEvent lastEvent) {
        HashMap<String, Integer> values = new HashMap<>();
        for (ScanResult sr : lastEvent.getResult())
            values.put(sr.BSSID, sr.level);
        references.add(new ReferenceLocation(newName, values));
    }

    public HashMap<String, Float> getDistanceProbabilities(WifiScanEvent e) {
        HashMap<String, Float> result = new HashMap<>();
        for (ReferenceLocation ref : references) {
            float sum = 0;

            for (HashMap.Entry<String, Integer> entry : ref.readings.entrySet()) {
                String ssid = entry.getKey();
                int rssi = entry.getValue();

                float comp = 0;
                for (ScanResult scanResult : e.getResult()) {
                    if (scanResult.BSSID.equals(ssid)) {
                        comp = MAX_SIGNAL-Math.abs(scanResult.level - rssi);
                        break;
                    }
                }
                comp -= MIN_SIGNAL;
                comp /= (MAX_SIGNAL - MIN_SIGNAL);
                    sum += comp;
            }
            result.put(ref.name, sum / ref.readings.size());
        }
        return result;
    }

    public String getPosition(WifiScanEvent e){
        double maxValue = Double.NEGATIVE_INFINITY;
        String currentLocation="unknown";

        for (ReferenceLocation ref : references) {
            float sum = 0;

            for (HashMap.Entry<String, Integer> entry : ref.readings.entrySet()) {
                String ssid = entry.getKey();
                int rssi = entry.getValue();

                float comp = 0;
                for (ScanResult scanResult : e.getResult()) {
                    if (scanResult.BSSID.equals(ssid)) {
                        comp = MAX_SIGNAL - Math.abs(scanResult.level - rssi);
                        break;
                    }
                }
                comp -= MIN_SIGNAL;
                comp /= (MAX_SIGNAL - MIN_SIGNAL);
                sum += comp;
            }

            double value = sum / ref.readings.size();
            if (value > maxValue) {
                maxValue = value;
                currentLocation = ref.name;
            }
        }
        return currentLocation;
    }
}
