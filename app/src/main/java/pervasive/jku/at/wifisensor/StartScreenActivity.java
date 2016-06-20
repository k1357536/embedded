package pervasive.jku.at.wifisensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StartScreenActivity extends AppCompatActivity {

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
            SendSurveyAnswer(((Spinner)findViewById(R.id.sAnswerItems)).getSelectedItem().toString());
        }
    };

    private View.OnClickListener toggleWifiListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToggleWiFiService();
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
        findViewById(R.id.bToggleWifi).setOnClickListener(toggleWifiListener);
    }

    private void StartServices() {
        //ToDo: Start services
    }

    private void ToggleWiFiService() {
        ToggleButton toggleWifiButton = (ToggleButton)findViewById(R.id.bToggleWifi);
        //ToDo: Enable/Disable service
    }

    private void ToggleMQSservice() {
        ToggleButton toggleMQSButton = (ToggleButton)findViewById(R.id.bToggleMQS);
        //ToDo: Enable/Disable service
    }

    private void SendSurveyAnswer(String answer) {
        //ToDo: Answer the survey
    }

    public void SurveyReceived(String question, String[] answers){
        if(question.isEmpty() || answers.length == 0) return;

        /* Toggle the visibility of the question */
        findViewById(R.id.lCurrentSurvey).setVisibility(View.VISIBLE);
        findViewById(R.id.lLastSurvey).setVisibility(View.INVISIBLE);

        /* Set the content */
        ((TextView)findViewById(R.id.tSuveryConent)).setText(question);
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
