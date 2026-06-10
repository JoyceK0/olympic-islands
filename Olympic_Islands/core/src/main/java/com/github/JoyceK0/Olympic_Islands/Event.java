package com.github.JoyceK0.Olympic_Islands;

/*
Class Description:
This is the class which creates the basic event object
used to store event names and their completion boolean
values in the event hash map.
 */

public class Event {

    public String name;
    public boolean completion;

    //Constructor
    public Event(String name) {
        this.name = name;
        completion = false;
    }

    public void completed() {
        this.completion = true;
    }

}
