package io.github.anthonyclemens.GameObjects.SingleTileObjects;

public enum Items {
    ITEM_WOOD("items", 1),
    ITEM_STONE("items", 0),
    ITEM_CACTUS("main", 9),
    ITEM_FISH("fish", 0),
    ITEM_SEED("items", 3),
    ITEM_ZOMBIE_FLESH("items", 0),
    ITEM_BERRIES("items", 4),
    ITEM_STRING("items", 0),
    ITEM_BONES("items", 2),
    ITEM_WOODEN_SWORD("weapons", 0);

    private final String spriteSheet;
    private final int spriteIndex;

    Items(String spriteSheet, int spriteIndex) {
        this.spriteSheet = spriteSheet;
        this.spriteIndex = spriteIndex;
    }

    public String getSpriteSheet() {
        return spriteSheet;
    }

    public int getSpriteIndex() {
        return spriteIndex;
    }
}
