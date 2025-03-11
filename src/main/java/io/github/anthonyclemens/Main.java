package io.github.anthonyclemens;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.states.Game;
import io.github.anthonyclemens.states.MainMenu;
import io.github.anthonyclemens.states.NewGame;
import io.github.anthonyclemens.states.SettingsMenu;


public class Main extends StateBasedGame{

        public Main() {
            super("IsoGame");
        }
        public static void main(String[] args){
        if(args.length>0){
            //
        }
        try {
            Settings settings = Settings.getInstance();
            Utils.loadSettings(settings);
            //Initialize the Slick2d engine
            AppGameContainer app = new AppGameContainer(new Main());
            app.setDisplayMode(settings.getWidth(), settings.getHeight(), settings.isFullscreen());
            app.setVSync(settings.isVsync());
		    app.setAlwaysRender(true);
		    app.setShowFPS(settings.isShowStats());
		    app.setMaximumLogicUpdateInterval(60);
		    app.setTargetFrameRate(settings.getMaxFPS());
            //app.setIcon("");
            app.start();
        } catch (SlickException e){
            System.err.println("Failed to create Slick2D Container");
        }
    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Isometric Game Engine";
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new MainMenu());
        addState(new NewGame());
        addState(new SettingsMenu());
        addState(new Game());
    }

}