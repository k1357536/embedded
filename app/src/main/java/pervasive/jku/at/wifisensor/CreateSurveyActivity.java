package pervasive.jku.at.wifisensor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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

        Intent myIntent = new Intent(CreateSurveyActivity.this, StartScreenActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(myIntent);
    }
}
