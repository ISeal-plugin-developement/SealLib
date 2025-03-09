package dev.iseal.sealLib.Utils;

import dev.iseal.sealLib.Interfaces.Dumpable;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.SealLib;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {

    private static ExceptionHandler instance;
    private final Logger log = Bukkit.getLogger();
    private ArrayList<String> currentLog = new ArrayList<>();

    // class, instance
    private final HashMap<Class<? extends Dumpable>, Dumpable> registeredClasses = new HashMap<>();

    public static ExceptionHandler getInstance() {
        if (instance == null)
            instance = new ExceptionHandler();
        return instance;
    }

    public void dealWithException(Exception ex, Level logLevel, String errorMessage, Object... moreInfo){
        currentLog = new ArrayList<>();
        Class<?> mainClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        currentLog.add( "[SealLib] "+"Exception triggered by "+mainClass.getName());
        currentLog.add( "[SealLib] "+"The exception message is "+ex.getMessage());
        currentLog.add( "[SealLib] "+"The error message is "+errorMessage);
        currentLog.add("[SealLib] "+"The stacktrace and all of its details known are as follows: ");
        for (StackTraceElement stackTraceElement : ex.getStackTrace())
            currentLog.add( "[SealLib] "+stackTraceElement.toString());

        currentLog.add( "[SealLib] "+"More details (make sure to tell these to the developer): ");
        int i = 1;
        for (Object obj : moreInfo) {
            currentLog.add( "[SealLib] More info "+i+": "+obj.toString());
            i++;
        }

        attemptToDealWithCustomException(ex);

        if (SealLib.isDebug())
            dumpAllClasses(mainClass);
        currentLog.forEach((str) -> log.log(logLevel, str));
    }

    public void dumpAllClasses(Class<?> caller) {
        if (caller == null) {
            caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        }

        HashMap<String, HashMap<String, Object>> dumpMap = new HashMap<>();
        registeredClasses.forEach((clazz, dumpable) -> {
            dumpMap.put(clazz.getSimpleName(), dumpable.dump());
        });

        dumpMap.forEach((className, dumpMapTemp) -> {
            dumpMapTemp.forEach((toDump, dumpValue) -> {
                if (dumpValue == null)
                    currentLog.add( "[SealLib] Dump from: "+className+" -> "+toDump+": null - something is wrong.");
                else
                    currentLog.add( "[SealLib] Dump from: "+className+" -> "+toDump+": "+dumpValue.toString());
            });
        });
    }

    private void attemptToDealWithCustomException(Exception ex) {
        if (ex instanceof SecurityException se) {
            currentLog.add("[SealLib] SecurityException caught, what?");
        }
    }

    public void registerClass(Class<? extends Dumpable> clazz, Dumpable instance) {
        registeredClasses.put(clazz, instance);
    }

}
