package dev.iseal.sealLib.Interfaces;

import dev.iseal.sealLib.Utils.ExceptionHandler;

import java.util.HashMap;

public interface Dumpable {

    default void dumpableInit() {
        ExceptionHandler.getInstance().registerClass(this.getClass(), this);
    }

    HashMap<String, Object> dump();
}
