package io.github.anthonyclemens;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.newdawn.slick.GameContainer;
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

    public float getMainVolume() {
        try{
            return Float.parseFloat(readFromFile("MainVolume"));
        }catch(Exception e){
            return 1f;
        }
    }

    public void setMainVolume(float mainVolume) {
        writeToFile("MainVolume", String.valueOf(mainVolume));
    }

    public void setMusicVolume(float mainVolume) {
        writeToFile("MusicVolume", String.valueOf(mainVolume));
    }

    public float getMusicVolume() {
        try{
            return Float.parseFloat(readFromFile("MusicVolume"));
        }catch(Exception e){
            return 1f;
        }
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private int extractResolutionPart(String resolution, int index) {
        if (resolution != null && resolution.contains("x")) {
            String[] parts = resolution.split("x");
            try {
                return Integer.parseInt(parts[index]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
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

}