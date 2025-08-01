package io.github.anthonyclemens;

import org.newdawn.slick.util.Log;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriends.PersonaChange;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;

public class SteamFriendsHandler implements SteamFriendsCallback {

    private SteamFriends steamFriends;

    public SteamFriendsHandler() throws SteamException {
        steamFriends = new SteamFriends(this);
    }

    public SteamFriends getSteamFriends() {
        return steamFriends;
    }

    public String getUsername() {
        return steamFriends.getPersonaName();
    }

    @Override
    public void onGameOverlayActivated(boolean active) {
        System.out.println("Steam overlay " + (active ? "activated" : "deactivated"));
    }

    @Override
    public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {
        System.out.println("Lobby join requested from Steam ID " + steamIDFriend + " to lobby " + steamIDLobby);
    }

    @Override
    public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height) {
        System.out.println("Avatar loaded for Steam ID " + steamID + " - Image ID: " + image + ", Size: " + width + "x" + height);
    }

    @Override
    public void onFriendRichPresenceUpdate(SteamID steamID, int appID) {
        System.out.println("Rich presence updated for Steam ID " + steamID + " in app " + appID);
    }

    @Override
    public void onGameRichPresenceJoinRequested(SteamID steamID, String connect) {
        Log.debug("Rich presence join requested:");
        Log.debug(" - Steam ID: " + steamID);
        Log.debug(" - Connect string: " + connect);
    }

    @Override
    public void onGameServerChangeRequested(String server, String password) {
        Log.debug("Game server change requested:");
        Log.debug(" - Server address: " + server);
        Log.debug(" - Server password: " + password);
    }

    @Override
    public void onPersonaStateChange(SteamID steamID, PersonaChange change) {
        Log.debug("Persona state change detected:");
        steamFriends.requestUserInformation(steamID, true);
        Log.debug(" - Steam ID: " + steamFriends.getFriendPersonaName(steamID));
        Log.debug(" - Change type: " + change);
    }

    @Override
    public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result) {
        Log.debug("Set persona name response received:");
        Log.debug(" - Success: " + success);
        Log.debug(" - Local success: " + localSuccess);
        Log.debug(" - Result: " + result);
    }
}
