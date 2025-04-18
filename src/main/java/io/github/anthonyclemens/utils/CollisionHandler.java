package io.github.anthonyclemens.utils;

import java.util.List;

import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.WorldGen.Chunk;

public class CollisionHandler {
    public void checkCollision(Player player, Chunk currentChunk) {
        if (currentChunk == null) {
            Log.debug("Current chunk is null, skipping collision check.");
            return;
        }
        List<GameObject> gameObjects = currentChunk.getGameObjects();
        if (gameObjects == null || gameObjects.isEmpty()) {
            return;
        }
        for (GameObject gob : gameObjects) {
            if (gob.getHitbox().intersects(player.getHitbox())) {
                float dx = player.getX() - player.getPreviousX();
                float dy = player.getY() - player.getPreviousY();
                if (Math.abs(dx) > Math.abs(dy)) {
                    player.setX(player.getPreviousX() + dx);
                } else {
                    player.setY(player.getPreviousY() + dy);
                }
                Log.debug("Collision detected with game object: " + gob.getName());
            }
        }
    }
}
