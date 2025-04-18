package io.github.anthonyclemens.Player;

import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.Utils;
import io.github.anthonyclemens.states.Game;

public class Player {
    private float x;
    private float y;
    private float dx;
    private float dy;
    private float previousX; // Previous X position
    private float previousY; // Previous Y position
    private int health; // Player health
    private int maxHealth; // Maximum health
    private final float defaultSpeed; // Default movement speed
    private int direction; // Current look direction
    private boolean cameraLocked = true; // Lock camera to player when true
    private final Animation[] animations; // Array of animations for 8 directions
    private final Animation[] idleAnimations;
    private float renderX;
    private float renderY;
    private final SoundBox playerSoundBox; // SoundBox for player sounds
    private Rectangle hitbox; // Hitbox for collision detection
    // Grass
    private final List<String> grassWalk = Utils.getFilePaths("sounds/Player/Walk/Grass/walk", 1, 8); // Grass walk sounds
    private final List<String> grassRun = Utils.getFilePaths("sounds/Player/Run/Grass/run", 1, 8); // Grass walk sounds
    // Water
    private final List<String> waterWalk = Utils.getFilePaths("sounds/Player/Walk/Water/walk", 1, 8); // Water walk sounds
    // Sand
    private final List<String> sandWalk = Utils.getFilePaths("sounds/Player/Walk/Sand/walk", 1, 8); // Sand walk sounds
    private final List<String> sandRun = Utils.getFilePaths("sounds/Player/Run/Sand/run", 1, 8); // Sand walk sounds

    public Player(float startX, float startY, float speed, Animation[] animations, Animation[] idleAnimations) {
        Settings settings = Settings.getInstance(); // Get settings instance
        this.x = startX;
        this.y = startY;
        this.health = 100; // Initialize health
        this.maxHealth = 100; // Initialize max health
        this.defaultSpeed = speed;
        this.animations = animations;
        this.idleAnimations = idleAnimations;
        this.playerSoundBox = new SoundBox(); // Initialize SoundBox
        this.playerSoundBox.addSounds("grassWalk", grassWalk); // Add walk sounds to SoundBox
        this.playerSoundBox.addSounds("grassRun", grassRun); // Add run sounds to SoundBox
        this.playerSoundBox.addSounds("waterWalk", waterWalk); // Add water sounds to SoundBox

        this.playerSoundBox.addSounds("sandWalk", sandWalk); // Add sand sounds to SoundBox
        this.playerSoundBox.addSounds("sandRun", sandRun); // Add sand sounds to SoundBox

        this.playerSoundBox.setVolume(settings.getPlayerVolume()); // Set volume for player sounds
        this.hitbox = new Rectangle(this.x,this.y, animations[direction].getWidth(), animations[direction].getHeight());
    }

    public void update(Input input, int delta, int tile) {
        previousX = x; // Store current X position as previous
        previousY = y; // Store current Y position as previous
        dx = 0;
        dy = 0;
        float speed = this.defaultSpeed;
        String block = getBlockType(tile); // Get block type player is on

        handleMovementInput(input);
        speed = adjustSpeedAndAnimation(input, block, speed);

        if (dx != 0 || dy != 0) {
            normalizeAndMove(delta, speed);
            updateDirection(dx, dy);
            animations[direction].start(); // Play movement animation
            playMovementSound(block, speed);
        } else {
            idleAnimations[direction].start(); // Play idle animation
        }

        toggleCameraLock(input);
    }

    private void handleMovementInput(Input input) {
        if (input.isKeyDown(Input.KEY_W)) dy -= 1; // Up
        if (input.isKeyDown(Input.KEY_S)) dy += 1; // Down
        if (input.isKeyDown(Input.KEY_A)) dx -= 1; // Left
        if (input.isKeyDown(Input.KEY_D)) dx += 1; // Right
    }

    private float adjustSpeedAndAnimation(Input input, String block, float speed) {
        if (input.isKeyDown(Input.KEY_LSHIFT)) {
            speed = this.defaultSpeed * 1.5f;
            animations[direction].setSpeed(1.5f); // Increase animation speed for running
        } else {
            animations[direction].setSpeed(1f); // Reset animation speed for walking
        }
        if (block.equals("water")) {
            speed *= 0.5f; // Slow down on water
            animations[direction].setSpeed(0.5f); // Slow down animation speed for water
        }
        return speed;
    }

    private void normalizeAndMove(int delta, float speed) {
        dy *= 0.5f;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            dx /= length;
            dy /= length;
        }
        this.x += dx * speed * delta;
        this.y += dy * speed * delta;
    }

    private void playMovementSound(String block, float speed) {
        if (!this.playerSoundBox.isAnySoundPlaying()) {
            if (speed <= this.defaultSpeed) {
                this.playerSoundBox.playRandomSound(block + "Walk"); // Play walk sound
            } else {
                this.playerSoundBox.playRandomSound(block + "Run"); // Play run sound
            }
        }
    }

    private void toggleCameraLock(Input input) {
        if (input.isKeyPressed(Input.KEY_SPACE)) {
            cameraLocked = !cameraLocked;
        }
    }

    private void updateDirection(float dx, float dy) {
        if (dx == 0 && dy < 0) direction = 0; // Up
        else if (dx > 0 && dy < 0) direction = 1; // Up-right
        else if (dx > 0 && dy == 0) direction = 2; // Right
        else if (dx > 0 && dy > 0) direction = 3; // Down-right
        else if (dx == 0 && dy > 0) direction = 4; // Down
        else if (dx < 0 && dy > 0) direction = 5; // Down-left
        else if (dx < 0 && dy == 0) direction = 6; // Left
        else if (dx < 0 && dy < 0) direction = 7; // Up-left
    }

    public void render(GameContainer container, float zoom, float cameraX, float cameraY) {
        renderX = (x - cameraX) * zoom + container.getWidth() / 2f;
        renderY = (y - cameraY) * zoom + container.getHeight() / 2f;
        if (dx == 0 && dy == 0) {
            idleAnimations[direction].draw(renderX, renderY, animations[direction].getWidth() * zoom, animations[direction].getHeight() * zoom); // Render idle animation
        } else {
            animations[direction].draw(renderX, renderY, animations[direction].getWidth() * zoom, animations[direction].getHeight() * zoom); // Render movement animation
        }
        if(Game.showDebug){
            container.getGraphics().drawRect(renderX, renderY, animations[direction].getWidth()*zoom, animations[direction].getHeight()*zoom);
        }
        hitbox.setBounds((int)renderX, (int)renderY, (int)(animations[direction].getWidth()*zoom), (int)(animations[direction].getHeight()*zoom));
    }

    private String getBlockType(int tile){
        if (tile >= 0 && tile <= 4 || tile >= 10 && tile <= 14) {
            return "grass";
        } else if (tile >= 5 && tile <= 6) {
            return "sand";
        } else if (tile >= 23 && tile <= 24) {
            return "water";
        } else if (tile >= 50 && tile <= 60) {
            return "stone";
        } else {
            return "grass"; // Default to grass if tile type is unknown
        }
    }

    public void setVolume(float volume) {
        this.playerSoundBox.setVolume(volume); // Set volume for player sounds
    }

    public boolean isCameraLocked() {
        return cameraLocked;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRenderX(){
        return renderX;
    }

    public float getRenderY(){
        return renderY;
    }

    public float getPreviousX() {
        return previousX; // Get previous X position
    }

    public float getPreviousY() {
        return previousY; // Get previous Y position
    }

    public void setX(float newX) {
        this.x = newX; // Set player X position
    }

    public void setY(float newY) {
        this.y = newY; // Set player Y position
    }

    public Rectangle getHitbox(){
        return this.hitbox; // Get player hitbox
    }

    public float getSpeed(){
        return this.defaultSpeed; // Get player speed
    }

    public int getHealth() {
        return health; // Get player health
    }

    public void addHealth(int add) {
        if(this.health+add > this.maxHealth){
            this.health = this.maxHealth; // Set player health to max if it exceeds
        } else {
            this.health += add; // Add health to player
        }
    }

    public void subtractHealth(int subtract) {
        if(this.health-subtract < 0){
            this.health = 0; // Set player health to max if it exceeds
        } else {
            this.health -= subtract; // Add health to player
        }
    }

    public int getMaxHealth() {
        return maxHealth; // Get player max health
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth; // Set player max health
    }
}
