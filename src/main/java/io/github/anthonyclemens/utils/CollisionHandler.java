package io.github.anthonyclemens.utils;

import java.util.List;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.states.Game;

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
                Rectangle playerHit = player.getHitbox();
                Rectangle objectHit = gob.getHitbox();
                
                // Manually compute the intersection rectangle
                float intersectX = Math.max(playerHit.getX(), objectHit.getX());
                float intersectY = Math.max(playerHit.getY(), objectHit.getY());
                float intersectWidth = Math.min(playerHit.getX() + playerHit.getWidth(), objectHit.getX() + objectHit.getWidth()) - intersectX;
                float intersectHeight = Math.min(playerHit.getY() + playerHit.getHeight(), objectHit.getY() + objectHit.getHeight()) - intersectY;
                if (intersectWidth > 0 && intersectHeight > 0) {
                    // Compute centers from hitboxes
                    float playerCenterX = playerHit.getX() + playerHit.getWidth() / 2;
                    float playerCenterY = playerHit.getY() + playerHit.getHeight() / 2;
                    float objectCenterX = objectHit.getX() + objectHit.getWidth() / 2;
                    float objectCenterY = objectHit.getY() + objectHit.getHeight() / 2;
                    float dx = player.getX() - player.getPreviousX();
                    float dy = player.getY() - player.getPreviousY();
                    if (intersectWidth < intersectHeight) {
                        // Horizontal collision resolution
                        if (playerCenterX < objectCenterX) {
                            // Player approaches from the left, push left.
                            player.setX(player.getX() - intersectWidth);
                        } else {
                            // Player approaches from the right, push right.
                            player.setX(player.getX() + intersectWidth);
                        }
                        // Simulate a bounce: reverse horizontal movement component.
                        player.setPreviousX(player.getX() + dx);
                    } else {
                        // Vertical collision resolution
                        if (playerCenterY < objectCenterY) {
                            // Player approaches from above, push upward.
                            player.setY(player.getY() - intersectHeight);
                        } else {
                            // Player approaches from below, push downward.
                            player.setY(player.getY() + intersectHeight);
                        }
                        // Simulate a bounce: reverse vertical movement component.
                        player.setPreviousY(player.getY() + dy);
                    }
                    
                    if (Game.showDebug) {
                        Log.debug("Collision detected with game object: " + gob.getName());
                    }
                }
            }
        }
    }
}


