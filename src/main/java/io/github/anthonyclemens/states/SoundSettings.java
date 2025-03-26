package io.github.anthonyclemens.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.GUI.Banner;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Utils;

public class SoundSettings extends BasicGameState{
    private Image backgroundImage;
    private Banner titleBanner;

    private static final String TITLE_STRING = "Sound Settings";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException{
        // Set background image
        backgroundImage = new Image("textures/Background.png");
        // Create title banner
        Image bannerImage = new Image("textures/GUI/TextField/UI_Paper_Banner_01_Downward.png");
        bannerImage.setFilter(Image.FILTER_NEAREST);
        titleBanner = new Banner(bannerImage, TITLE_STRING, Utils.getFont(MAIN_FONT, 48f), TwoDimensionMath.getMiddleX(640, container.getWidth()), 10, 640, 251);
        titleBanner.changeYOffset(20f);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        drawBackground(container); // Render the background to fit screen (no stretching)
        titleBanner.render(g); // Render the Title banner
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
