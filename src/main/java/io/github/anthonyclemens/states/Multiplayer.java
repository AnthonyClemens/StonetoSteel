package io.github.anthonyclemens.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;

import io.github.anthonyclemens.GameStates;
import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.Camera;
import io.github.anthonyclemens.Rendering.IsoRenderer;
import io.github.anthonyclemens.SharedData;
import io.github.anthonyclemens.Sound.JukeBox;
import io.github.anthonyclemens.Sound.SoundBox;
import io.github.anthonyclemens.SteamFriendsHandler;
import io.github.anthonyclemens.WorldGen.ChunkManager;
import io.github.anthonyclemens.utils.AmbientSoundManager;
import io.github.anthonyclemens.utils.ClientHandler;
import io.github.anthonyclemens.utils.CollisionHandler;
import io.github.anthonyclemens.utils.DebugGUI;
import io.github.anthonyclemens.utils.DisplayHUD;
import io.github.anthonyclemens.utils.SaveLoadManager;

public class Multiplayer extends BasicGameState{
    // Game Variables
    private float zoom = 2.0f;
    private float cameraX = 0;
    private float cameraY = 0;
    private boolean dragging = false;
    private float lastMouseX;
    private float lastMouseY;
    private boolean showHUD = true;

    // Game Objects
    private Camera camera;
    private Player player;
    private DayNightCycle env;
    private Calender calender;
    private IsoRenderer renderer;
    public static JukeBox jukeBox;
    public static SoundBox ambientSoundBox;
    public static SoundBox passiveMobSoundBox;
    public static SoundBox enemyMobSoundBox;
    public static SoundBox gameObjectSoundBox;
    ChunkManager chunkManager;

    private CollisionHandler collisionHandler;
    private DebugGUI debugGUI;
    private DisplayHUD displayHUD;
    private AmbientSoundManager ambientSoundManager;
    private SaveLoadManager saveLoadManager;
    private ClientHandler clientHandler;


    @Override
    public int getID() {
        return GameStates.MULTIPLAYER.getID();
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        Log.debug("Multiplayer Initialized");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        SteamFriends steamFriends;
        try {
            steamFriends = new SteamFriends(new SteamFriendsHandler());
            String[] ip = SharedData.getIPAddress().split(":", 2);
            int port = (ip.length > 1) ? Integer.parseInt(ip[1]) : 75564;
            clientHandler = new ClientHandler(ip[0], port, steamFriends.getPersonaName());
            clientHandler.connect();
        } catch (SteamException ex) {
            Log.error("Error loading Steam Username");
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        SteamAPI.runCallbacks();
    }
}
