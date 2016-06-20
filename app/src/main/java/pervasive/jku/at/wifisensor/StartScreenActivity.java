package pervasive.jku.at.wifisensor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import pervasive.jku.at.wifisensor.comm.IConnectionReceivedHandler;
import pervasive.jku.at.wifisensor.comm.ISurveyConsumer;
import pervasive.jku.at.wifisensor.comm.ServiceHandler;
import pervasive.jku.at.wifisensor.comm.Survey;
import pervasive.jku.at.wifisensor.comm.SurveyEncoderService;
import pervasive.jku.at.wifisensor.wifi.WifiScanEvent;
import pervasive.jku.at.wifisensor.wifi.WifiScanListener;
import pervasive.jku.at.wifisensor.wifi.WifiService;

public class StartScreenActivity extends AppCompatActivity implements WifiScanListener {

    private Survey currentSurvey;
    private boolean surveyProcessing = false;
    private  Handler handler;

       private WifiService wifiService;
    private ServiceConnection wifiServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        handler = new Handler(this.getMainLooper());
        InitListeners();
        StartServices();
        ClearAndShowWaitingScreen();
    }

    private View.OnClickListener sendAnswerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendSurveyAnswer();
        }
    };

    private View.OnClickListener toggleWifiListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToggleWiFiService();
        }
    };

    private View.OnClickListener toggleMQServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToggleMQSservice();
        }
    };

    private View.OnClickListener createSurveyListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(StartScreenActivity.this, CreateSurveyActivity.class);
            startActivity(myIntent);
        }
    };

    private AdapterView.OnItemSelectedListener answerSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setButtonEnabled(true);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            setButtonEnabled(false);
        }
    };

    private void InitListeners() {
        ((Spinner) findViewById(R.id.sAnswerItems)).setOnItemSelectedListener(answerSelected);
        findViewById(R.id.bCreateSurvey).setOnClickListener(createSurveyListener);
        findViewById(R.id.bToggleMQS).setOnClickListener(toggleMQServiceListener);
        findViewById(R.id.bToggleWifi).setOnClickListener(toggleWifiListener);
        findViewById(R.id.bOk).setOnClickListener(sendAnswerListener);
    }

    private void StartServices() {
        ServiceHandler.SetSurveyEncoderForCurrentActivity(this, new IConnectionReceivedHandler() {
            @Override
            public void ConnectionReceived() {
                ServiceHandler.GetSurveyEncoder().setSurveyConsumer(new ISurveyConsumer() {
                    @Override
                    public void acceptSurvey(Survey s) {
                        currentSurvey = s;
                        handler.post(surveyReceived);
                    }

                    @Override
                    public void acceptIntermediateResult(Survey s) {
                    }
                });
            }
        });

        Intent mIntent = new Intent(this, WifiService.class);
        wifiServiceConnection = new ServiceConnection() {
            public void onServiceDisconnected(ComponentName name) {
                wifiService = null;
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                WifiService.LocalBinder mLocalBinder = (WifiService.LocalBinder) service;
                wifiService = mLocalBinder.getServerInstance();
                wifiService.registerListener(StartScreenActivity.this);
                //((ToggleButton) findViewById(R.id.toggle_wifi_scan)).setChecked(wifiService.isScanning());
            }
        };
        bindService(mIntent, wifiServiceConnection, BIND_AUTO_CREATE);
    }

    private void ToggleWiFiService() {
        ToggleButton toggleWifiButton = (ToggleButton) findViewById(R.id.bToggleWifi);
        //ToDo: Enable/Disable service
    }

    private void ToggleMQSservice() {
        ToggleButton toggleMQSButton = (ToggleButton) findViewById(R.id.bToggleMQS);
        //ToDo: Enable/Disable service
    }

    private void SendSurveyAnswer() {
        ServiceHandler.GetSurveyEncoder().answerSurvery(currentSurvey, ((Spinner) findViewById(R.id.sAnswerItems)).getSelectedItemPosition());
        ClearAndShowWaitingScreen();
    }

    public Runnable surveyReceived = new Runnable() {
        @Override
        public void run() {
            if (currentSurvey.question.isEmpty() || currentSurvey.options.length == 0 || surveyProcessing) return;

            surveyProcessing = true;

        /* Toggle the visibility of the question */
            findViewById(R.id.lCurrentSurvey).setVisibility(View.VISIBLE);
            findViewById(R.id.lLastSurvey).setVisibility(View.INVISIBLE);

            String[] answers = new String[currentSurvey.options.length];
            for (int currentAnswerIndex = 0; currentAnswerIndex < answers.length; currentAnswerIndex++)
                answers[currentAnswerIndex] = currentSurvey.options[currentAnswerIndex].text;

        /* Set the content */
            ((TextView) findViewById(R.id.tSuveryConent)).setText(currentSurvey.question);
            ((Spinner) findViewById(R.id.sAnswerItems)).setAdapter(
                    new ArrayAdapter<String>(StartScreenActivity.this, android.R.layout.simple_spinner_item, answers));

        /* Make it possible to answer */
            setButtonEnabled(true);
        }
    };

    /***
     * Clears the view by deleting the old values and toggling another
     * linearLayout's visibility.
     */
    public void ClearAndShowWaitingScreen() {
        /* Clear the current survey view */
        ((TextView) findViewById(R.id.tSuveryConent)).setText("");
        ((Spinner) findViewById(R.id.sAnswerItems)).setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{}));
        setButtonEnabled(false);

        /* Set the new visibility */
        findViewById(R.id.lCurrentSurvey).setVisibility(View.INVISIBLE);
        findViewById(R.id.lLastSurvey).setVisibility(View.VISIBLE);

        surveyProcessing = false;
    }

    private void setButtonEnabled(boolean state) {
        findViewById(R.id.bOk).setEnabled(state);
    }

    private String lastLoc = "";

    @Override
    public void onWifiChanged(WifiScanEvent event) {
        final String loc = wifiService.getWifiLocator().getPosition(event);

        if (!loc.equals(lastLoc)) {
            ServiceHandler.GetSurveyEncoder().updateLocation(loc);
            Handler h = new Handler(StartScreenActivity.this.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.tRoom)).setText(loc);
                }
            });
        }
    }
}
