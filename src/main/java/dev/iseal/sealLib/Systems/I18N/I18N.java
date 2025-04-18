package dev.iseal.sealLib.Systems.I18N;

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
import java.util.logging.Logger;

import static dev.iseal.sealLib.SealLib.isDebug;

public class I18N implements Dumpable {

    private static I18N instance;
    public static I18N getInstance() {
        if (instance == null) {
            instance = new I18N();
        }
        return instance;
    }

    public I18N() {
        dumpableInit();
    }

    private static final HashMap<String, ResourceBundle> selectedBundles = new HashMap<>();

    private void unpackAllLanguages(JavaPlugin plugin) throws URISyntaxException {
        Logger logger = Bukkit.getLogger();
        ResourceWalker.getInstance().walk(plugin.getClass(), "languages", (inputStream, fileName) -> {
            File dataFolder = plugin.getDataFolder();
            File targetFile = new File(dataFolder, "languages/" + fileName);
            if (isDebug())
                logger.info("[SealLib] Processing language file: " + fileName);

            if (targetFile.exists()) {
                if (isDebug())
                    logger.info("[SealLib] Target file exists, checking for updates");
                PropertyResourceBundle oldResourceBundle;
                try (InputStreamReader reader = new InputStreamReader(new FileInputStream(targetFile), StandardCharsets.UTF_8)) {
                    oldResourceBundle = new PropertyResourceBundle(reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                PropertyResourceBundle newResourceBundle;
                try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    newResourceBundle = new PropertyResourceBundle(reader);
                } catch (Exception e) {
                    logger.severe("[SealLib] Exception while loading resource file: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                checkAndApplyUpdates(newResourceBundle, oldResourceBundle, targetFile);
            } else {
                if (isDebug())
                    logger.info("[SealLib] Target file for "+fileName+" does not exist, creating directories and new file");
                try {
                    targetFile.getParentFile().mkdirs();
                    targetFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (isDebug())
                    logger.info("[SealLib] Copying file from resources to data folder");
                try {
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (isDebug())
                    logger.info("[SealLib] File copied successfully");
            }
        });
    }

    public void setBundle(JavaPlugin plugin, String localeLang, String localeCountry) throws IOException {
        Logger logger = Bukkit.getLogger();
        PropertyResourceBundle resourceBundle = null;

        // unpack all languages and check updates
        try {
            unpackAllLanguages(plugin);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // get caller class
        Class<?> mainClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        String fileName = "Messages_" + localeLang + "_" + localeCountry + ".properties";
        if (isDebug())
            logger.info("[SealLib] File name constructed: " + fileName);

        // make file object
        File dataFolder = plugin.getDataFolder();
        File targetFile = new File(dataFolder, "languages/" + fileName);

        if (!targetFile.exists()) {
            logger.severe("[SealLib] Target file does not exist, loading default en_US file");
            fileName = "Messages_en_US.properties";
            targetFile = new File(dataFolder, "languages/" + fileName);
        }

        logger.info("[SealLib] Target file path: " + targetFile.getAbsolutePath());

        resourceBundle = new PropertyResourceBundle(new FileInputStream(targetFile));
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

    public static String getTranslation(String key, String... args) {
        return translate(key, args);
    }

    private static String getTranslation(String key) {
        return translate(key, new String[0]);
    }

    public static String translate(String key) {
        return translate(key, new String[0]);
    }

    public static String translate(String key, String... args) {
        try {
            String translation = selectedBundles.get(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                            .getCallerClass().getPackageName().split("\\.")[2])
                    .getString(key);

            for (int i = 0; i < args.length; i++) {
                translation = translation.replace("{" + i + "}", args[i] != null ? args[i] : "null");
            }

            return ChatColor.translateAlternateColorCodes('&', translation);
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
