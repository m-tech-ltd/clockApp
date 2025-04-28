package com.github.mtechltd;

import java.time.LocalTime;

public class TimeEvent {
    private final String name;
    private final LocalTime time;

    public TimeEvent(String name, LocalTime time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public LocalTime getTime() {
        return time;
    }
}