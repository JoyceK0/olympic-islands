package com.github.JoyceK0.Olympic_Islands.dialogue;

import java.util.ArrayList;

// This class manages all the dialogues and provides a way to create a generic decision tree for dialogue storage and easy access.


public class DialogueList {

    // Attributes

    public String name; // the name of the character speaking
    public boolean done; // keeps track of if the dialogue is done and needs to be skipped next time
    public boolean mainBranch; // keeps track of if the current dialogue is part of the main storyline or not
    public String triggerEvent; // what event needs to be triggered to run this dialogue
    public String causesEvent; // what event does the current dialogue cause if any
    public ArrayList<String> dialogue; // the main dialogue array list
    public String path1name; //the path names are the choice options that are displayed on the dialogue pane
    public String path2name;
    public String path3name;
    public int chosenOption; // the option the player as chosen for the dialogue between 1, 2, and 3
    private DialogueList path1;
    private DialogueList path2;
    private DialogueList path3;
    public DialogueList prevDialogue; // link to previous dialogue for easier navigation
    public String spritePath; // link to the sprite image that shows up beside the character dialogue

    // Constructor

    public DialogueList(String name, boolean mainBranch, ArrayList<String> dialogue, DialogueList prevDialogue, String triggerEvent, String causesEvent) {
        this.name = name;
        this.mainBranch = mainBranch;
        this.dialogue = dialogue;
        this.prevDialogue = prevDialogue;
        this.spritePath = name+".png";
        this.triggerEvent = triggerEvent;
        this.causesEvent = causesEvent;

        path1name = null;
        path2name = null;
        path3name = null;
        chosenOption = -1;
        path1 = null;
        path2 = null;
        path3 = null;
    }

    // Methods

    // a .next method for returning the next dialogue in the list, return null if no dialogue left in that branch
    public DialogueList next(){

        switch(this.chosenOption){

            case -1:
                if(this.path2 != null){ //If another option exists, and no choice is present, must choose!
                    return this; //keep player stuck on this dialogue till an option is chosen
                }
                return path1; //path1 will be null if it does not contain anything, signifying end of convo.
            case 1:
                return this.path1;
            case 2:
                return this.path2;
            case 3:
                return this.path3;
            default:
                System.out.println("PATH ERROR: Invalid value in chosenOption!");
                break;
        }

        return null;
    }



    // a .addPath method for adding a path name and its reference value
    public void addPath(String pathName, DialogueList path){
        if(path1==null){
            this.path1name = pathName;
            this.path1 = path;
        }
        else if(path2==null){
            this.path2name = pathName;
            this.path2 = path;
        }
        else if(path3==null){
            this.path3name = pathName;
            this.path3 = path;
        }
        else{
            System.out.println("ALL DIALOGUE PATHS ARE FULL");
        }
    }

}
