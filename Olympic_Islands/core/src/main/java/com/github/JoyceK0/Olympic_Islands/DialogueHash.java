package com.github.JoyceK0.Olympic_Islands;

/*
Class Description:
This hash table contains all the NPC names as keys and the values are
the top of the linked list stack of dialogues for each NPC. This helps
access the dialogue list quickly.
 */


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

        int hashNumber = name.hashCode(); // This converts the name string into an arbitrary number that stays consistent for each name every runtime
        return(hashNumber%buckets.length);

    }//end of calcBucket



    public void addToTable(DialogueList npcToAdd) {
        //Add a npc to the hash table

        //Error checking for input value
        if(npcToAdd == null) {
            System.out.println("NPC to add value is null!");
            return;
        }
        else if(buckets[calcBucket(npcToAdd.name)] != null) {

            if(buckets[calcBucket(npcToAdd.name)].name.equals(npcToAdd.name)) { //If NPC already added then avoid addition
                return;
            }

            // If NPC not exists, then perform linear probing to find the next insertion spot available

            int pos = calcBucket(npcToAdd.name);

            while(true){

                pos += 1; // Increase position being checked

                // If npc exists and is the same as the one trying to add, return
                if(buckets[pos] != null) {
                    if(buckets[pos].name.equals(npcToAdd.name)) {
                        return;
                    }

                    // Skip rest of the code if value is null and not equal to value to add
                    else{ continue; }
                }

                buckets[pos] = npcToAdd; // Add npc to table if spot is empty
                return;
            }

        }

        //Add npc
        buckets[calcBucket(npcToAdd.name)] = npcToAdd;


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

       int pos = calcBucket(name); //Initial and expected position to find npc data

        // Perform linear probing to find the npc

       while(true) {

           if(buckets[pos] == null){ //Since no NPCs are ever removed, null will not be a problem when searching
               break;
           }

           else if(buckets[pos].name.equals(name)){ // If NPC is found, then return its DialogueList ref value
               return buckets[pos];
           }

           pos += 1; // Increase position by one
       }

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


}
