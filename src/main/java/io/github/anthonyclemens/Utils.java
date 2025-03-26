package io.github.anthonyclemens;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Utils {

    public static TrueTypeFont getFont(String location, float fontSize) {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream(location);

            // Create AWT Font
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont(fontSize); // Set font size
            return new TrueTypeFont(awtFont, true);
        } catch (FontFormatException | IOException e) {
            Log.error(e);
        }
        return null;
    }

    public static Settings loadSettings(Settings settings){
        try {
            // Specify the file path relative to the current directory
            File file = new File(System.getProperty("user.dir"),"settings.xml");
            InputStream inputStream = null;
            if (!file.exists()) {
                ClassLoader classLoader = Main.class.getClassLoader();
                inputStream = classLoader.getResourceAsStream("settings.xml");
            }else{
                inputStream = new FileInputStream(file);
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();

            // Load video settings
            Element video = (Element) root.getElementsByTagName("video").item(0);
            settings.setMaxFPS(Integer.parseInt(video.getElementsByTagName("maxfps").item(0).getTextContent()));
            settings.setVsync(Boolean.parseBoolean(video.getElementsByTagName("vsync").item(0).getTextContent()));
            settings.setFullscreen(Boolean.parseBoolean(video.getElementsByTagName("fullscreen").item(0).getTextContent()));
            settings.setResolution(video.getElementsByTagName("resolution").item(0).getTextContent());
            settings.setShowStats(Boolean.parseBoolean(video.getElementsByTagName("showstats").item(0).getTextContent()));

            // Load sound settings
            Element sound = (Element) root.getElementsByTagName("sound").item(0);
            settings.setMainVolume(Float.parseFloat(sound.getElementsByTagName("main").item(0).getTextContent()));
            settings.setMusicVolume(Float.parseFloat(sound.getElementsByTagName("music").item(0).getTextContent()));
            settings.setAmbientVolume(Float.parseFloat(sound.getElementsByTagName("ambient").item(0).getTextContent()));
            settings.setPlayerVolume(Float.parseFloat(sound.getElementsByTagName("player").item(0).getTextContent()));
            settings.setEnemyVolume(Float.parseFloat(sound.getElementsByTagName("enemy").item(0).getTextContent()));

            // Load controls settings
            Element controls = (Element) root.getElementsByTagName("controls").item(0);
            settings.setUpKey(controls.getElementsByTagName("up").item(0).getTextContent());
            settings.setDownKey(controls.getElementsByTagName("down").item(0).getTextContent());
            settings.setLeftKey(controls.getElementsByTagName("left").item(0).getTextContent());
            settings.setRightKey(controls.getElementsByTagName("right").item(0).getTextContent());

            inputStream.close();
        } catch (SAXException e){
            System.err.println("SAX Error");
        } catch (ParserConfigurationException e){
            System.err.println("Failed to parse XML file");
        } catch (IOException e){
            System.err.println("Failed to open file");
        }
        return settings;
    }

    public static void saveSettings(Settings settings) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Create root element
            Element root = doc.createElement("settings");
            doc.appendChild(root);

            // Video settings
            Element video = doc.createElement("video");
            root.appendChild(video);

            Element maxFPS = doc.createElement("maxfps");
            maxFPS.setTextContent(String.valueOf(settings.getMaxFPS()));
            video.appendChild(maxFPS);

            Element vsync = doc.createElement("vsync");
            vsync.setTextContent(String.valueOf(settings.isVsync()));
            video.appendChild(vsync);

            Element fullscreen = doc.createElement("fullscreen");
            fullscreen.setTextContent(String.valueOf(settings.isFullscreen()));
            video.appendChild(fullscreen);

            Element resolution = doc.createElement("resolution");
            resolution.setTextContent(settings.getResolution());
            video.appendChild(resolution);

            Element showStats = doc.createElement("showstats");
            showStats.setTextContent(String.valueOf(settings.isShowStats()));
            video.appendChild(showStats);

            // Sound settings
            Element sound = doc.createElement("sound");
            root.appendChild(sound);

            Element mainVolume = doc.createElement("main");
            mainVolume.setTextContent(String.valueOf(settings.getMainVolume()));
            sound.appendChild(mainVolume);

            Element musicVolume = doc.createElement("music");
            musicVolume.setTextContent(String.valueOf(settings.getMusicVolume()));
            sound.appendChild(musicVolume);

            Element ambientVolume = doc.createElement("ambient");
            ambientVolume.setTextContent(String.valueOf(settings.getAmbientVolume()));
            sound.appendChild(ambientVolume);

            Element playerVolume = doc.createElement("player");
            playerVolume.setTextContent(String.valueOf(settings.getPlayerVolume()));
            sound.appendChild(playerVolume);

            Element enemyVolume = doc.createElement("enemy");
            enemyVolume.setTextContent(String.valueOf(settings.getEnemyVolume()));
            sound.appendChild(enemyVolume);

            // Controls settings
            Element controls = doc.createElement("controls");
            root.appendChild(controls);

            Element upKey = doc.createElement("up");
            upKey.setTextContent(settings.getUpKey());
            controls.appendChild(upKey);

            Element downKey = doc.createElement("down");
            downKey.setTextContent(settings.getDownKey());
            controls.appendChild(downKey);

            Element leftKey = doc.createElement("left");
            leftKey.setTextContent(settings.getLeftKey());
            controls.appendChild(leftKey);

            Element rightKey = doc.createElement("right");
            rightKey.setTextContent(settings.getRightKey());
            controls.appendChild(rightKey);

            // Write to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult("settings.xml");
            transformer.transform(source, result);

            Log.info("Settings saved successfully.");
        } catch (ParserConfigurationException e) {
            System.err.println("Failed to create XML document");
        } catch (TransformerException e) {
            System.err.println("Failed to write XML file");
        }
    }

}
