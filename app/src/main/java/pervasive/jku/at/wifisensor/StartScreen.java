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

public class StartScreen extends AppCompatActivity {
    private String lastSurveyQuestion, lastSurveyAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        InitListeners();
    }

    private void InitListeners() {
        ((Spinner)findViewById(R.id.sAnswerItems)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setButtonEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setButtonEnabled(false);
            }
        });

        ((FloatingActionButton)findViewById(R.id.bCreateSurvey)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StartScreen.this, CreateSurveyActivity.class);
                startActivity(myIntent);
            }
        });
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

    public void SetLastSurveyResult(String question, String answer){
        lastSurveyQuestion = question;
        lastSurveyAnswer = answer;
    }

    /***
     * Clears the view by deleting the old values and toggling another
     * linearLayout's visibility.
     */
    public void ClearAndShowLastResult(){
        /* Clear the current survey view */
        ((TextView)findViewById(R.id.tSuveryConent)).setText("");
        ((Spinner)findViewById(R.id.sAnswerItems)).setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { }));
        setButtonEnabled(false);

        /* Set the new visibility */
        findViewById(R.id.lCurrentSurvey).setVisibility(View.INVISIBLE);
        findViewById(R.id.lLastSurvey).setVisibility(View.VISIBLE);

        /* Set the last survey result */
        ((TextView)findViewById(R.id.tLastSurveyQuestion)).setText(lastSurveyQuestion);
        ((TextView)findViewById(R.id.tLastSurveyAnswer)).setText(lastSurveyAnswer);
    }

    private void setButtonEnabled(boolean state){
        findViewById(R.id.bOk).setEnabled(state);
    }
}
