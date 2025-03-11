package io.github.anthonyclemens.states;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import io.github.anthonyclemens.GUI.Buttons.Carousel;
import io.github.anthonyclemens.GUI.Buttons.ColorTextButton;
import io.github.anthonyclemens.GUI.GUIElement;
import io.github.anthonyclemens.Math.TwoDimensionMath;
import io.github.anthonyclemens.Settings;
import io.github.anthonyclemens.Utils;

public class SettingsMenu extends BasicGameState{
    //Variables
    private Settings settings;
    private Input input;
    private List<String> validResolutions;
    private ColorTextButton[] sectionButtons;
    private final List<GUIElement> videoButtons = new ArrayList<>();
    private final List<GUIElement> audioButtons = new ArrayList<>();
    private final List<GUIElement> controlButtons = new ArrayList<>();
    private String titleString = "Video Settings";
    private ActiveMenu activeMenu = ActiveMenu.VIDEO;


    //Constants
    private static final float SPACING = 64;
    private static final int PADDING = 6;
    private final TrueTypeFont titleF = new TrueTypeFont(new Font("Courier", Font.BOLD, 32), true);
    private final TrueTypeFont menuOptionsF = new TrueTypeFont(new Font("Courier", Font.PLAIN, 32),true);
    private final String[] menuOptions = {"Video", "Audio", "Controls", "Apply", "Back"};

    private enum ActiveMenu {
        VIDEO,
        AUDIO,
        CONTROL
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        input = container.getInput();
        settings = Settings.getInstance();

        //Get Valid Screen Resolutions
        Set<String> validRes = new LinkedHashSet<>();
        try {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            Arrays.sort(modes, Comparator.comparingInt(mode -> mode.getWidth() * mode.getHeight()));
            for(DisplayMode m : modes){
                validRes.add(m.getWidth()+"x"+m.getHeight());
            }
            validResolutions = new ArrayList<>(validRes);
        } catch (LWJGLException e) {
            System.err.println("Cannot get display modes");
        }

        //Create the Video Buttons

        //Resolution Carousel
        videoButtons.add(new Carousel.Builder()
            .data(validResolutions)
            .leftImage(new Image("textures/GUI/ArrowsLeft2.png"))
            .rightImage(new Image("textures/GUI/ArrowsRight2.png"))
            .font(menuOptionsF)
            .color(Color.black)
            .position(container.getWidth()/2, 200)
            .onClick(currentItem ->{
                settings.setResolution(currentItem);
            })
            .build()
        );
        //Refresh Rate Carousel
        videoButtons.add(new Carousel.Builder()
            .data(new ArrayList<>(Arrays.asList("30","60","90","120","144","165","240")))
            .leftImage(new Image("textures/GUI/ArrowsLeft2.png"))
            .rightImage(new Image("textures/GUI/ArrowsRight2.png"))
            .font(menuOptionsF)
            .color(Color.black)
            .position(container.getWidth()/2, 300)
            .onClick(currentItem ->{
                settings.setMaxFPS(Integer.parseInt(currentItem));
            })
            .build()
        );



        //Create the Options Tabs
        sectionButtons = new ColorTextButton[menuOptions.length];
        for(int i=0; i<menuOptions.length; i++){
            if(!"Apply".equals(menuOptions[i])){
                sectionButtons[i] = new ColorTextButton.Builder()
                .textColor(Color.black)
                .backgroundColor(Color.darkGray)
                .font(menuOptionsF)
                .buttonString(menuOptions[i])
                .position(20, i*SPACING+200)
                .padding(PADDING)
                .build();
            }else{
                sectionButtons[i] = new ColorTextButton.Builder()
                .textColor(Color.black)
                .backgroundColor(Color.green)
                .font(menuOptionsF)
                .buttonString(menuOptions[i])
                .position(20, i*SPACING+200)
                .padding(PADDING)
                .build();
            }
        }

        for(int i=0; i<videoButtons.size(); i++){
            float newX = TwoDimensionMath.getMiddleX(videoButtons.get(i).getRect(), container.getWidth());
            videoButtons.get(i).move(newX, i*SPACING+200);
        }

    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(Color.gray);
        titleF.drawString(container.getWidth()/2-titleF.getWidth(titleString)/2, 100, titleString, Color.black);
        for(ColorTextButton tb : sectionButtons){
            tb.render(g);
        }
        switch (activeMenu) {
            case VIDEO -> renderMenu(videoButtons, g);
            case AUDIO -> renderMenu(audioButtons, g);
            case CONTROL -> renderMenu(controlButtons, g);
        }
    }

    private void renderMenu(List<GUIElement> list, Graphics g) {
        for (GUIElement element : list) {
            element.render(g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for (ColorTextButton sb : sectionButtons) {
            sb.update(input);
            if (sb.isClicked()) {
                switch (sb.getText()) {
                    case "Video" -> setMenu("Video Settings", ActiveMenu.VIDEO);
                    case "Audio" -> setMenu("Audio Settings", ActiveMenu.AUDIO);
                    case "Controls" -> setMenu("Controls", ActiveMenu.CONTROL);
                    case "Apply" -> applySettings(container);
                    case "Back" -> game.enterState(0);
                }
            }
        }
        List<GUIElement> buttonsToUpdate = switch (activeMenu) {
            case VIDEO -> videoButtons;
            case AUDIO -> audioButtons;
            case CONTROL -> controlButtons;
            default -> List.of();
        };
        buttonsToUpdate.forEach(ge -> ge.update(input));
    }

    private void setMenu(String title, ActiveMenu menu) {
        titleString = title;
        activeMenu = menu;
    }

    private void applySettings(GameContainer container) throws SlickException{
        Utils.saveSettings(settings);
        AppGameContainer gc = (AppGameContainer) container;
        gc.setDisplayMode(settings.getWidth(), settings.getHeight(), settings.isFullscreen());
        gc.setTargetFrameRate(settings.getMaxFPS());
        gc.setVSync(settings.isVsync());
        gc.setShowFPS(settings.isShowStats());
        for(int i=0; i<videoButtons.size(); i++){
            float newX = TwoDimensionMath.getMiddleX(videoButtons.get(i).getRect(), container.getWidth());
            videoButtons.get(i).move(newX, videoButtons.get(i).getY());
        }
    }

}
