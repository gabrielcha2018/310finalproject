import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class SentimentAnalysis {
    public static final int POSITIVE =      1;
    public static final int NEUTRAL =       0;
    public static final int NEGATIVE =     -1;

    private StanfordCoreNLP stanfordCoreNLP;
    private Properties properties;
    private String propertiesName="tokenize, ssplit, pos, lemma, ner, parse, sentiment";

    public SentimentAnalysis(){
        properties=new Properties();
        properties.setProperty("annotators",propertiesName);
        stanfordCoreNLP=new StanfordCoreNLP(properties);
    }

    public int getSentimentAnalysis(String sentence){
        int score=0;
        CoreDocument coreDocument=new CoreDocument(sentence);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreSentence> sentences=coreDocument.sentences();
        for(CoreSentence s:sentences){
            String sentiment=s.sentiment();
            System.out.println(sentiment+"<-"+sentence);
            if(sentiment.equals("Negative")){
                score--;
            }
            if(sentiment.equals("Very negative")){
                score=score-2;
            }
            if(sentiment.equals("Positive")){
                score++;
            }
            if(sentiment.equals("Very positive")){
                score=score+2;
            }
        }
        return score;
    }
}
