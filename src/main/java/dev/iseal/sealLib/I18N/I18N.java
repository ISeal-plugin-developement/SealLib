package dev.iseal.sealLib.I18N;

import dev.iseal.sealLib.Interfaces.Dumpable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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
        AtomicReference<PropertyResourceBundle> resourceBundle = new AtomicReference<>();
        Class<?> mainClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        try (FileInputStream fis = new FileInputStream(plugin.getDataFolder() + "/languages/Messages_"+localeLang+"_"+localeCountry+".properties")) {
            resourceBundle.set(new PropertyResourceBundle(fis));
        } catch (FileNotFoundException e) {
            ResourceWalker.walk(mainClass, "/languages", path -> {
                if (path.toString().contains("Messages_"+localeLang+"_"+localeCountry+".properties")) {
                    try (FileInputStream fis = new FileInputStream(path.toString())) {
                        resourceBundle.set(new PropertyResourceBundle(fis));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        selectedBundles.put(mainClass.getPackageName(), resourceBundle.get());
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
