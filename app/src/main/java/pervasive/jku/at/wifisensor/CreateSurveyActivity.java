package pervasive.jku.at.wifisensor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import pervasive.jku.at.wifisensor.comm.ServiceHandler;
import pervasive.jku.at.wifisensor.comm.Survey;

public class CreateSurveyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);
        InitListeners();
    }

    private void InitListeners() {
        findViewById(R.id.bAddAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAnswer();
            }
        });

        findViewById(R.id.bSendSurvey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSurvey();
            }
        });
    }

    private void AddAnswer() {
        EditText newAnswerEdit = new EditText(this);
        newAnswerEdit.setLayoutParams(new ActionBar.LayoutParams(android.app.ActionBar.LayoutParams.FILL_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT));
        newAnswerEdit.setHint("...");
        ((LinearLayout)findViewById(R.id.lAnswerLayout)).addView(newAnswerEdit);
    }

    private void CreateSurvey() {
        LinearLayout linearLayout = ((LinearLayout)findViewById(R.id.lAnswerLayout));
        String question = ((EditText)findViewById(R.id.eQuestion)).getText().toString();
        String[] answers = new String[linearLayout.getChildCount()];
        for(int currentChild = 0; currentChild < linearLayout.getChildCount(); currentChild++)
            answers[currentChild] = ((EditText)linearLayout.getChildAt(currentChild)).getText().toString();

        /* Actually start the survey */
        ServiceHandler.GetSurveyEncoder().startSurvey(new Survey(question, answers));

        Intent myIntent = new Intent(CreateSurveyActivity.this, SurveyResultActivity.class);
        myIntent.putExtra("Question", question);
        startActivity(myIntent);
        finish();
    }
}
