import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import net.didion.jwnl.JWNLException;

public class Testing {
    @Test
    public void testSentimentAnalysisPositive() {
        SentimentAnalysis test = new SentimentAnalysis();
        assertEquals(1, test.getSentimentAnalysis("This is a great sentence"));
        assertEquals(-1, test.getSentimentAnalysis("This is a bad sentence"));
        assertEquals(0, test.getSentimentAnalysis("This is a sentence"));
        assertEquals(2, test.getSentimentAnalysis("This is a great sentence. This is a great sentence."));
    }

    @Test   
    public void testResponseBasicFuncions(){
        String[] triggers = {"trigger1", "trigger2", "trigger3"};
        Response response = new Response("This is the response message.", triggers);
        assertEquals("This is the response message.",response.getMessage());
        for(int i=0; i<triggers.length; i++){
            assertEquals(triggers[i],response.getTriggers()[i]);
        }   
    }

    @Test   
    public void testDictCorrectSpelling(){
        try {
            Dict d=new Dict();
            assertEquals(true,d.isWordExist("word"));
            assertEquals(false,d.isWordExist("xxxxxxxx"));
            String correct_word = "word";
            assertEquals(true,d.getPossibleCorrectSpelling("wrd").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("wod").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("wor").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("wword").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("woord").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("worrd").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("wordd").indexOf(correct_word)!=-1);
            assertEquals(true,d.getPossibleCorrectSpelling("wrod").indexOf(correct_word)!=-1);


        } catch (FileNotFoundException | JWNLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test   
    public void testDictgetSynonym(){
        try {
            Dict d=new Dict();
            assertEquals(false,d.getSynonym("black").indexOf("white")!=-1);
            assertEquals(true,d.getSynonym("black").indexOf("dark")!=-1);
        } catch (FileNotFoundException | JWNLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testselectPath() throws IOException {
        Chatbot test = new Chatbot();
        test.initializeResponses();
        assertEquals(
            "I understand that the screen on your headset is having some issues. To help you troubleshoot further, please let me know if the screen can still turn on or if it is black.", 
            test.selectPath(test.Root, "I have a problem with the screen", 0).getMessage()
        );
    }
}
