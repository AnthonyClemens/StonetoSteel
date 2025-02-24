package io.github.anthonyclemens;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Utils {

    public static void loadSettings(Settings settings){
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("settings.xml");
            if (inputStream == null) {
                System.err.println("XML file not found in resources.");
                return;
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
    }

}
