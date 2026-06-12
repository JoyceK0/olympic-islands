package com.github.JoyceK0.Olympic_Islands.input;

public enum Command {
    // An enumeration is a finite collection of constants and properties that cannot change, like cardinal points or days of the week
    // We use an enum to define the finite number of commands the game can have
    // Since we are allowing custom keymapping from the user's end for input keys, this helps to keep track of our constants
    // Better than creating a whole class or an untransferrable list in the main game class
    LEFT,
    RIGHT,
    DOWN,
    UP,
    SELECT,
    CANCEL
}
