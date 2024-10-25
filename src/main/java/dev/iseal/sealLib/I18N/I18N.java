package dev.iseal.sealLib.I18N;

import dev.iseal.sealLib.Interfaces.Dumpable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class I18N implements Dumpable {

    private static I18N instance;
    public static I18N getInstance() {
        if (instance == null) {
            instance = new I18N();
        }
        return instance;
    }

    private static final HashMap<String, ResourceBundle> selectedBundles = new HashMap<>();

    public void setBundle(JavaPlugin plugin, String localeLang, String localeCountry) throws IOException {
        Logger logger = Bukkit.getLogger();
        PropertyResourceBundle resourceBundle = null;
        Class<?> mainClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        String fileName = "Messages_" + localeLang + "_" + localeCountry + ".properties";
        logger.info("[SealLib] File name constructed: " + fileName);
        File dataFolder = plugin.getDataFolder();
        File targetFile = new File(dataFolder, "languages/" + fileName);
        logger.info("[SealLib] Target file path: " + targetFile.getAbsolutePath());

        if (targetFile.exists()) {
            logger.info("[SealLib] Target file exists, loading from data folder");
            PropertyResourceBundle oldResourceBundle;
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(targetFile), StandardCharsets.UTF_8)) {
                oldResourceBundle = new PropertyResourceBundle(reader);
            }
            PropertyResourceBundle newResourceBundle;
            try (InputStream resourceStream = mainClass.getResourceAsStream("/languages/" + fileName);
                 InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8)) {
                if (resourceStream == null) {
                    logger.severe("[SealLib] Resource file not found: " + fileName);
                    throw new FileNotFoundException("Resource file not found: " + fileName);
                }
                newResourceBundle = new PropertyResourceBundle(reader);
            } catch (Exception e) {
                logger.severe("[SealLib] Exception while loading resource file: " + e.getMessage());
                throw new RuntimeException(e);
            }

            logger.info("[SealLib] Checking and applying updates if necessary");
            checkAndApplyUpdates(newResourceBundle, oldResourceBundle, targetFile);
            resourceBundle = new PropertyResourceBundle(new FileInputStream(targetFile));
        } else {
            logger.info("[SealLib] Target file does not exist, creating directories and new file");
            targetFile.getParentFile().mkdirs();
            targetFile.createNewFile();

            logger.info("[SealLib] Copying file from resources to data folder");
            try (InputStream resourceStream = mainClass.getResourceAsStream("/languages/" + fileName)) {
                if (resourceStream == null) {
                    logger.severe("[SealLib] Resource file not found: " + fileName);
                    throw new FileNotFoundException("Resource file not found: " + fileName);
                }
                Files.copy(resourceStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            logger.info("[SealLib] Loading resource bundle from new file");
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(targetFile), StandardCharsets.UTF_8)) {
                resourceBundle = new PropertyResourceBundle(reader);
            } catch (FileNotFoundException e) {
                logger.severe("[SealLib] File not found: " + targetFile.getAbsolutePath());
                throw new RuntimeException(e);
            }
        }

        logger.info("[SealLib] Loaded language file: " + fileName + " v" + resourceBundle.getString("BUNDLE_VERSION"));
        selectedBundles.put(mainClass.getPackageName().split("\\.")[2], resourceBundle);
    }

    private void checkAndApplyUpdates(PropertyResourceBundle newResourceBundle, PropertyResourceBundle oldResourceBundle, File targetFile) {
        try {
            int newVer = Integer.parseInt(newResourceBundle.getString("BUNDLE_VERSION"));
            int oldVer = Integer.parseInt(oldResourceBundle.getString("BUNDLE_VERSION"));
            if (newVer > oldVer) {
                // update bundle
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8)) {
                    newResourceBundle.keySet().forEach(key -> {
                        try {
                            writer.write(key + "=" + newResourceBundle.getString(key) + "\n");
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
            return ChatColor.translateAlternateColorCodes('&',
                    selectedBundles.get(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                            .getCallerClass().getPackageName().split("\\.")[2])
                    .getString(key));
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String translate(String key) {
        try {
            return ChatColor.translateAlternateColorCodes('&',
                    selectedBundles.get(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                            .getCallerClass().getPackageName().split("\\.")[2])
                    .getString(key));
        } catch (MissingResourceException | NullPointerException e) {
            return key;
        }
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
