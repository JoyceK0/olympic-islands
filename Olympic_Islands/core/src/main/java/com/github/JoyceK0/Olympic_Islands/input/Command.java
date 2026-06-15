package com.github.JoyceK0.Olympic_Islands.input;

public enum Command {
    // An enumeration is a finite collection of constants and properties that cannot change, like cardinal points or days of the week.
    // By setting something's input as type enum, it can only hold the values assigned in the enum
    // We use an enum to define the finite number of commands the game can have
    // Since we are allowing custom keymapping from the user's end for input keys, this helps to keep track of our constants

    // movement
    LEFT,
    RIGHT,
    DOWN,
    UP,
    SELECT, // used to toggle dialogue pane
    CANCEL // esc command, unused for our program
}
