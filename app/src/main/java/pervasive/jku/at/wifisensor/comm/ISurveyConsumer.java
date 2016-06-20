package pervasive.jku.at.wifisensor.comm;

/**
 * Created by Michael Hansal on 20.06.2016.
 */
public interface ISurveyConsumer {
    void acceptSurvey(Survey s);

    void acceptIntermediateResult(Survey s);
}
