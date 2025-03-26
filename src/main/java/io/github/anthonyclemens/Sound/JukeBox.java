package io.github.anthonyclemens.Sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class JukeBox {

    private HashMap<String, HashMap<String, Music>> songs;
    private Music currentMusic;
    private final Random random;
    private float volume;

    public JukeBox(Builder builder) {
        this.songs = builder.songs;
        this.random = new Random();
    }

    public JukeBox() {
        this.songs = new HashMap<>();
        this.random = new Random();
    }

    // Add a single song to the jukebox
    public void addSong(String category, String path) throws SlickException {
        Log.debug("Loading song ("+category+","+path+")");
        this.songs.computeIfAbsent(category, k -> new HashMap<>()).put(path, new Music(path, true));
    }

    // Add multiple songs to the jukebox
    public void addSongs(String category, List<String> paths) {
        paths.forEach(path -> {
            try {
                this.songs
                    .computeIfAbsent(category, k -> new HashMap<>())
                    .put(path, new Music(path, true));
                Log.debug("Loading song (" + category + "," + path + ")");
            } catch (SlickException e) {
                Log.error(e);
            }
        });
    }

    public void playRandomSong(String category) {
        stopMusic();
        HashMap<String, Music> categorySongs = this.songs.get(category);
        if (categorySongs == null || categorySongs.isEmpty()) {
            Log.warn("No songs available in category: " + category);
            return;
        }
        List<String> paths = new ArrayList<>(categorySongs.keySet());
        String randomPath = paths.get(this.random.nextInt(paths.size()));
        Music randomSong = categorySongs.get(randomPath);
        playSong(randomSong);
    }

    // Loop a single song
    public void playSong(Music song){
        stopMusic();
        currentMusic=song;
        currentMusic.setVolume(volume);
        Log.debug("Playing song at "+findSongPath(song));
        currentMusic.loop();
    }

    // Stop the music
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    // Check if music is playing
    public boolean isPlaying() {
        return currentMusic != null && currentMusic.playing();
    }

    public void setVolume(float volume){
        this.volume=volume;
    }

    public String findSongPath(Music song) {
        if (song == null) {
            Log.warn("Provided song is null.");
            return null;
        }

        // Iterate through all categories
        for (Map.Entry<String, HashMap<String, Music>> categoryEntry : this.songs.entrySet()) {
            HashMap<String, Music> categorySongs = categoryEntry.getValue();

            // Search through the songs in each category
            for (Map.Entry<String, Music> songEntry : categorySongs.entrySet()) {
                if (songEntry.getValue().equals(song)) {
                    return songEntry.getKey(); // Return the path if a match is found
                }
            }
        }

        Log.warn("Song not found.");
        return null; // Return null if the song isn't found
    }

    public static class Builder {
        private HashMap<String, HashMap<String, Music>> songs;

        public Builder() {
            this.songs = new HashMap<>();
        }

        public Builder addSong(String category, String path) throws SlickException {
            this.songs.computeIfAbsent(category, k -> new HashMap<>()).put(path, new Music(path, true));
            return this;
        }

        public Builder addSongs(String category, List<String> paths) {
            paths.forEach(path -> {
                try {
                    this.songs
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .put(path, new Music(path, true));
                    Log.debug("Loading song (" + category + "," + path + ")");
                } catch (SlickException e) {
                    Log.error(e);
                }
            });
            return this;
        }

        public JukeBox build() {
            return new JukeBox(this);
        }
    }
}

