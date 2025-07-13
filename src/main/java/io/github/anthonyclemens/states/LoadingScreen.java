package io.github.anthonyclemens.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

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
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.Utils;

public class LoadingScreen extends BasicGameState {

    private Image loadingImage;
    private static final int BAR_WIDTH = 600;
    private static final int BAR_HEIGHT = 64;
    private static final String TITLE_STRING = "Loading";
    private static final String MAIN_FONT = "fonts/MedievalTimes.ttf";
    private static TrueTypeFont mainFont;

    private boolean doneLoading = false;
    private int frameCount = 0;
    private int completedSteps = 0;
    private int totalSteps = 0;
    private final Queue<Runnable> loadingSteps = new LinkedList<>();

    private final List<String> dayMusic = new ArrayList<>(Arrays.asList(
    "music/day/ForestWalk.ogg",
    "music/day/SpringFlowers.ogg"
    ));

    private final List<String> nightMusic = new ArrayList<>(Arrays.asList(
        "music/night/Moonlight-ScottBuckley.ogg",
        "music/night/AdriftAmongInfiniteStars-ScottBuckley.ogg"
    ));

    private final List<String> plainsSounds = new ArrayList<>(Arrays.asList(
    "sounds/Plains/birds.ogg",
    "sounds/Plains/birds1.ogg"
    ));

    private final List<String> nightSounds = new ArrayList<>(Arrays.asList(
        "sounds/Night/crickets.ogg",
        "sounds/Night/cicadas.ogg"
    ));

    private final List<String> desertSounds = new ArrayList<>(Arrays.asList(
        "sounds/Desert/wind.ogg"
    ));

    private final List<String> waterSounds = new ArrayList<>(Arrays.asList(
        "sounds/Water/flowingwater.ogg"
    ));

    private final List<String> beachSounds = new ArrayList<>(Arrays.asList(
        "sounds/Beach/waves.ogg"
    ));

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        Log.debug("Loading Screen entered.");
        mainFont = Utils.getFont(MAIN_FONT, 48f);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        loadingImage = new Image("textures/Background.png");
        loadingSteps.add(() -> Game.jukeBox = new JukeBox());
        loadingSteps.add(() -> Game.jukeBox.addSongs("dayMusic", dayMusic));
        loadingSteps.add(() -> Game.jukeBox.addSongs("nightMusic", nightMusic));
        loadingSteps.add(() -> Game.ambientSoundBox = new SoundBox());
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("plainsSounds", plainsSounds));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("desertSounds", desertSounds));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("waterSounds", waterSounds));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("beachSounds", beachSounds));
        loadingSteps.add(() -> Game.ambientSoundBox.addSounds("nightSounds", nightSounds));
        totalSteps = loadingSteps.size(); // Save initial size

    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        //RenderUtils.drawBackground(loadingImage,container);
        g.setColor(Color.white);
        mainFont.drawString(TwoDimensionMath.getMiddleX(mainFont.getWidth(MAIN_FONT), container.getWidth()), 10, TITLE_STRING);
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
        Log.debug("Progress: " + completedSteps + "/" + totalSteps + ", Queue left: " + loadingSteps.size());
        if (!doneLoading && frameCount > 1 && !loadingSteps.isEmpty()) {
            loadingSteps.poll().run(); // Execute next step
            completedSteps++;          // Update progress bar
        } else if (!doneLoading && loadingSteps.isEmpty()) {
            Log.debug("All resources loaded. Transitioning...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Log.error("Loading interrupted", e);
            }
            SharedData.enterState(GameStates.GAME, game);
            doneLoading = true;
        }
    }

    @Override
    public int getID() {
        return 98;
    }
}
