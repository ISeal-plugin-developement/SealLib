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

    public static ExceptionHandler getInstance() {
        if (instance == null)
            instance = new ExceptionHandler();
        return instance;
    }

    public void dealWithException(Exception ex, Level logLevel, String errorMessage, Object... moreInfo){
        currentLog = new ArrayList<>();
        Class<?> mainClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        currentLog.add( "[SealLib] "+"Exception triggered by "+mainClass.getPackageName());
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

        if (logLevel == Level.SEVERE)
            MetricsManager.getInstance().sendError(errorMessage, mainClass.getPackageName().split("\\.")[2]);
        currentLog.forEach((str) -> log.log(logLevel, str));
    }

    public void dumpAllClasses(Class<?> caller) {
        if (caller == null) {
            caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        }

        Set<Class<?>> dumpableClasses = new HashSet<>();
        dumpableClasses.addAll(GlobalUtils.findAllClassesInPackage(caller.getPackageName(), Dumpable.class));
        HashMap<String, HashMap<String, Object>> dumpMap = new HashMap<>();
        dumpableClasses.forEach(clazz -> {
            try {
                if (clazz.equals(Dumpable.class)) return;
                // check if class is singleton
                if (clazz.getDeclaredMethods().length == 0) return;
                AtomicBoolean done = new AtomicBoolean(false);

                Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getName().equals("getInstance")).findFirst().ifPresent(getInstance -> {
                    try {
                        Object instance = getInstance.invoke(null);
                        Method dump = clazz.getDeclaredMethod("dump");
                        dumpMap.put(clazz.getSimpleName(), (HashMap<String, Object>) dump.invoke(instance));
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        currentLog.add("[SealLib] " + "Error while trying to instantiate and dump class " + clazz.getSimpleName());
                    } catch (Exception e) {
                        currentLog.add("[SealLib] " + "Error while trying to dump class " + clazz.getSimpleName());
                    }
                    done.set(true);
                });
                if (done.get()) return;
                // Last resort, create new instance and dump. 99% of the time this will fail, someone screwed up Dumpable implementation real bad
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method dump = clazz.getDeclaredMethod("dump");
                    dumpMap.put(clazz.getSimpleName(), (HashMap<String, Object>) dump.invoke(instance));
                    done.set(true);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException | SecurityException | IllegalArgumentException e1) {
                    currentLog.add("[SealLib] " + "Error while trying to instantiate and dump class " + clazz.getSimpleName());
                } catch (Exception e) {
                    currentLog.add("[SealLib] " + "Error while trying to dump class " + clazz.getSimpleName());
                }
                if (done.get()) return;
            } catch (Exception e) {
                currentLog.add("[SealLib] " + "Error while trying to dump class " + clazz.getSimpleName());
            }
        });
        dumpMap.forEach((className, dumpMapTemp) -> {
            dumpMapTemp.forEach((toDump, dumpValue) -> {
                currentLog.add( "[SealLib] Dump from: "+className+" -> "+toDump+": "+dumpValue.toString());
            });
        });
    }

    private void attemptToDealWithCustomException(Exception ex) {
        if (ex instanceof SecurityException se) {
            currentLog.add("[SealLib] SecurityException caught, what?");
        }
    }

}
