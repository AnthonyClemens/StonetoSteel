package io.github.anthonyclemens;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import io.github.anthonyclemens.states.ControlSettings;
import io.github.anthonyclemens.states.Game;
import io.github.anthonyclemens.states.MainMenu;
import io.github.anthonyclemens.states.NewGame;
import io.github.anthonyclemens.states.SettingsMenu;
import io.github.anthonyclemens.states.SoundSettings;
import io.github.anthonyclemens.states.VideoSettings;


public class Main extends StateBasedGame{

        public Main() {
            super("Stone to Steel");
        }
        public static void main(String[] args){
        try {
            Settings settings = Settings.getInstance();
            settings = Utils.loadSettings(settings);
            //Initialize the Slick2d engine
            AppGameContainer app = new AppGameContainer(new Main());
            app.setDisplayMode(settings.getWidth(), settings.getHeight(), settings.isFullscreen());
            app.setVSync(settings.isVsync());
		    app.setAlwaysRender(true);
		    app.setShowFPS(false);
		    app.setMaximumLogicUpdateInterval(60);
		    app.setTargetFrameRate(settings.getMaxFPS());
            //app.setIcon("");
            app.start();
        } catch (SlickException e){
            Log.error("Failed to create Slick2D Container");
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
    }

}