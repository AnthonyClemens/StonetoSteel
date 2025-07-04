package io.github.anthonyclemens;

import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public class SharedData {
    // Private constructor to prevent instantiation
    private SharedData() {}

    private static int seed = 0;
    private static boolean hotstart = false;
    private static GameStates lastState = null;

    public static boolean isHotstart() {
        return hotstart;
    }

    public static void setHotstart(boolean value) {
        hotstart = value;
    }

    public static void setSeed(int newSeed) {
        seed = newSeed;
    }

    public static int getSeed() {
        return seed;
    }

    public static void enterState(GameStates state, StateBasedGame game) {
        // Assign lastState to the current state before switching
        GameStates currentState = getCurrentState(game);
        lastState = currentState;
        Log.debug("Entering state: " + state + " from " + (lastState != null ? lastState : "none"));
        game.enterState(state.getID());
    }

    /**
     * Utility to get the current GameStates enum from the StateBasedGame.
     */
    private static GameStates getCurrentState(StateBasedGame game) {
        int currentId = game.getCurrentState().getID();
        return GameStates.fromID(currentId);
    }

    public static GameStates getLastState() {
        return lastState;
    }
}
