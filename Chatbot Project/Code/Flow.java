import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Flow object is where a response requires a set of fixed actions to complete a task
 * Example: If user is asking for account information, flow would be fixed
 * actions of getting username and email.
 */
public class Flow {
    public String title;
    public ArrayList<String> responseID;
    public ArrayList<String> responses;
    public ArrayList<String> pattern_format;

    public Flow(ArrayList<String> responseID, ArrayList<String> responses, ArrayList<String> patterns, String title) {
        this.responses = responses;
        this.pattern_format = patterns;
        this.title = title;
        this.responseID = responseID;
    }

    /**
     * Takes user input and matches it to a pattern in 'pattern_format'
     */
    public boolean matches(int tick, String userInput) {
        if (pattern_format.get(tick).isEmpty()) {
            return true;
        }
        return userInput.matches(pattern_format.get(tick));
    }

    public void saveInputs(ArrayList<String> inputs) {
        int sentimentAnalysis_score=0;
        SentimentAnalysis sentimentAnalysis=new SentimentAnalysis();
        sentimentAnalysis_score=sentimentAnalysis.getSentimentAnalysis(inputs.get(responseID.indexOf("Review")));

        try {
            File review = null;
            if(sentimentAnalysis_score > 0){
                review = new File("GoodReviews.txt");
            }
            if(sentimentAnalysis_score == 0){
                review = new File("NeutralReviews.txt");
            }
            if(sentimentAnalysis_score < 0){
                review = new File("BadReviews.txt");
            }
            review.createNewFile();
            FileWriter fileWriter = new FileWriter(review,true);
            fileWriter.write(
                "------" + 
                inputs.get(responseID.indexOf("Name")) + "|"+ 
                inputs.get(responseID.indexOf("Email")) +
                "------\n" + 
                inputs.get(responseID.indexOf("Review")) + "\n"
            );
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
