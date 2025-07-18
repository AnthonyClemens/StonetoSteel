package io.github.anthonyclemens;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Settings {
    private static Settings instance;
    private static final String SETTINGS_FILE = "settings.xml";

    // Private constructor to prevent instantiation
    private Settings() {}

    // Get the single instance of Settings
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public int getMaxFPS() {
        try{
            return Integer.parseInt(readFromFile("MaxFPS"));
        }catch(Exception e){
            return 60;
        }
    }

    public void setMaxFPS(int maxFPS) {
        writeToFile("MaxFPS", String.valueOf(maxFPS));
    }

    public boolean isVsync() {
        return Boolean.parseBoolean(readFromFile("VSync"));
    }

    public void setVsync(boolean vsync) {
        writeToFile("VSync", String.valueOf(vsync));
    }

    public boolean isFullscreen() {
        return Boolean.parseBoolean(readFromFile("Fullscreen"));
    }

    public void setFullscreen(boolean fullscreen) {
        writeToFile("Fullscreen", String.valueOf(fullscreen));
    }

    public String getResolution() {
        return readFromFile("Resolution");
    }

    public int getWidth(){
        try{
            int width = extractResolutionPart(readFromFile("Resolution"), 0);
            if (width == -1) {
                throw new Exception();
            }
            return width;
        }catch(Exception e){
            return 1024;
        }
    }

    public int getHeight(){
        try{
            int height = extractResolutionPart(readFromFile("Resolution"), 1);
            if (height == -1) {
                throw new Exception();
            }
            return height;
        }catch(Exception e){
            return 768;
        }
    }

    public void setResolution(String resolution) {
        writeToFile("Resolution", resolution);
    }

    public void setMainVolume(float mainVolume) {
        writeToFile("MainVolume", String.valueOf(mainVolume));
    }

    public float getMainVolume() {
        return getVolume("MainVolume");
    }

    public void setMusicVolume(float musicVolume) {
        writeToFile("MusicVolume", String.valueOf(musicVolume));
    }

    public float getMusicVolume() {
        return getVolume("MusicVolume");
    }

    public void setAmbientVolume(float ambientVolume) {
        writeToFile("AmbientVolume", String.valueOf(ambientVolume));
    }

    public float getAmbientVolume() {
        return getVolume("AmbientVolume");
    }

    public void setPlayerVolume(float playerVolume) {
        writeToFile("PlayerVolume", String.valueOf(playerVolume));
    }

    public float getPlayerVolume() {
        return getVolume("PlayerVolume");
    }

    public void setFriendlyVolume(float friendlyVolume) {
        writeToFile("FriendlyVolume", String.valueOf(friendlyVolume));
    }

    public float getFriendlyVolume() {
        return getVolume("FriendlyVolume");
    }

    public void setEnemyVolume(float enemyVolume) {
        writeToFile("EnemyVolume", String.valueOf(enemyVolume));
    }

    public float getEnemyVolume() {
        return getVolume("EnemyVolume");
    }

    public void setTexturePack(String texturePack) {
        writeToFile("texturePack", texturePack);
    }

    public String getTexturePack() {
        return readFromFile("texturePack");
    }

    public void setSoundPack(String soundPack) {
        writeToFile("soundPack", soundPack);
    }

    public String getSoundPack() {
        return readFromFile("soundPack");
    }

    public String getUpKey() {
        return readFromFile("UpKey");
    }

    public void setUpKey(String upKey) {
        writeToFile("UpKey", upKey);
    }

    private String readFromFile(String key) {
        try {
            File file = new File(SETTINGS_FILE);
            if (!file.exists()) {
                return "";
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            Element root = doc.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(key);

            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
        } catch (Exception e) {
            Log.error(e);
        }
        return "";
    }

    private void writeToFile(String key, String value) {
        try {
            File file = new File(SETTINGS_FILE);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (file.exists()) {
                doc = builder.parse(file);
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("Settings");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(key);

            if (nodes.getLength() > 0) {
                nodes.item(0).setTextContent(value);
            } else {
                Element element = doc.createElement(key);
                element.appendChild(doc.createTextNode(value));
                root.appendChild(element);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private int extractResolutionPart(String resolution, int index) {
        if (resolution != null && resolution.contains("x")) {
            String[] parts = resolution.split("x");
            try {
                return Integer.parseInt(parts[index]);
            } catch (NumberFormatException e) {
                Log.error(e);
                return -1;
            }
        }
        return -1;
    }

    public void applyToGameContainer(GameContainer gc) {
        try {
            gc.setTargetFrameRate(getMaxFPS());
            gc.setVSync(isVsync());
            gc.setFullscreen(isFullscreen());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getVolume(String category){
        try{
            return Float.parseFloat(readFromFile(category));
        }catch(Exception e){
            return 1f;
        }
    }

    public void writeDefaultOptions() {
        Settings settings = getInstance();
        settings.setMaxFPS(60);
        settings.setVsync(true);
        settings.setFullscreen(false);
        settings.setResolution("1024x768");
        settings.setTexturePack("texturePacks/default/assets.json");
        settings.setSoundPack("soundPacks/default/assets.json");
        settings.setMainVolume(1f);
        settings.setMusicVolume(1f);
        settings.setAmbientVolume(1f);
        settings.setPlayerVolume(1f);
        settings.setFriendlyVolume(1f);
        settings.setEnemyVolume(1f);
        settings.setUpKey("W");
    }
}