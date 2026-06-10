package com.github.JoyceK0.Olympic_Islands;

import java.util.ArrayList;

/*
Class Description:
This class manages all the dialogues and provides a way to create a linked list for dialogue storage and easy access.
 */


public class DialogueList {

    // Attributes

    public String name;
    public boolean done;
    public boolean mainBranch;
    public String triggerEvent;
    public ArrayList<String> dialogue;
    public String path1name;
    public String path2name;
    public String path3name;
    public int chosenOption;
    private DialogueList path1;
    private DialogueList path2;
    private DialogueList path3;
    public DialogueList prevDialogue;
    public String spritePath;

    // Constructor

    public DialogueList(String name, boolean mainBranch, ArrayList<String> dialogue, DialogueList prevDialogue, String triggerEvent) {
        this.name = name;
        this.mainBranch = mainBranch;
        this.dialogue = dialogue;
        this.prevDialogue = prevDialogue;
        this.spritePath = name+".png";
        this.triggerEvent = triggerEvent;

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



    // a .addPath method for adding a path name and its linked list ref number
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
