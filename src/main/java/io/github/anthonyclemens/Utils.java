package io.github.anthonyclemens;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
}
