package io.github.anthonyclemens.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.Utils;
public class DebugGUI {
    /**
     * DebugGUI is a utility class for rendering debug information on the screen.
     * It provides methods to display FPS, memory usage, mouse position, selected tile and chunk,
     * zoom level, biome information, and sound status.
     */

    private final TrueTypeFont font = Utils.getFont("fonts/Roboto-Black.ttf", 24f);

    private String getMemUsage() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024L * 1024L) + "MB";
    }

    private int[] getSelectedBlock(IsoRenderer renderer, Player player) {
        if (renderer == null || player == null) {
            return new int[4];
        }
        return renderer.screenToIsometric(player.getRenderX(), player.getRenderY());
    }
    public void renderDebugGUI(Graphics g, GameContainer container, IsoRenderer renderer, Player player, float zoom, JukeBox jukeBox, SoundBox ambientSoundBox) {
        g.setColor(Color.black);
        int index = 0;

        int[] selectedBlock = getSelectedBlock(renderer, player);

        String tile = (selectedBlock.length >= 2) ? selectedBlock[0] + ", " + selectedBlock[1] : "N/A";
        String chunk = (selectedBlock.length >= 4) ? selectedBlock[2] + ", " + selectedBlock[3] : "N/A";
        String biome = "N/A";
        if (renderer != null && player != null && selectedBlock.length >= 4) {
            biome = String.valueOf(renderer.getChunkManager().getBiomeForChunk(selectedBlock[2], selectedBlock[3]));
        }
        String song = (jukeBox != null) ? jukeBox.getCurrentSong() : "N/A";
        String ambient = (ambientSoundBox != null) ? ambientSoundBox.getCurrentSound() : "N/A";
        String playerSound = (player != null) ? player.getSound() : "N/A";
        String playerPos = (player != null) ? player.getX() + ", " + player.getY() : "N/A";
        String playerHealth = (player != null) ? String.valueOf(player.getHealth()) : "N/A";

        String[] debugStrings = new String[] {
            "FPS: " + container.getFPS() + " FPS",
            "Memory Usage: " + getMemUsage(),
            "Mouse: " + container.getInput().getMouseX() + ", " + container.getInput().getMouseY(),
            "Tile: " + tile,
            "Chunk: " + chunk,
            "Zoom level: " + Math.round(zoom * 100.0) / 100.0 + "x",
            "Biome: " + biome,
            "Song playing: " + song,
            "Ambient sound playing: " + ambient,
            "Player sound: " + playerSound,
            "Player position: " + playerPos,
            "Player health: " + playerHealth,
        };

        for (String s : debugStrings) {
            g.setFont(font);
            g.drawString(s, 10, 20f + 20 * index);
            index++;
        }
    }
}
