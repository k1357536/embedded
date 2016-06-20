package pervasive.jku.at.wifisensor.comm;

import java.util.UUID;

/**
 * Created by Michael Hansal on 20.06.2016.
 */
public class Survey {
    public static class Entry {
        public String text;
        public int votes;

        public Entry(String txt) {
            text = txt;
            votes = 0;
        }
    }

    public UUID id;
    public String question;
    public Entry[] options;

    public Survey(String question, String[] options) {
        super();
        this.id = UUID.randomUUID();
        this.question = question;
        this.options = new Entry[options.length];
        for (int i = 0; i < this.options.length; i++)
            this.options[i] = new Entry(options[i]);
    }

    public Survey() {
        id = UUID.randomUUID();
        question = "";
        options = new Entry[0];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("'" + question + "': ");
        for (Entry a : options)
            sb.append("'" + a.text + "' ");
        return sb.toString();
    }

    public String toResults() {
        StringBuilder sb = new StringBuilder("'" + question + "': \n");
        for (int i = 0; i < options.length; i++)
            sb.append("  '" + options[i].text + "':" + options[i].votes + " \n");
        return sb.toString();
    }
}
