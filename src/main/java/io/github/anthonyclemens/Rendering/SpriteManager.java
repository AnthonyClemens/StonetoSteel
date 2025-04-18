package io.github.anthonyclemens.Rendering;

import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;


public class SpriteManager {
    private SpriteManager() {}
    private static final HashMap<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static void addSpriteSheet(String name, String path, int tileWidth, int tileHeight) {
        try {
            SpriteSheet spriteSheet = new SpriteSheet(path, tileWidth, tileHeight);
            spriteSheets.put(name, spriteSheet);
        } catch (SlickException e) {
            Log.error("Error loading sprite sheet: " + e.getMessage());
        }
    }

    public static void removeSpriteSheet(String name) {
        try {
            spriteSheets.remove(name);
        } catch (Exception e) {
            Log.error("Error removing sprite sheet: " + e.getMessage());
        }
    }

    public static SpriteSheet getSpriteSheet(String name) {
        if(spriteSheets.get(name) == null){
            Log.warn("Sprite sheet not found: " + name);
            return null;
        }
        return spriteSheets.get(name);
    }

    public static int getSpriteWidth(String name) {
        SpriteSheet spriteSheet = getSpriteSheet(name);
        if (spriteSheet == null) {
            Log.error("Cannot get sprite width, sprite sheet is null: " + name);
            return -1;
        }
        return spriteSheet.getWidth()/spriteSheet.getHorizontalCount();
    }

    public static int getSpriteHeight(String name) {
        SpriteSheet spriteSheet = getSpriteSheet(name);
        if (spriteSheet == null) {
            Log.error("Cannot get sprite height, sprite sheet is null: " + name);
            return -1;
        }
        return spriteSheet.getHeight()/spriteSheet.getVerticalCount();
    }

}
