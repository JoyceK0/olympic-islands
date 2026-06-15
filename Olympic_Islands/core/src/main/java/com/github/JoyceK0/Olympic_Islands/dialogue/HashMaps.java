package com.github.JoyceK0.Olympic_Islands.dialogue;

public class HashMaps {

    // This parent class contains basic methods for the Dialogue and Event Hash Classes

    //Constructor
    public HashMaps() {
    }

    // Calculate the hash index
    public int calcBucket(String name, int bucketsLength) {

        int hashNumber = name.hashCode() & 0x7fffffff; // This converts the name string into an arbitrary number that stays consistent for each name during a single runtime.
        // The & 0x7fffffff ensures that the hashCode is always a positive number. We could use Math.abs() but it only works to a certain level and may still output a negative
        // index for certain cases
        return(hashNumber%bucketsLength);

    }//end of calcBucket


    // THIS METHOD IS ONLY FOR TESTING
    public void displayTable(Object[] buckets) {
        //display contents of buckets in order

        System.out.println("\n_______________________\n\nTHE CONTENTS OF THE HASH TABLE:"); //header

        //Print the buckets
        for(int i = 0; i < buckets.length; i++) {

            //If bucket contents is null, print error
            if(buckets[i] == null) {
                System.out.println("\n    " + i + ". EMPTY BUCKET\n");
            }

            //Print out bucket name depending on which class it is
            else if(buckets[i] instanceof DialogueList bucket) {
                System.out.println("\n    " + i + ". " + bucket.name + "\n");
            }
            else if(buckets[i] instanceof Event bucket) {
                System.out.println("\n    " + i + ". " + bucket.name + "\n");
            }
            else {
                System.out.println("\n    " + i + ". UNKNOWN TYPE" + buckets[i].toString() + "\n");
            }
        }

    }//end of displayTable

}
