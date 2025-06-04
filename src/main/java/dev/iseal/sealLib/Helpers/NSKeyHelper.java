package dev.iseal.sealLib.Helpers;

import dev.iseal.sealLib.SealLib;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NSKeyHelper {

    private final static HashMap<String, NamespacedKey> CACHE = new HashMap<>();
    private final static Logger log = SealLib.getPlugin().getLogger();

    /*
        * Get a NamespacedKey from the cache or create a new one.
        * @param key The key to get.
        * @return The NamespacedKey.
        *
        * WARNING: getting a key while it is not in cache (has not been created before) is dangerous and should be avoided.
        * Please make sure to create the key before getting it using getKey(JavaPlugin, String).
     */
    public static NamespacedKey getKey(String key) {
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        ExceptionHandler.getInstance().dealWithException(
                new RuntimeException("NSKey not found in cache! Making new key with ns SealLib. This is dangerous!"),
                Level.WARNING,
                "NSKEY_NOT_IN_CACHE",
                log,
                key
        );
        NamespacedKey nsKey = new NamespacedKey(SealLib.getPlugin(), key);
        CACHE.put(key, nsKey);
        return nsKey;
    }

    /*
        * Get a NamespacedKey from the cache or create a new one.
        * @param plugin The plugin to use for the NamespacedKey.
        * @param key The key to get.
        * @return The NamespacedKey.
        *
        * plugin will not be used if key is already in cache. though if you are sure it is in cache use getKey(String) instead.
     */
    public static NamespacedKey getKey(JavaPlugin plugin, String key) {
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        NamespacedKey nsKey = new NamespacedKey(plugin, key);
        CACHE.put(key, nsKey);
        return nsKey;
    }

}
