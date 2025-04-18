package io.github.anthonyclemens.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.IsoRenderer;

public class DebugGUI {
    public void renderDebugGUI(Graphics g, GameContainer container, IsoRenderer renderer, Player player, float zoom) {
        Input input = container.getInput();
        g.setColor(Color.black);
        int[] selectedBlock = renderer.screenToIsometric(player.getRenderX(), player.getRenderY());
        g.drawString("FPS: " + container.getFPS(), 10, 20);
        g.drawString("Mouse: " + input.getMouseX() + ", " + input.getMouseY(), 10, 40);
        g.drawString("Tile: " + selectedBlock[0] + ", " + selectedBlock[1], 10, 60);
        g.drawString("Chunk: " + selectedBlock[2] + ", " + selectedBlock[3], 10, 80);
        g.drawString("Zoom level: " + Math.round(zoom * 100.0) / 100.0 + "x", 10, 100);
        g.drawString("Biome: " + renderer.getChunkManager().getBiomeForChunk(selectedBlock[2], selectedBlock[3]), 10, 120);
    }
}
