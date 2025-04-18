package io.github.anthonyclemens.utils;

import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.WorldGen.Biome;

public class AmbientSoundManager {
    private final IsoRenderer renderer;
    private final JukeBox jukeBox;
    private final SoundBox ambientSoundBox;
    private boolean dayNightSwitch = true;
    private Biome lastBiome = null;

    public AmbientSoundManager(IsoRenderer renderer, JukeBox jukeBox, SoundBox ambientSoundBox) {
        this.renderer = renderer;
        this.jukeBox = jukeBox;
        this.ambientSoundBox = ambientSoundBox;
    }

    public void playAmbientMusic(DayNightCycle env) {
        if (env.isSunDown()) {
            if (!dayNightSwitch) {
                Log.debug("Switching to night music...");
                jukeBox.playRandomSong("nightMusic");
                dayNightSwitch = true;
            }
        } else {
            if (dayNightSwitch) {
                Log.debug("Switching to day music...");
                jukeBox.playRandomSong("dayMusic");
                dayNightSwitch = false;
            }
        }
    }

    public void playAmbientSounds(DayNightCycle env, Player player, IsoRenderer renderer) {
        int[] playerBlock = renderer.screenToIsometric(player.getRenderX(), player.getRenderY());
        if (env.isSunDown() && !ambientSoundBox.isAnySoundPlaying()) {
            ambientSoundBox.playRandomSound("nightSounds");
        }
        Biome currentBiome = renderer.getChunkManager().getBiomeForChunk(playerBlock[2], playerBlock[3]);
        if (lastBiome != currentBiome) {
            lastBiome = currentBiome;
            ambientSoundBox.stopAllSounds();
        }
        if (env.isSunUp() && !ambientSoundBox.isAnySoundPlaying()) {
            switch (currentBiome) {
                case DESERT -> ambientSoundBox.playRandomSound("desertSounds");
                case PLAINS -> ambientSoundBox.playRandomSound("plainsSounds");
                case WATER -> ambientSoundBox.playRandomSound("waterSounds");
                case BEACH -> ambientSoundBox.playRandomSound("beachSounds");
            }
        }
    }
}
