package com.github.JoyceK0.Olympic_Islands.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;

/*
This class is the event handler, and it contains all the trigger events for the game.
Certain objects can only be interacted with if certain events occurred, therefore
this hash map contains and organizes all this data for easy access.
Although this is not fully implemented in the game itself besides the dialogues, it provides
the basic framework for the game, improving scalability dractically for all sorts fo complex interactions.
 */

public class EventMap extends DialogueHash {

    public Event[] buckets;
    //key is the event name string and the value is a event value signifying completion of event

    //Constructor
    public EventMap(int howManyBuckets) {

        //Create a new buckets array
        buckets = new Event[howManyBuckets];

    }


    //Overloaded constructor with default value
    public EventMap() {

        //Create a new buckets array
        buckets = new Event[50];
    }



    //METHODS FOR EVENT MAP

    public void addToTable(String eventToAdd) {
        //Add an event to the hash table

        //Error checking for input value
        if(eventToAdd.equals("null")) {
            System.out.println("Event to add value is null!");
            return;
        }

        // Perform linear probing to find the appropriate insertion spot available

        int initialPos = calcBucket(eventToAdd, buckets.length);
        int pos = initialPos;

        do {

            // If event exists and is the same as the one trying to add, return
            if(buckets[pos] != null) {
                if(buckets[pos].name.equals(eventToAdd)) {
                    return;
                }
            }


            else { // Value is null
                buckets[pos] = new Event(eventToAdd); // Add event to table
                return;
            }

            // Increment position if value is not null and not equal to value to add, meaning its occupied
            pos = (pos + 1) % buckets.length; // Increase position being checked

        } while(pos != initialPos);

        // If exits means it found no empty spot
        System.out.println("Hash Table is FULL!");

    }//end of addToTable



    // Not a required method for the game but still there
    public Event removeFromMap(String name) {
        //Removes an event from the table based on its name and returns its ref value

        int eventPlace = calcBucket(name, buckets.length);
        Event removedEvent = buckets[eventPlace]; // save removed event ref value
        buckets[eventPlace] = null; //replace original value with null

        return removedEvent;

    }//end of removeFromMap



    public Event getEvent(String name) {
        //find and return event reference value from table based on name

        int initialPos = calcBucket(name, buckets.length); //Initial and expected position to find event object
        int pos = initialPos;
        // Perform linear probing to find the event

        do {

            if(buckets[pos] == null){ //Since no events are ever removed, null will not be a problem when searching
                break;
            }

            else if(buckets[pos].name.equals(name)){ // If event is found, then return its object ref value
                return buckets[pos];
            }

            pos = (pos + 1) % buckets.length; // Increase position by one, and wrap around if reach the end

        } while(pos != initialPos);

        //This will only run if a value is not returned in the loop, DNE in table
        System.out.println("Event not found! Returned null value.");

        return null;

    }//end of getEvent



    // a file opener function which opens an inputted file path and creates the event objects, adding them to the hash table
    public void loadEvents() {


        FileHandle file = Gdx.files.internal("data/TestEvents.txt"); //initialize the file to read through file handler class
        BufferedReader reader = new BufferedReader(file.reader()); //wrap file handler inside buffered reader to optimize memory management and read entire liens rather than by character
        String line; // instantiate the holder variable for the read line


        try { // Wrapped in try/catch for the case if the file turns out to be corrupted or the read operation fails, prevents the game from crashing entirely


            while((line = reader.readLine()) != null) { // Keep running till the end of the file is reached

                String name = line.strip().trim();
                this.addToTable(name);

            }

            reader.close(); // close the file reader

        } catch (IOException e) { // This will run if the text file for events is unreadable due to some restraints
            System.out.println("Event file is unreadable!");
            e.printStackTrace(); // prints the exact diagnostic problem in the console after the error message
        }

    }


}
