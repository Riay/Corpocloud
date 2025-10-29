package com.corpocloud.notes;

public class Note {
    private long id;
    private String title;
    private String description;
    private String date;

    public Note(long id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}
