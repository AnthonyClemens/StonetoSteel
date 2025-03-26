package io.github.anthonyclemens.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GUI.Banner;

public class VideoSettings extends BasicGameState{
    private List<String> validResolutions;
    private Image backgroundImage;
    private Banner titleBanner;

    private static final String TITLE_STRING = "Sound Settings";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        //Get Valid Screen Resolutions
        Set<String> validRes = new LinkedHashSet<>();
        try {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            Arrays.sort(modes, Comparator.comparingInt(mode -> mode.getWidth() * mode.getHeight()));
            for(DisplayMode m : modes){
                if(m.getHeight()>=768){
                    validRes.add(m.getWidth()+"x"+m.getHeight());
                }
            }
            validResolutions = new ArrayList<>(validRes);
        } catch (LWJGLException e) {
            Log.error("Cannot get display modes");
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        
    }

    private void drawBackground(GameContainer container) {
        // Get screen dimensions
        int screenWidth = container.getWidth();
        int screenHeight = container.getHeight();

        // Get image dimensions
        float imageWidth = backgroundImage.getWidth();
        float imageHeight = backgroundImage.getHeight();

        // Calculate the scaling factor and center position in one step
        float scaleFactor = Math.max(screenWidth / imageWidth, screenHeight / imageHeight);
        float scaledWidth = imageWidth * scaleFactor;
        float scaledHeight = imageHeight * scaleFactor;
        float x = (screenWidth - scaledWidth) / 2;
        float y = (screenHeight - scaledHeight) / 2;

        // Render the scaled image
        backgroundImage.draw(x, y, scaledWidth, scaledHeight);
    }
}
