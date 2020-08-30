package com.example.vocabase;

public class WordDetails {
    private String id;
    private String word;
    private String sentence;
    public WordDetails(String id, String word, String sentence)
    {
        this.id = id;
        this.word = word;
        this.sentence = sentence;
    }
    public String getID() {
        return this.id;
    }
    public String getWord(){
        return this.word;
    }
    public String getSentence(){
        return this.sentence;
    }
}
