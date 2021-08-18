package com.example.flashcards.model;

public class Card {
    String id;
    String question;
    String answer;
    String whatToShow;
    String search;

    public Card(String id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.whatToShow = question;
        this.search = question.toLowerCase();
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setWhatToShow(String whatToShow) {
        this.whatToShow = whatToShow;
    }

    public String getWhatToShow() {
        return whatToShow;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
