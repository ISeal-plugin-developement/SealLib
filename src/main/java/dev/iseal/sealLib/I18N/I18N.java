package dev.iseal.sealLib.I18N;

import dev.iseal.sealLib.Interfaces.Dumpable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class I18N implements Dumpable {

    private static I18N instance;
    public static I18N getInstance() {
        if (instance == null) {
            instance = new I18N();
        }
        return instance;
    }

    private static final HashMap<String, ResourceBundle> selectedBundles = new HashMap<>();

    public void setBundle(JavaPlugin plugin, String localeLang, String localeCountry) throws URISyntaxException, IOException {
        PropertyResourceBundle resourceBundle = null;
        Class<?> mainClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        String fileName = "Messages_" + localeLang + "_" + localeCountry + ".properties";
        File dataFolder = plugin.getDataFolder();
        File targetFile = new File(dataFolder, "languages/" + fileName);


        if (targetFile.exists()) {
            // Load the file from the data folder
            PropertyResourceBundle oldResourceBundle = new PropertyResourceBundle(new FileInputStream(targetFile));
            PropertyResourceBundle newResourceBundle;
            try (InputStream resourceStream = mainClass.getResourceAsStream("/languages/" + fileName)) {
                if (resourceStream == null) {
                    throw new FileNotFoundException("Resource file not found: " + fileName);
                }
                newResourceBundle = new PropertyResourceBundle(resourceStream);
            }

            checkAndApplyUpdates(newResourceBundle, oldResourceBundle, targetFile);
        } else {
            // Create directories if they do not exist
            targetFile.getParentFile().mkdirs();

            // Copy the file from resources to the data folder
            try (InputStream resourceStream = mainClass.getResourceAsStream("/languages/" + fileName)) {
                if (resourceStream == null) {
                    throw new FileNotFoundException("Resource file not found: " + fileName);
                }
                Files.copy(resourceStream, targetFile.toPath());
            }
            try (FileInputStream fis = new FileInputStream(targetFile)) {
                resourceBundle = new PropertyResourceBundle(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Bukkit.getLogger().info("Loaded language file: " + fileName + " v" + resourceBundle.getString("BUNDLE_VERSION"));

        selectedBundles.put(mainClass.getPackageName(), resourceBundle);
    }

    private void checkAndApplyUpdates(PropertyResourceBundle newResourceBundle, PropertyResourceBundle oldResourceBundle, File targetFile) {
        try {
            int newVer = Integer.parseInt(newResourceBundle.getString("BUNDLE_VERSION"));
            int oldVer = Integer.parseInt(oldResourceBundle.getString("BUNDLE_VERSION"));
            if (newVer > oldVer) {
                // update bundle
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    newResourceBundle.keySet().forEach(key -> {
                        try {
                            fos.write((key + "=" + newResourceBundle.getString(key) + "\n").getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTranslation(String key) {
        try {
            return selectedBundles
                    .get(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                            .getCallerClass().getPackageName())
                    .getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String translate(String key) {
        return getTranslation(key);
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> dump = new HashMap<>();
        selectedBundles.forEach((key, value) -> {
            HashMap<String, String> bundle = new HashMap<>();
            Enumeration<String> keys = value.getKeys();
            while (keys.hasMoreElements()) {
                String k = keys.nextElement();
                bundle.put(k, value.getString(k));
            }
            dump.put(key, bundle);
        });
        return dump;
    }
}
