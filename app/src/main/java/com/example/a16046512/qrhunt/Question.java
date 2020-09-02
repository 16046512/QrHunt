package com.example.a16046512.qrhunt;

import java.util.ArrayList;

public class Question {
    private String id,question,ansa,ansb,ansc,ansd,hint,correctans;
    private ArrayList<String> questions = new ArrayList<String>();


    public Question(String id,String question, String ansa, String ansb, String ansc, String ansd, String hint, String correctans) {
        this.id = id;
        this.question = question;
        this.ansa = ansa;
        this.ansb = ansb;
        this.ansc = ansc;
        this.ansd = ansd;
        this.hint = hint;
        this.correctans = correctans;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnsa() {
        return ansa;
    }

    public String getAnsb() {
        return ansb;
    }

    public String getAnsc() {
        return ansc;
    }

    public String getAnsd() {
        return ansd;
    }

    public String getHint() {
        return hint;
    }

    public String getCorrectans() {
        return correctans;
    }
}
