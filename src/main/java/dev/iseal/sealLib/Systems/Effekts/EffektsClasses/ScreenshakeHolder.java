package dev.iseal.sealLib.Systems.Effekts.EffektsClasses;

import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.io.Serializable;

public class ScreenshakeHolder implements Serializable {
    //public Easing easing;
    public Object easing;

    public ScreenshakeHolder(ScreenshakeInstance easing) {
        this.easing = easing;
    }

    public ScreenshakeHolder() {
        easing = Easing.LINEAR;
    }
}
