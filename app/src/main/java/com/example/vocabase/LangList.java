package com.example.vocabase;

public class LangList {
    private String id;
    private String language;
    public LangList(String id, String language)
    {
        this.id = id;
        this.language = language;
    }
    public String getID() {
        return this.id;
    }
    public String getLanguage(){
        return this.language;
    }
}
