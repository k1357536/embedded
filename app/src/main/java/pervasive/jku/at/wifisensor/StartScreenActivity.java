package pervasive.jku.at.wifisensor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
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

public class StartScreenActivity extends AppCompatActivity {

    private Survey currentSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
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
        ((Spinner)findViewById(R.id.sAnswerItems)).setOnItemSelectedListener(answerSelected);
        findViewById(R.id.bCreateSurvey).setOnClickListener(createSurveyListener);
        findViewById(R.id.bToggleMQS).setOnClickListener(toggleMQServiceListener);
        findViewById(R.id.bToggleWifi).setOnClickListener(toggleWifiListener);
        findViewById(R.id.bOk).setOnClickListener(sendAnswerListener);
    }

    private void StartServices() {
        ServiceHandler.SetSurveyEncoderForCurrentActivity(this, new IConnectionReceivedHandler() {
            @Override
            public void ConnectionReceived() {
                ServiceHandler.GetSurveyEncoder().updateLocation("HS1");
                ServiceHandler.GetSurveyEncoder().setSurveyConsumer(new ISurveyConsumer() {
                    @Override
                    public void accept(Survey s) {
                        currentSurvey = s;
                        SurveyReceived();
                    }
                });
            }
        });
    }

    private void ToggleWiFiService() {
        ToggleButton toggleWifiButton = (ToggleButton)findViewById(R.id.bToggleWifi);
        //ToDo: Enable/Disable service
    }

    private void ToggleMQSservice() {
        ToggleButton toggleMQSButton = (ToggleButton)findViewById(R.id.bToggleMQS);
        //ToDo: Enable/Disable service
    }

    private void SendSurveyAnswer() {
        ServiceHandler.GetSurveyEncoder().answerSurvery(currentSurvey, ((Spinner)findViewById(R.id.sAnswerItems)).getSelectedItemPosition());
        ClearAndShowWaitingScreen();
    }

    public void SurveyReceived(){
        if(currentSurvey.question.isEmpty() || currentSurvey.options.length == 0) return;

        /* Toggle the visibility of the question */
        findViewById(R.id.lCurrentSurvey).setVisibility(View.VISIBLE);
        findViewById(R.id.lLastSurvey).setVisibility(View.INVISIBLE);

        String[] answers = new String[currentSurvey.options.length];
        for(int currentAnswerIndex = 0; currentAnswerIndex < answers.length; currentAnswerIndex++)
            answers[currentAnswerIndex] = currentSurvey.options[currentAnswerIndex].text;

        /* Set the content */
        ((TextView)findViewById(R.id.tSuveryConent)).setText(currentSurvey.question);
        ((Spinner)findViewById(R.id.sAnswerItems)).setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, answers));

        /* Make it possible to answer */
        setButtonEnabled(true);
    }

    /***
     * Clears the view by deleting the old values and toggling another
     * linearLayout's visibility.
     */
    public void ClearAndShowWaitingScreen(){
        /* Clear the current survey view */
        ((TextView)findViewById(R.id.tSuveryConent)).setText("");
        ((Spinner)findViewById(R.id.sAnswerItems)).setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { }));
        setButtonEnabled(false);

        /* Set the new visibility */
        findViewById(R.id.lCurrentSurvey).setVisibility(View.INVISIBLE);
        findViewById(R.id.lLastSurvey).setVisibility(View.VISIBLE);
    }

    private void setButtonEnabled(boolean state){
        findViewById(R.id.bOk).setEnabled(state);
    }
}
