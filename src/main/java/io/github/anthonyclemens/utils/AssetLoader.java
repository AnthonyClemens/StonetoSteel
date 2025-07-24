package io.github.anthonyclemens.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AssetLoader {

    public AssetLoader(){}

    public static List<String> loadListFromFile(String fileName, String keyName) {
        List<String> result = new ArrayList<>();
        String prefix = fileName.replace("/assets.json", "") + "/";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean insideArray = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Look for the key
                if (line.startsWith("\"" + keyName + "\"")) {
                    insideArray = true;
                    continue;
                }

                // Stop when array ends
                if (insideArray && line.contains("]")) {
                    break;
                }

                if (insideArray) {
                    // Strip quotes, commas, and whitespace
                    line = line.replaceAll("[\",]", "").trim();
                    if (!line.isEmpty()) {
                        result.add(prefix + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String loadSingleAssetFromFile(String fileName, String keyName) {
        String prefix = fileName.replace("/assets.json", "") + "/";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean insideArray = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"" + keyName + "\"")) {
                    insideArray = true;
                    continue;
                }

                if (insideArray) {
                    if (line.contains("]")) break;

                    line = line.replaceAll("[\",]", "").trim();
                    if (!line.isEmpty()) {
                        return prefix + line;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

