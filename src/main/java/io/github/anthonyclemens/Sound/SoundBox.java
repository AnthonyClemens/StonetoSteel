package io.github.anthonyclemens.Sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.Log;

/**
 * SoundBox manages categorized sound effects, allowing random playback and resource cleanup.
 */
public class SoundBox {

    private HashMap<String, HashMap<String, Sound>> sounds;
    private final Random random;
    private float volume;
    private boolean playing;

    /**
     * Constructs a new SoundBox.
     */
    public SoundBox() {
        this.sounds = new HashMap<>();
        this.random = new Random();
        this.playing = false;
    }

    /**
     * Adds a single sound to a category.
     * @param category Category name.
     * @param path     Path to the sound file.
     */
    public void addSound(String category, String path) throws SlickException {
        Log.debug("Loading sound (" + category + ", " + path + ")");
        this.sounds.computeIfAbsent(category, k -> new HashMap<>()).put(path, new Sound(path));
    }

    /**
     * Adds multiple sounds to a category.
     * @param category Category name.
     * @param paths    List of sound file paths.
     */
    public void addSounds(String category, List<String> paths) {
        paths.forEach(path -> {
            try {
                this.sounds.computeIfAbsent(category, k -> new HashMap<>()).put(path, new Sound(path));
                Log.debug("Loading sound (" + category + ", " + path + ")");
            } catch (SlickException e) {
                Log.error(e);
            }
        });
    }

    /**
     * Stops all currently playing sounds.
     */
    public void stopAllSounds() {
        for (HashMap<String, Sound> categorySounds : sounds.values()) {
            for (Sound sound : categorySounds.values()) {
                sound.stop();
            }
        }
        this.playing = false;
    }

    /**
     * Plays a random sound from the specified category.
     * @param category Category name.
     */
    public void playRandomSound(String category) {
        HashMap<String, Sound> categorySounds = this.sounds.get(category);
        if (categorySounds == null || categorySounds.isEmpty()) {
            Log.warn("No sounds available in category: " + category);
            return;
        }
        List<String> paths = new ArrayList<>(categorySounds.keySet());
        String randomPath = paths.get(this.random.nextInt(paths.size()));
        Sound randomSound = categorySounds.get(randomPath);
        playSound(randomSound);
    }

    /**
     * Plays a specific sound.
     * @param sound Sound object to play.
     */
    public void playSound(Sound sound) {
        if (sound == null) {
            Log.warn("Provided sound is null.");
            return;
        }
        sound.play(1.0f, volume);
    }

    /**
     * Sets the playback volume for all sounds.
     * @param volume Volume (0.0 to 1.0).
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * Checks if any sound is currently playing.
     * @return true if any sound is playing, false otherwise.
     */
    public boolean isAnySoundPlaying() {
        for (HashMap<String, Sound> categorySounds : sounds.values()) {
            for (Sound sound : categorySounds.values()) {
                if (sound.playing()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the file path of any currently playing sound, or null if none is playing.
     * @return The path of the currently playing sound as a String.
     */
    public String getCurrentSound() {
        for (HashMap<String, Sound> categorySounds : sounds.values()) {
            for (Map.Entry<String, Sound> entry : categorySounds.entrySet()) {
                if (entry.getValue().playing()) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }


    /**
     * Returns the category name of any currently playing sound, or null if none is playing.
     * @return The category of the currently playing sound as a String.
     */
    public String getCurrentCategory() {
        for (Map.Entry<String, HashMap<String, Sound>> categoryEntry : sounds.entrySet()) {
            for (Sound sound : categoryEntry.getValue().values()) {
                if (sound.playing()) {
                    return categoryEntry.getKey();
                }
            }
        }
        return "none";
    }
    /**
     * Releases all loaded sounds and clears resources.
     */
    public void clear() {
        stopAllSounds();
        for (HashMap<String, Sound> category : sounds.values()) {
            for (Sound sound : category.values()) {
                if (sound != null) {
                    sound.stop();
                    sound = null;
                }
            }
            category.clear();
        }
        sounds.clear();
    }
}