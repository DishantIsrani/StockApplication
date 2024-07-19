package com.example.stockapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsItem {
    private String image;
    private String source;
    private long datetime;
    private String headline;
    private String description;
    private String newsurllink;

    public NewsItem(String image, String source, long datetime, String headline, String description, String newsurllink) {
        this.image = image;
        this.source = source;
        this.datetime = datetime;
        this.headline = headline;
        this.description = description;
        this.newsurllink = newsurllink;
    }

    public String getImage() {
        return image;
    }

//    public void setImage(String image) {
//        this.image = image;
//    }

    public String getSource() {
        return source;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getHeadline() {
        return headline;
    }

    public String getFormattedDateTime() {
        // Convert timestamp to a readable format (e.g., "Apr 20, 2024")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        return dateFormat.format(new Date(datetime * 1000L));
    }

    public String getElapsedTime() {
        // Get the current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Calculate the time difference between current time and news time
        long timeDifferenceMillis = currentTimeMillis - (datetime * 1000L);

        // Convert milliseconds to seconds
        long seconds = timeDifferenceMillis / 1000;

        // Convert seconds to minutes and hours
        long minutes = seconds / 60;
        long hours = minutes / 60;

        // Return the elapsed time in seconds, minutes, or hours
        if (hours > 0) {
            return hours + " hours ago";
        } else if (minutes > 0) {
            return minutes + " minutes ago";
        } else {
            return seconds + " seconds ago";
        }
    }



    public String getDescription(){
        return description;
    }

    public String getNewsurllink(){
        return newsurllink;
    }
}

