import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.ArrayList;

public class Dict{

    private Dictionary dictionary;
    private int limit=6;
    public static final int ADJECTIVE =         0;
    public static final int ADVERB =            1;
    public static final int CATS =              2;
    public static final int NOUN =              3;
    public static final int VERB =              4;
    public static final int CONJUNCTION =       5;
    public static final int HFVERB =            6;
    public static final int PRONOUN =           7;
    public static final char[] ALPHABET = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    private InputStream conjunctionFilePath; 
    private InputStream highFreqVerbFilePath; 
    private InputStream highFreqWordFilePath; 
    private InputStream pronounFilePath; 

    public Dict() throws JWNLException, FileNotFoundException, InterruptedException{        
        String rr = getClass().getClassLoader().getResource("Dictionaries/Dict").getFile();
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<jwnl_properties language=\"en\">\n    <version publisher=\"Princeton\" number=\"3.1\" language=\"en\"/>\n    <dictionary class=\"net.didion.jwnl.dictionary.FileBackedDictionary\">\n        <param name=\"dictionary_element_factory\"\n               value=\"net.didion.jwnl.princeton.data.PrincetonWN17FileDictionaryElementFactory\"/>\n        <param name=\"file_manager\" value=\"net.didion.jwnl.dictionary.file_manager.FileManagerImpl\">\n            <param name=\"file_type\" value=\"net.didion.jwnl.princeton.file.PrincetonRandomAccessDictionaryFile\"/>\n            <param name=\"dictionary_path\" value=\"" + rr + "\"/>\n        </param>\n    </dictionary>\n    <resource class=\"PrincetonResource\"/>\n</jwnl_properties>";
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JWNL.initialize(stream);
        this.dictionary = Dictionary.getInstance();
    }

    public String extendSentence(String sentence) throws FileNotFoundException, JWNLException, InterruptedException{
        ArrayList<String> words;
        words=breaksToWords(sentence);
        for(int i=0; i<words.size(); i++){

            if(!isWordExist(words.get(i))){
                ArrayList<String> correctSpellings = getPossibleCorrectSpelling(words.get(i));
                if(!correctSpellings.isEmpty()){
                    for(String correctWord:correctSpellings){
                        words.set(i,words.get(i)+" | "+correctWord);

                    }
                }
            }else{
                if(getPOS(words.get(i))[NOUN] && !getPOS(words.get(i))[ADJECTIVE] && !getPOS(words.get(i))[VERB]) {
                    if (!isConjunction(words.get(i)) && !isHFVerb(words.get(i)) && !isPronoun(words.get(i))) {
                        ArrayList<String> synonyms=new ArrayList<String>();
                        if(getSynonym(words.get(i)).size()==0){synonyms = getAnyRelated(words.get(i));}else{synonyms = getSynonym(words.get(i));}
                        for (String synonym : synonyms) {
                            words.set(i, words.get(i) + " / " + synonym);
                        }
                    }
                }
            }
        }
        String newSentence="";
        for(String word:words){
            newSentence=newSentence+" "+word;
        }
        return newSentence;
    }
    public String correctSpelling(String sentence) throws FileNotFoundException, JWNLException, InterruptedException{
        ArrayList<String> words;
        words=breaksToWords(sentence);
        for(int i=0; i<words.size(); i++){
            if(!isWordExist(words.get(i))){
                ArrayList<String> correctSpellings = getPossibleCorrectSpelling(words.get(i));
                if(!correctSpellings.isEmpty()){
                    for(String correctWord:correctSpellings){
                        words.set(i,words.get(i)+"|"+correctWord);

                    }
                }
            }
        }
        String newSentence="";
        for(String word:words){
            newSentence=newSentence+" "+word;
        }
        return newSentence;
    }
    /**
     * get all synonyms for word
     * @param String word
     * @return ArrayList<String> contains all synonyms
     */
    public ArrayList<String> getSynonym(String word) throws JWNLException, FileNotFoundException {
        word=word.toLowerCase();

        ArrayList<String> allSynonym = new ArrayList<String>();
        IndexWordSet indexWordSet = dictionary.lookupAllIndexWords(word);
        for(IndexWord indexWord:indexWordSet.getIndexWordArray()) {
            Synset[] senses = indexWord.getSenses();
            for(Synset synset:senses) {
                PointerTargetNodeList h = PointerUtils.getInstance().getSynonyms(synset);
                for (int i = 0; i < h.size(); i++) {
                    allSynonym.addAll(breaksToWords(h.get(i).toString(),word));
                }
            }
        }
        if(allSynonym.size()>=limit){
            allSynonym.subList(0,limit);
            return allSynonym;
        }else{
            return allSynonym;
        }
    }
    /**
     * get all related words for word
     * @param String word
     * @return ArrayList<String> contains all related words
     */
    public ArrayList<String> getAnyRelated(String word) throws JWNLException, FileNotFoundException {
        word=word.toLowerCase();

        ArrayList<String> allSynonym = new ArrayList<String>();

        IndexWordSet indexWordSet = dictionary.lookupAllIndexWords(word);
        for(IndexWord indexWord:indexWordSet.getIndexWordArray()) {
            Synset[] senses = indexWord.getSenses();
            for(Synset synset:senses) {
                allSynonym.addAll(breaksToWords(synset.toString(),word));
                PointerTargetNodeList h = PointerUtils.getInstance().getSynonyms(synset);
                for (int i = 0; i < h.size(); i++) {
                    allSynonym.addAll(breaksToWords(h.get(i).toString(),word));
                }
            }
        }
        if(allSynonym.size()>=limit){
            allSynonym.subList(0,limit);
            return allSynonym;
        }else{
            return allSynonym;
        }
    }
    private ArrayList<String> breaksToWords(String str,String originalWord){
        ArrayList<String> words = new ArrayList<String>();
        int start = str.indexOf("Words:")+6;
        int end = str.indexOf("--")-1;
        String word,line;
        if(start!=-1 && end!=-1) {
            line = str.substring(start, end);
            while(line.indexOf(",")!=-1){
                word = line.substring(0,line.indexOf(","));
                line = line.substring(line.indexOf(",")+1);
                String old=word;
                word="";
                for(int i=0; i<old.length(); i++){
                    if(old.charAt(i)=='('){break;}
                    if(old.charAt(i)=='-'){word=word+" ";}else if(old.charAt(i)=='_'){word=word+" ";}else if(old.charAt(i)==' '){word=word+"";}else{word=word+old.charAt(i);}
                }
                if(word.compareTo(originalWord)!=0){words.add(word);}
            }
            word = line.substring(line.indexOf(",")+1);
            String old=word;
            word="";
            for(int i=0; i<old.length(); i++){
                if(old.charAt(i)=='('){break;}
                if(old.charAt(i)=='-'){word=word+" ";}else if(old.charAt(i)=='_'){word=word+" ";}else if(old.charAt(i)==' '){word=word+"";}else{word=word+old.charAt(i);}
            }
            if(word.compareTo(originalWord)!=0){words.add(word);}
            return words;
        }else{
            return words;
        }
    }
    private ArrayList<String> breaksToWords(String sentence){
        ArrayList<String> words = new ArrayList<String>();
        
        while(sentence.charAt(0)==' '){
            sentence=sentence.substring(1);
        }
        while(sentence.indexOf(" ")!=-1){
            String word=sentence.substring(0,sentence.indexOf(" "));
            String old=word;
            word="";
            for(int i=0; i<old.length(); i++){
                if(old.charAt(i)=='('){break;}
                if(old.charAt(i)=='-'){word=word+" ";}else if(old.charAt(i)=='.'){}else if(old.charAt(i)==','){}else if(old.charAt(i)=='_'){word=word+" ";}else if(old.charAt(i)==32){word=word+"";}else{word=word+old.charAt(i);}
            }
            words.add(word);
            sentence=sentence.substring(sentence.indexOf(" ")+1);
        }
        String word=sentence;
        String old=word;
        word="";
        for(int i=0; i<old.length(); i++){
            if(old.charAt(i)=='('){break;}
            if(old.charAt(i)=='-'){word=word+" ";}else if(old.charAt(i)=='.'){}else if(old.charAt(i)==','){}else if(old.charAt(i)=='_'){word=word+" ";}else if(old.charAt(i)==32){word=word+"";}else{word=word+old.charAt(i);}
        }
        words.add(word);
        return words;
    }
    /**
     * get part of speech for word
     * @param String word
     * @return ArrayList<Integer> contains all POS type
     */
    public boolean[] getPOS(String word) throws JWNLException, FileNotFoundException {
        word=word.toLowerCase();

        boolean[] allPOS = new boolean[8];

        for(int i=0; i< allPOS.length; i++){allPOS[i]=false;}

        IndexWordSet indexWordSet = dictionary.lookupAllIndexWords(word);
        for(IndexWord indexWord:indexWordSet.getIndexWordArray()) {
            POS pos =indexWord.getPOS();
            if(pos.toString().indexOf("adjective")!=-1){allPOS[ADJECTIVE]=true;}
            if(pos.toString().indexOf("adverb")!=-1){allPOS[ADVERB]=true;}
            if(pos.toString().indexOf("cats")!=-1){allPOS[CATS]=true;}
            if(pos.toString().indexOf("noun")!=-1){allPOS[NOUN]=true;}
            if(pos.toString().indexOf("verb")!=-1){allPOS[VERB]=true;}
        }
        if(isConjunction(word)){allPOS[CONJUNCTION]=true;}
        if(isHFVerb(word)){allPOS[HFVERB]=true;}
        if(isPronoun(word)){allPOS[PRONOUN]=true;}
        return allPOS;
    }
    /**
     * check if word existing in the dictionary
     * @param String word
     * @return boolean return ture if word exist
     */
    public boolean isWordExist(String word) throws JWNLException, FileNotFoundException {
        word=word.toLowerCase();

        if(isConjunction(word)){return true;}
        if(isHFVerb(word)){return true;}
        if(isPronoun(word)){return true;}

        IndexWordSet indexWordSet = dictionary.lookupAllIndexWords(word);
        if(indexWordSet.size()==0){return false;}else{return true;}
    }
    private boolean isConjunction(String word){
        this.conjunctionFilePath = getClass().getClassLoader().getResourceAsStream("Dictionaries/additionalDict/conjunctions.txt");
        try (Scanner inputFile = new Scanner(conjunctionFilePath)) {
            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
                if(line.indexOf(word)!=-1){
                    return true;
                }
            }
            inputFile.close();
        }
        return false;
    }
    private boolean isHFVerb(String word){
        this.highFreqVerbFilePath = getClass().getClassLoader().getResourceAsStream("Dictionaries/additionalDict/highFrequentVerb.txt");
        try (Scanner inputFile = new Scanner(highFreqVerbFilePath)) {
            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
                if(line.indexOf(word)!=-1){
                    return true;
                }
            }
            inputFile.close();
        }       
        return false;
    }
    private boolean isPronoun(String word){
        this.pronounFilePath = getClass().getClassLoader().getResourceAsStream("Dictionaries/additionalDict/pronouns.txt");
        try (Scanner inputFile = new Scanner(pronounFilePath)) {
            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
                if(line.indexOf(word)!=-1){
                    return true;
                }
            }
            inputFile.close();
        }  
        return false;
    }
    /**
     * get Possible Correct Spelling for word
     * @param String word
     * @return ArrayList<Integer> contains all Possible Correct Spelling
     */
    public ArrayList<String> getPossibleCorrectSpelling(String word) throws FileNotFoundException, JWNLException, InterruptedException{
        ArrayList<String> possibleSpelling = new ArrayList<String>();
        word=word.toLowerCase();

        if(isWordExist(word)){
            possibleSpelling.add(word);
            return possibleSpelling;
        }

        possibleSpelling.addAll(swapCheck(word));
        possibleSpelling.addAll(cutCheck(word,1));

        String possibleW=checkInAddDict(word);
        if(possibleW!="EMPTY"){
            possibleSpelling.add(possibleW);
        }

        possibleSpelling.addAll(replaceCheck(word,1));
        possibleSpelling.addAll(addCheck(word,1));

        return possibleSpelling;
    }
    private String checkInAddDict(String word) throws FileNotFoundException{
        this.highFreqWordFilePath = getClass().getClassLoader().getResourceAsStream("Dictionaries/additionalDict/highFrequentWords.txt");
        int maxMatch=0;
        String maxMatchWord="EMPTY";
        InputStream[] paths = {highFreqWordFilePath, highFreqVerbFilePath, pronounFilePath, conjunctionFilePath};
        for (InputStream x: paths) {
            Scanner inputFile = new Scanner(x);

            while (inputFile.hasNext()) {
                String dictWord = inputFile.next();
                if(dictWord.charAt(0)==word.charAt(0)){
                    int match=0;

                    if(Math.abs(dictWord.length()-word.length())<=3) {
                        for (int i = 0; i < word.length(); i++) {
                            if (dictWord.indexOf(word.charAt(i)) != -1) {match++;}
                        }
                        for (int i = 0; i < word.length(); i++) {
                            if(i+3< word.length()) {
                                if (dictWord.indexOf(word.substring(i, i + 3)) != -1) {match=match+3;}
                            }else{
                                if (dictWord.indexOf(word.substring(i)) != -1) {match=match+3;}
                            }
                        }
                        if (maxMatch<match) {
                            maxMatch=match;
                            maxMatchWord=dictWord;
                        }
                    }
                }
            }
            inputFile.close();
        }
        return maxMatchWord;
    }
    private ArrayList<String> cutCheck(String word, int depth) throws FileNotFoundException, JWNLException {
        ArrayList<String> possibleSpelling = new ArrayList<String>();
        if(word.length()>0 && depth != 0) {
            for (int i = 0; i < word.length(); i++) {
                String possibleWord = "";
                for (int j = 0; j < word.length(); j++) {
                    if (j != i) {
                        possibleWord = possibleWord + word.charAt(j);
                    }
                }
                possibleSpelling.addAll(cutCheck(possibleWord,depth-1));
            }
        }
        if (isWordExist(word)) {
            possibleSpelling.add(word);
        }
        return possibleSpelling;
    }
    private ArrayList<String> replaceCheck(String word, int depth) throws FileNotFoundException, JWNLException {
        ArrayList<String> possibleSpelling = new ArrayList<String>();
        if(word.length()>0 && depth != 0) {
            for (int i = 0; i < word.length(); i++) {
                for(char letter:ALPHABET){
                    String possibleWord="";
                    for(int j=0; j<word.length(); j++){
                        if(j!=i){possibleWord=possibleWord+word.charAt(j);}else{possibleWord=possibleWord+letter;}
                    }
                    possibleSpelling.addAll(replaceCheck(possibleWord,depth-1));
                }
            }
        }
        if (isWordExist(word)) {
            possibleSpelling.add(word);
        }
        return possibleSpelling;
    }
    private ArrayList<String> addCheck(String word, int depth) throws FileNotFoundException, JWNLException {
        ArrayList<String> possibleSpelling = new ArrayList<String>();
        if(word.length()>0 && depth != 0) {
            for (int i = 0; i < word.length(); i++) {
                for(char letter:ALPHABET){
                    String possibleWord="";
                    for(int j=0; j<word.length(); j++){
                        if(j!=i){possibleWord=possibleWord+word.charAt(j);}else{possibleWord=possibleWord+word.charAt(j);possibleWord=possibleWord+letter;}
                    }
                    possibleSpelling.addAll(addCheck(possibleWord,depth-1));
                }
            }
        }
        if (isWordExist(word)) {
            possibleSpelling.add(word);
        }
        return possibleSpelling;
    }
    private ArrayList<String> swapCheck(String word) throws FileNotFoundException, JWNLException {
        ArrayList<String> possibleSpelling = new ArrayList<String>();
        char[] letters= new char[word.length()];
        for(int i=0; i<word.length(); i++){
            letters[i]=word.charAt(i);
        }
        char[] swap;
        for(int i=0; i<letters.length-1; i++){
            swap=letters.clone();
            char keep=swap[i];
            swap[i]=swap[i+1];
            swap[i+1]=keep;
            String swapWord="";
            for(int j=0; j<swap.length; j++){swapWord=swapWord+swap[j];}
            if (isWordExist(swapWord)) {
                possibleSpelling.add(swapWord);
            }
        }
        return possibleSpelling;
    }
}