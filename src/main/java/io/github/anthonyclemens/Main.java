package io.github.anthonyclemens;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;

import io.github.anthonyclemens.states.ControlSettings;
import io.github.anthonyclemens.states.Credits;
import io.github.anthonyclemens.states.Game;
import io.github.anthonyclemens.states.LoadingScreen;
import io.github.anthonyclemens.states.MainMenu;
import io.github.anthonyclemens.states.Multiplayer;
import io.github.anthonyclemens.states.MultiplayerMenu;
import io.github.anthonyclemens.states.NewGame;
import io.github.anthonyclemens.states.PauseMenu;
import io.github.anthonyclemens.states.SettingsMenu;
import io.github.anthonyclemens.states.SoundSettings;
import io.github.anthonyclemens.states.VideoSettings;


public class Main extends StateBasedGame{

    private static AppGameContainer app;

    public Main() {
        super("Stone to Steel");
    }
    public static void main(String[] args){
        if (args.length > 0 && args[0].contains("-server")) {
            System.out.println("Server Mode Entered");
            return;
        }
        Settings settings = Settings.getInstance();
        try {
            SteamAPI.loadLibraries("./libraries");

            if (!SteamAPI.init()) {
                Log.error("Steamworks initialization failed. Is the Steam client running?");
            } else {
                Log.debug("Steamworks API initialized.");
            }
        } catch (SteamException e) {
            Log.error("Error loading Steam libraries: " + e.getMessage());
        }
        try {
            //Initialize the Slick2d engine
            app = new AppGameContainer(new Main());
            setSettings(settings);
            //app.setIcon("");
            app.start();
        } catch (SlickException e){
            try {
                Log.debug("Failed to initialize Slick2D, creating default settings file.");
                settings.writeDefaultOptions();
                main(args); // Retry after creating default settings
            } catch (Exception e1) {
                Log.error("Default settings file creation failed.", e1);
            }
            Log.error("Failed to create Slick2D Container");
            Log.error(e);
        }
    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Stone to Steel";
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new MainMenu());
        addState(new NewGame());
        addState(new SettingsMenu());
        addState(new Game());
        addState(new VideoSettings());
        addState(new SoundSettings());
        addState(new ControlSettings());
        addState(new PauseMenu());
        addState(new LoadingScreen());
        addState(new Credits());
        addState(new MultiplayerMenu());
        addState(new Multiplayer());
    }

    public static void setSettings(Settings settings) throws SlickException{
        app.setDisplayMode(settings.getWidth(), settings.getHeight(), settings.isFullscreen());
        app.setVSync(settings.isVsync());
		app.setAlwaysRender(true);
		app.setShowFPS(false);
		app.setMaximumLogicUpdateInterval(60);
		app.setTargetFrameRate(settings.getMaxFPS());
    }

}