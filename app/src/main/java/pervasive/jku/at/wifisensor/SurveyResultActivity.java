package pervasive.jku.at.wifisensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pervasive.jku.at.wifisensor.comm.ServiceHandler;
import pervasive.jku.at.wifisensor.comm.Survey;

public class SurveyResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_result);
        InitListeners();
        InitUIValues();
    }

    private View.OnClickListener closeSurveyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CloseSurvey();
        }
    };

    private View.OnClickListener returnToStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReturnToStart();
        }
    };

    private void InitListeners() {
        findViewById(R.id.bEndSurvey).setOnClickListener(closeSurveyListener);
        findViewById(R.id.bReturn).setOnClickListener(returnToStartListener);
    }

    private void InitUIValues() {
        ((TextView)findViewById(R.id.tQuestion)).setText(getIntent().getStringExtra("Question"));
    }

    private void CloseSurvey() {
        Survey s = ServiceHandler.GetSurveyEncoder().finishSurvey();

        ((TextView)findViewById(R.id.tResult)).setText(s.toResults()); //ToDo: Result text.
        findViewById(R.id.bReturn).setEnabled(true); //Make it possible to leave this view.
        findViewById(R.id.bEndSurvey).setEnabled(false);
    }

    private void ReturnToStart() {
        Intent myIntent = new Intent(SurveyResultActivity.this, StartScreenActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(myIntent);
        finish();
    }
}
