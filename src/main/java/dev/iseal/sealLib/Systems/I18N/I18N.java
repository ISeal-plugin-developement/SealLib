package dev.iseal.sealLib.Systems.I18N;

import dev.iseal.sealUtils.Interfaces.Dumpable;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class I18N {

    private static I18N instance;
    public static I18N getInstance() {
        if (instance == null) {
            instance = new I18N();
        }
        return instance;
    }

    private static final dev.iseal.sealUtils.systems.I18N.I18N utilsI18N = dev.iseal.sealUtils.systems.I18N.I18N.getInstance();

    public void setBundle(JavaPlugin plugin, String localeLang, String localeCountry) throws IOException {
        utilsI18N.setBundle(plugin.getClass(), plugin.getDataFolder(), localeLang, localeCountry);
    }

    @Deprecated(
            forRemoval = true,
            since = "1.1.3.0"
    )
    public static String getTranslation(String key, String... args) {
        return
                ChatColor.translateAlternateColorCodes('&',
                        dev.iseal.sealUtils.systems.I18N.I18N.getTranslation(
                                StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                        .getCallerClass(),
                key, args
                        )
                );
    }

    @Deprecated(
            forRemoval = true,
            since = "1.1.3.0"
    )
    private static String getTranslation(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                dev.iseal.sealUtils.systems.I18N.I18N.getTranslation(
                        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                .getCallerClass(),
                        key
                )
        );
    }

    public static String translate(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                dev.iseal.sealUtils.systems.I18N.I18N.translate(
                        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                .getCallerClass(),
                        key
                )
        );
    }

    public static String translate(String key, String... args) {
        return ChatColor.translateAlternateColorCodes('&',
                dev.iseal.sealUtils.systems.I18N.I18N.translate(
                        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                .getCallerClass(),
                        key, args
                )
        );
    }
}
