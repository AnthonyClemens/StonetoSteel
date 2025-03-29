package io.github.anthonyclemens;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class Utils {

    public static TrueTypeFont getFont(String location, float fontSize) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream(location);

            // Create AWT Font
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont(fontSize); // Set font size
            return new TrueTypeFont(awtFont, true);
        } catch (FontFormatException | IOException e) {
            Log.error(e);
        }
        return null;
    }

}
