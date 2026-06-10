package com.github.JoyceK0.Olympic_Islands;

/*
Class Description:
This hash table contains all the NPC names as keys and the values are
the top of the linked list stack of dialogues for each NPC. This helps
access the dialogue list quickly. This also contains the method to load
dialogues into the hash table and create the linked queue lists (FIFO).
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogueHash {

    //Buckets attribute basically hold the main Array,
    // and all the values within its buckets are the ArrayLists with the values!
    public DialogueList[] buckets;

    //Constructor
    public DialogueHash(int howManyBuckets) {

        //Create a new buckets array
        buckets = new DialogueList[howManyBuckets];

        //When adding characters to the hashmap, on the index of its name we add the
        // ref value for the top linked list for its dialogue set

    }


    //Overloaded constructor with default value
    public DialogueHash() {

        //Create a new buckets array
        buckets = new DialogueList[50];
    }


    //METHODS OF DIALOGUE HASH


    public int calcBucket(String name) {
        //Performs hashing index calculation

        int hashNumber = Math.abs(name.hashCode()); // This converts the name string into an arbitrary number that stays consistent for each name every runtime
        return(hashNumber%buckets.length);

    }//end of calcBucket



    public void addToTable(DialogueList npcToAdd) {
        //Add a npc to the hash table

        //Error checking for input value
        if(npcToAdd == null) {
            System.out.println("NPC to add value is null!");
            return;
        }

        // Perform linear probing to find the appropriate insertion spot available

        int initialPos = calcBucket(npcToAdd.name);
        int pos = initialPos;

        do {

            // If npc exists and is the same as the one trying to add, return
            if(buckets[pos] != null) {
                if(buckets[pos].name.equals(npcToAdd.name)) {
                    return;
                }
            }

            else { // Value is null
                buckets[pos] = npcToAdd; // Add npc to table
                return;
            }

            // Increment position if value is not null and not equal to value to add, meaning its occupied
            pos = (pos + 1) % buckets.length; // Increase position being checked

        } while(pos != initialPos);

        // If exits means it found no empty spot
        System.out.println("Hash Table is FULL!");

    }//end of addToTable


    // Not a required method for the game but still there
    public DialogueList removeFromTable(String name) {
        //Removes a npc from the table based on their name and returns their ref value

        int npcPlace = calcBucket(name);
        DialogueList removedNpc = buckets[npcPlace]; // save removed npc ref value
        buckets[npcPlace] = null; //replace original value with null

        return removedNpc;

    }//end of removeFromTable



    public DialogueList getNpc(String name) {
        //find and return npc starting dialogue reference value from table based on name

       int initialPos = calcBucket(name); //Initial and expected position to find npc data
       int pos = initialPos;
        // Perform linear probing to find the npc

       do {

           if(buckets[pos] == null){ //Since no NPCs are ever removed, null will not be a problem when searching
               break;
           }

           else if(buckets[pos].name.equals(name)){ // If NPC is found, then return its DialogueList ref value
               return buckets[pos];
           }

           pos = (pos + 1) % buckets.length; // Increase position by one, and wrap around if reach the end

       } while(pos != initialPos);

        //This will only run if a value is not returned in the loop, DNE in table
        System.out.println("NPC not found! Returned null value.");

        return null;

    }//end of getNpc



    // ONLY FOR TESTING

    public void displayTable() {
        //display contents of buckets in order

        System.out.println("\n_______________________\n\nTHE CONTENTS OF THE HASH TABLE:"); //header

        //Print the buckets
        for(int i = 0; i < buckets.length; i++) {

            //If bucket contents is null, print error
            if(buckets[i] == null) {
                System.out.println("\n    " + i + ". EMPTY BUCKET\n");
            }

            //Print out NPC name
            else {
                System.out.println("\n    " + i + ". " + buckets[i].name + "\n");
            }
        }

    }//end of displayTable



    // a file opener function which opens an inputted file path and creates the linked lists for every character, adding the top of the queue to the hash table
    public void loadDialogue() {

        FileHandle file = Gdx.files.internal(""); //initialize the file to read through file handler class
        BufferedReader reader = new BufferedReader(file.reader()); //wrap file handler inside buffered reader to optimize memory management and read entire liens rather than by character
        String line; // instantiate the holder variable for the read line

        try { // Wrapped in try/catch for the case if the file turns out to be corrupted or the read operation fails, prevents the game from crashing entirely

            /*
            Formatting for text document. Read before to understand the following code.

            Character Name             //whose dialogue map this is
            Dialogue            //the dialogue they start with
            //Now right after this you have two options. You either write the dialogue that directly follows the one above, no choices given to the player
            // Or you first write the alternate paths this conversation could take after the dialogue above (like small talk, not part of main dialogue path)
            //OPTION ONE: main dialogue path
            -                   // The hash signifies that the following dialogue is part of the main dialogue branch and is not small talk, MUST ALWAYS precede main talk dialogue
            Speaker             //name of the person speaking following dialogue (could be main character)
            Choice Name         // Path name for player (can be null if no alternate small talk or branched for the previous dialogue available)
            Dialogue            //the dialogue, split by semicolons if required to appear in separate portions
            Trigger Event       //the trigger event that needs to be satisfied to be able to trigger this dialogue, can be null if none required
            //OPTION TWO: small talk alternate paths
            #                   // the number signifies the number of possible small-talks, max of two, MUST ALWAYS precese small talk dialogue
            Speaker
            Choice Name         // this is what appears on the screen for the player, also labeled as path name in the code
            Dialogue
            //The trio of speaker, choice name, and dialogue continues again if there is another small talk path available
            //IMPORTANT: is any dialogue has multiple options, it must be preceeded by the small talk part first before the main path dialogue for that one.
            //  So for example if I have the previous dialogue being "hello", three options can be "How are you", "what is the mission", and "bye bye". If
            //  "what is the mission" is the main path dialogue, then in the formatting, the dialogues for "How are you" and "bye bye" come first before the
            //  main-path dialogue formatting for "what is the mission" underneath the dialogue "Hello"
            ...
                                //Once all the dialogues for one character end, an empty line separates the start of the dialogue set for another character, samfe format as above
            Character Name //whose dialogue is it
            ...
             */


            while((line = reader.readLine()) != null) { // Keep running till the end of the file is reached

                String name = line.strip().trim(); // According to formatting, the first line in every iteration will be the character name
                ArrayList<String> dialogue = new ArrayList<>(Arrays.asList(reader.readLine().trim().strip().split(";"))); // get the first dialogue to go with the character name

                DialogueList character = new DialogueList(name, true, dialogue, null, ""); // instantiate character root
                DialogueList prevDialogue = character; //assign previous dialogue as the top of the stack/the root

                line = reader.readLine(); //set line as the first line under the character


                while(line!=null && !(line.trim().isEmpty())) { // now keep reading till encountering an empty line which separates different character dialogues

                    if(isInteger(line)) { // if the new line is an integer, it signifies alternate paths that deviate from main path, first read them and add as sub-paths

                        int numSmallTalk = Integer.parseInt(line); // find the number of small-talk paths included (max of 2)

                        for(int i = 0; i < numSmallTalk; i++) { // read the sub dialogues using a set format

                            String currentSpeakerName = reader.readLine().trim(); // find the name of the character talking (sometimes the main character can also talk in between the main dialogue path, so we need to know who is talking to set graphics)
                            String pathName = reader.readLine().trim(); // find sub-path name (what is shown on screen as an option)
                            dialogue = new ArrayList<>(Arrays.asList(reader.readLine().trim().strip().split(";"))); // find and split the dialogue into an array list if it is too long

                            DialogueList smallTalk = new DialogueList(currentSpeakerName, false, dialogue, prevDialogue, ""); // instantiate new object and assign attributes

                            prevDialogue.addPath(pathName, smallTalk); // add as sub path to the one before
                        }

                        line = reader.readLine(); // after reading the sub dialogues, read the next line to skip the hash that precedes the main branch dialogue

                    }

                    String speakerName = reader.readLine().trim(); // set the name of the character speaking as of the moment

                    String pathName; //instantiate pathName variable and then check to see if null. Can be null if no alternate paths exist to the dialogue.
                    if ((line = reader.readLine().trim()).equals("null")) { // if it was null, then the pathName also remains as null
                        pathName = null;  }
                    else { pathName = line; } // else set as the pathName

                    dialogue = new ArrayList<>(Arrays.asList(reader.readLine().trim().strip().split(";"))); // determine the new dialogue and format it into array list if it is shown in parts
                    String triggerEvent = reader.readLine().trim(); // set the trigger event variable, can exist for some mainstream dialogues which open after quest is completed

                    DialogueList mainDialogue = new DialogueList(speakerName, true, dialogue, prevDialogue, triggerEvent); // instantiate new dialogue object with the main branch data

                    prevDialogue.addPath(pathName, mainDialogue); // add the main path dialogue to the previous dialogue

                    prevDialogue = mainDialogue; // set the previous as the current main dialogue in order to move onto the next set

                    line = reader.readLine(); // increment the line reader to check if the character's dialogues are finished (empty line) or document ended (null value)

                }

                // Finally, add the character to the hashtable
                this.addToTable(character);

            }

            reader.close(); // close the file reader

        } catch (IOException e) { // This will run if the text file for dialogues is unreadable due to some restraints
            System.out.println("Dialogue file is unreadable!");
            e.printStackTrace(); // prints the exact diagnostic problem in the console after the error message
        }

    }



    //Helper method for checking is string is int or not
    private static boolean isInteger(String toCheck) {
        try { // tries to convert to integer, if works then returns true
            Integer.parseInt(toCheck.trim());
            return true;
        } catch(NumberFormatException e) { // if fails returns false
            return false;
        }
    }

}
