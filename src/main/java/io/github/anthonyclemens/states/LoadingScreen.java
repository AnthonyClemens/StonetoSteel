package io.github.anthonyclemens.states;

import java.util.LinkedList;
import java.util.Queue;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Rendering.SpriteManager;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.Utils;
import io.github.anthonyclemens.utils.AssetLoader;

public class LoadingScreen extends BasicGameState {

    private Image loadingImage;
    private static final int BAR_WIDTH = 600;
    private static final int BAR_HEIGHT = 64;
    private static final String TITLE_STRING = "Loading";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";
    private static TrueTypeFont mainFont;

    private boolean doneLoading = false;
    private int frameCount = 0;
    private int completedSteps;
    private int totalSteps = 0;
    private final Queue<Runnable> loadingSteps = new LinkedList<>();

    private String soundPack;
    private String lastSoundPack;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        Log.debug("Loading Screen entered.");
        mainFont = Utils.getFont(MAIN_FONT, 48f);
        if(!Settings.getInstance().getSoundPack().equals(lastSoundPack)){
            Log.debug("Sound pack changed from " + lastSoundPack + " to " + Settings.getInstance().getSoundPack());
            soundPack = Settings.getInstance().getSoundPack();
            lastSoundPack = soundPack;
            loadingSteps.clear();
            totalSteps = 0;
            init(container, game);
            completedSteps = 0;
            doneLoading=false;
        }else{
            Log.debug("Sound pack unchanged: " + soundPack);
            if(doneLoading){
                Log.debug("Already loaded, skipping loading steps.");
                SharedData.enterState(GameStates.GAME, game);
            }
        }
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        loadingImage = new Image("textures/Background.png");
        loadingSteps.add(() -> Game.jukeBox = new JukeBox());
        loadingSteps.add(() -> Game.jukeBox.addSongs("dayMusic", AssetLoader.loadListFromFile(soundPack, "dayMusic")));
        loadingSteps.add(() -> Game.jukeBox.addSongs("nightMusic", AssetLoader.loadListFromFile(soundPack, "nightMusic")));
        loadingSteps.add(() -> Game.ambientSoundBox = new SoundBox());
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("plainsSounds", AssetLoader.loadListFromFile(soundPack, "plainsSounds")));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("desertSounds", AssetLoader.loadListFromFile(soundPack, "desertSounds")));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("waterSounds", AssetLoader.loadListFromFile(soundPack, "waterSounds")));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("beachSounds", AssetLoader.loadListFromFile(soundPack, "beachSounds")));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("nightSounds", AssetLoader.loadListFromFile(soundPack, "nightSounds")));
        loadingSteps.add(() -> Game.passiveMobSoundBox = new SoundBox());
        loadingSteps.add(() -> Game.passiveMobSoundBox.addSounds("fishHurt", AssetLoader.loadListFromFile(soundPack, "fishHurtSounds")));
        loadingSteps.add(() -> Game.enemyMobSoundBox = new SoundBox());
        loadingSteps.add(() -> Game.gameObjectSoundBox = new SoundBox());
        loadingSteps.add(() -> Game.gameObjectSoundBox.addSounds("bigTreeHitSounds", AssetLoader.loadListFromFile(soundPack, "bigTreeHitSounds")));
        loadingSteps.add(() -> Game.gameObjectSoundBox.addSounds("smallTreeHitSounds", AssetLoader.loadListFromFile(soundPack, "smallTreeHitSounds")));
        loadingSteps.add(() -> SpriteManager.addSpriteSheet("main", "textures/World/18x18.png", 18, 18));
        loadingSteps.add(() -> SpriteManager.addSpriteSheet("fishes", "textures/Organisms/fish.png", 16, 16));
        loadingSteps.add(() -> SpriteManager.addSpriteSheet("bigtrees", "textures/World/48x48.png", 48, 48));
        loadingSteps.add(() -> SpriteManager.addSpriteSheet("smalltrees", "textures/World/16x32.png", 16, 32));
        totalSteps = loadingSteps.size(); // Save initial size
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        //RenderUtils.drawBackground(loadingImage,container);
        //g.setColor(Color.white);
        //mainFont.drawString(TwoDimensionMath.getMiddleX(mainFont.getWidth(MAIN_FONT), container.getWidth()), 10, TITLE_STRING);
        // Dimensions and position
        int barX = (container.getWidth() - BAR_WIDTH) / 2;
        int barY = container.getHeight() - 80;
        // Colors and progress
        g.setColor(Color.darkGray);
        g.fillRect(barX, barY, BAR_WIDTH, BAR_HEIGHT);
        float percent = (float) completedSteps / totalSteps;
        int filledWidth = (int) (BAR_WIDTH * percent);
        g.setColor(Color.green);
        g.fillRect(barX, barY, filledWidth, BAR_HEIGHT);
        String loadingText = "Loading " + (int)(percent * 100) + "%";
        float textX = TwoDimensionMath.getMiddleX(mainFont.getWidth(loadingText), container.getWidth());
        float textY = barY - 64f;
        mainFont.drawString(textX, textY, loadingText, Color.white);
        frameCount++;
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if (!doneLoading && frameCount % 2 == 0 && !loadingSteps.isEmpty()) {
            Log.debug("Progress: " + completedSteps + "/" + totalSteps);
            loadingSteps.poll().run(); // Execute next step
            completedSteps++;          // Update progress bar
        } else if (!doneLoading && loadingSteps.isEmpty()) {
            Log.debug("All resources loaded. Transitioning...");
            SharedData.enterState(GameStates.GAME, game);
            doneLoading = true;
        }
    }

    @Override
    public int getID() {
        return GameStates.LOADING_SCREEN.getID();
    }
}
