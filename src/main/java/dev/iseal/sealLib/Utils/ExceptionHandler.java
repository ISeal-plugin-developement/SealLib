package dev.iseal.sealLib.Utils;

import dev.iseal.sealLib.Interfaces.Dumpable;
import dev.iseal.sealLib.Metrics.MetricsManager;
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
        String[] callingClass = getCallingClassName();
        currentLog.add( "[SealLib] "+"Exception triggered by "+callingClass[0]);
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

        dumpAllClasses();
        MetricsManager.getInstance().sendError(errorMessage, callingClass[1]);
        currentLog.forEach((str) -> log.log(logLevel, str));
    }

    private String[] getCallingClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ExceptionHandler.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                String classPackage = ste.getClass().getPackageName().split("\\.")[2];
                return new String[]{ ste.getClassName(), classPackage };
            }
        }
        return null;
    }

    public void dumpAllClasses() {
        Set<Class<?>> dumpableClasses = new HashSet<>();
        dumpableClasses.addAll(GlobalUtils.findAllClassesInPackage(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass().getPackageName(), Dumpable.class));
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
}
