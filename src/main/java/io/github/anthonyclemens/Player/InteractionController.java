package io.github.anthonyclemens.Player;

import java.util.List;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameObjects.GameObject;
import io.github.anthonyclemens.GameObjects.SingleTileObjects.Item;
import io.github.anthonyclemens.GameObjects.SingleTileObjects.Items;
import io.github.anthonyclemens.WorldGen.Chunk;
import io.github.anthonyclemens.WorldGen.ChunkManager;

public class InteractionController {
    public static void interact(Input input, ChunkManager cm, Circle playerReach, Inventory playerInventory) {
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        if (!playerReach.contains(mouseX, mouseY)) return;
        if (!input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) return;

        int[] clickedLoc = cm.getIsoRenderer().screenToIsometric(mouseX, mouseY);

        Chunk chunk = cm.getChunk(clickedLoc[2], clickedLoc[3]);
        if (chunk == null) return; // Safety check

        List<GameObject> objects = chunk.getGameObjects();
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getHitbox().contains(mouseX, mouseY)) {
                GameObject obj = objects.get(i);
                String name = obj.getName();

                if (name.startsWith("ITEM_")) {
                    Items itemType = Items.valueOf(name);
                    if(playerInventory.addItem(itemType, ((Item) obj).getQuantity())){
                        chunk.removeGameObject(i);
                    }
                } else {
                    /*switch (name) {
                        case "bigTree" -> obj.removeHealth(10);
                        case "smallTree" -> obj.removeHealth(5);
                        case "fish" -> obj.removeHealth(5);
                        default -> Log.debug(name + " clicked on");
                    }*/
                    try {
                        obj.removeHealth(5);
                    } catch (Exception e) {
                        Log.debug("Error removing health, clicked on: "+name);
                    }
                }
            }
        }
    }
}
