package pervasive.jku.at.wifisensor;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;

import java.io.IOException;

import pervasive.jku.at.wifisensor.comm.CommService;
import pervasive.jku.at.wifisensor.wifi.WifiScanEvent;
import pervasive.jku.at.wifisensor.wifi.WifiScanListener;

/**
 * Created by Michael Hansal on 09.05.2016.
 */
class WifiScanToCommRelay implements WifiScanListener {
    private WiFiActivity wiFiActivity;

    private CommService commService=null;

    public WifiScanToCommRelay(WiFiActivity wiFiActivity) {
        this.wiFiActivity = wiFiActivity;
    }

    @Override
    public void onWifiChanged(WifiScanEvent event) {
        Log.d(WiFiActivity.TAG_IOT, "wifiCommRelay called");
        try {
            DataByteArrayOutputStream baos = new DataByteArrayOutputStream();
            AsciiBuffer header = new AsciiBuffer(event.getMAC());
            baos.writeInt(header.length);
            baos.write(header);
            //tuple size
            baos.writeInt(event.getResult().size());
            for (ScanResult data : event.getResult()) {
                baos.writeLong(Long.parseLong(data.BSSID.replaceAll(":", ""), 16));
                baos.writeInt(data.level);
            }
            if (commService != null) {
                commService.sendMessage("sensor", baos.toBuffer());
            } else {
                Log.e(WiFiActivity.TAG_IOT, "comm not available");
            }
        } catch (IOException e) {
            Log.e(WiFiActivity.TAG_IOT, "error preparing message", e);
        }
    }

    public void setCommService(CommService commService) {
        this.commService = commService;
    }
}
