package pervasive.jku.at.wifisensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SurveyResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_result);
        InitListeners();
    }

    private View.OnClickListener closeSurveyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
        }
    }

    private void InitListeners() {

    }

    private void CloseSurvey() {
        //ToDo: Close survey
    }
}
