package io.github.anthonyclemens.Player;

import java.util.EnumMap;
import java.util.Map;

import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameObjects.Items;

public class Inventory {

    private final Map<Items, Integer> itemCounts = new EnumMap<>(Items.class);
    private final Map<Items, Integer> itemMaxSizes = Map.of(
        Items.ITEM_WOOD, 100,
        Items.ITEM_STONE, 100
    );

    public boolean addItem(Items itemType, int quantity) {
        int current = getItemCount(itemType);
        int max = itemMaxSizes.getOrDefault(itemType, Integer.MAX_VALUE);
        int toAdd = Math.min(quantity, max - current);

        if (toAdd > 0) {
            itemCounts.merge(itemType, toAdd, Integer::sum);
            Log.debug("Added " + itemType + " x" + toAdd);
            return true;
        } else {
            Log.debug("Cannot add " + itemType + ". Max reached.");
            return false;
        }
    }

    public int getItemCount(Items itemType) {
        return itemCounts.getOrDefault(itemType, 0);
    }

    public int getTotalItemCount() {
        return itemCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Items, Integer> getItems() {
        return itemCounts;
    }

    public void removeItem(Items item, int quantity) {
        if (itemCounts.containsKey(item)) {
            int currentCount = itemCounts.get(item);
            if (currentCount >= quantity) {
                itemCounts.put(item, currentCount - quantity);
                Log.debug("Removed " + item + " x" + quantity);
            } else {
                Log.debug("Cannot remove " + item + ". Not enough in inventory.");
            }
        } else {
            Log.debug("Cannot remove " + item + ". Item not found in inventory.");
        }
    }

}
