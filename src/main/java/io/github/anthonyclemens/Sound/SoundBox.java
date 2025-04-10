package io.github.anthonyclemens.Sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.Log;

public class SoundBox {

    private HashMap<String, HashMap<String, Sound>> sounds;
    private final Random random;
    private float volume;
    private boolean playing;

    public SoundBox() {
        this.sounds = new HashMap<>();
        this.random = new Random();
        this.playing = false;
    }

    // Add a single sound to the SoundBox
    public void addSound(String category, String path) throws SlickException {
        Log.debug("Loading sound (" + category + ", " + path + ")");
        this.sounds.computeIfAbsent(category, k -> new HashMap<>()).put(path, new Sound(path));
    }

    // Add multiple sounds to the SoundBox
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

    // Stop all sounds in the SoundBox
    public void stopAllSounds() {
        for (HashMap<String, Sound> categorySounds : sounds.values()) {
            for (Sound sound : categorySounds.values()) {
                sound.stop();
            }
        }
        this.playing = false;
    }

    // Play a random sound from the specified category
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

    // Play a specific sound
    public void playSound(Sound sound) {
        if (sound == null) {
            Log.warn("Provided sound is null.");
            return;
        }
        sound.play(1.0f, volume);
    }

    // Set the volume for all sounds
    public void setVolume(float volume) {
        this.volume = volume;
    }

    // Check if any sound is playing
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

}