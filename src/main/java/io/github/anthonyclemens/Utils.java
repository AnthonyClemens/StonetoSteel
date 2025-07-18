package io.github.anthonyclemens;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Utility class providing helper methods for font loading and file path generation.
 */
public class Utils {

    /**
     * Loads a TrueTypeFont from the specified resource location and font size.
     * @param location Path to the font resource.
     * @param fontSize Desired font size.
     * @return TrueTypeFont instance or null if loading fails.
     */
    public static TrueTypeFont getFont(String location, float fontSize) {
        InputStream inputStream = null;
        try {
            inputStream = ResourceLoader.getResourceAsStream(location);
            // Create AWT Font from input stream
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont(fontSize);
            return new TrueTypeFont(awtFont, true);
        } catch (FontFormatException | IOException e) {
            Log.error(e);
        } finally {
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException e) { /* ignore */ }
            }
        }
        return null;
    }

    /**
     * Generates a list of file paths with a numeric suffix and .ogg extension.
     * @param prefix Path prefix (e.g., "sounds/Player/Walk/Grass/walk").
     * @param start  Starting index (inclusive).
     * @param end    Ending index (exclusive).
     * @return List of file paths.
     */
    public static List<String> getFilePaths(String prefix, int start, int end){
        List<String> paths = new ArrayList<>();
        for(int i = start; i < end; i++){
            paths.add(prefix+String.format("%01d", i)+".ogg");
        }
        return paths;
    }

    public static void takeScreenShot(Graphics gfx, GameContainer gc) {
        int width = gc.getWidth();
        int height = gc.getHeight();
        BufferedImage screenshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
        gfx.getArea(0,0, width, height, buffer);
        buffer.flip();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte r = buffer.get();
                byte g = buffer.get();
                byte b = buffer.get();
                byte a = buffer.get();

                // Convert byte values (0-255) to int and combine into ARGB format
                int argb = ((a & 0xFF) << 24) |
                        ((r & 0xFF) << 16) |
                        ((g & 0xFF) << 8) |
                        (b & 0xFF);

                screenshot.setRGB(x, y, argb);
            }
        }
        String filePath = "screenshot_" + System.currentTimeMillis() + ".png";

        try {
            File outputFile = new File("screenshots/"+filePath);
            ImageIO.write(screenshot, "png", outputFile);
            Log.debug("Image saved successfully to: " + filePath);
        } catch (IOException e) {
            Log.error("Error saving image: " + e.getMessage());
        }
    }
}
