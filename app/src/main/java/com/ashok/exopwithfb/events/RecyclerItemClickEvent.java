package com.ashok.exopwithfb.events;

/**
 * Created by ashok on 10/2/16.
 */

public class RecyclerItemClickEvent {

    String link;

    public RecyclerItemClickEvent(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
