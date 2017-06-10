package model_classes;

import android.support.annotation.NonNull;

import CSTime.DateTime;

/**
 * Created by tarek on 6/4/17.
 */

public class Alarm implements Comparable<Alarm>{
    private String key;
    private String text;
    private DateTime timeFrom;
    private DateTime timeTo;

    public Alarm(String key, String text, DateTime timeFrom, DateTime timeTo) {
        this.key = key;
        this.text = text;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DateTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(DateTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public DateTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(DateTime timeTo) {
        this.timeTo = timeTo;
    }

    @Override
    public int compareTo(@NonNull Alarm o) {
        return this.getTimeFrom().compareTo(o.getTimeFrom());
    }
}
