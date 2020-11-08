package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailability implements Event<Integer> {

    private String bookTitle;

    public CheckAvailability(String bookTitle){
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
