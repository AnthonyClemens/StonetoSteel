package io.github.anthonyclemens.Rendering;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

public class RenderUtils {
    public static void drawBackground(Image backgroundImage, GameContainer container) {
        int screenWidth = container.getWidth();
        int screenHeight = container.getHeight();

        float imageWidth = backgroundImage.getWidth();
        float imageHeight = backgroundImage.getHeight();

        float scaleFactor = Math.max(screenWidth / imageWidth, screenHeight / imageHeight);
        float scaledWidth = imageWidth * scaleFactor;
        float scaledHeight = imageHeight * scaleFactor;
        float x = (screenWidth - scaledWidth) / 2;
        float y = (screenHeight - scaledHeight) / 2;

        backgroundImage.draw(x, y, scaledWidth, scaledHeight);
    }
}
