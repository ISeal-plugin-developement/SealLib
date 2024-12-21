package dev.iseal.sealLib.Systems.Effekts;

import com.esotericsoftware.kryo.kryo5.Serializer;
import dev.iseal.sealLib.Systems.Effekts.Serializers.ScreenshakeSerializer;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

public enum Effekt {

    SCREENSHAKE(100, ScreenshakeInstance.class, new ScreenshakeSerializer()),
    PARTICLE(101, WorldParticleBuilder.class, null);

    public static Effekt fromID(int effectID) {
        for (Effekt effekt : values()) {
            if (effekt.getID() != effectID) {
                continue;
            }
            return effekt;
        }
        return SCREENSHAKE;
    }

    private final int effectID;
    private final Class<?> effectClass;
    private final Serializer<?> serializer;

    Effekt(int effectID, Class<?> effectClass, Serializer<?> serializer) {
        this.effectID = effectID;
        this.effectClass = effectClass;
        this.serializer = serializer;
    }

    public int getID() {
        return effectID;
    }
    public Class<?> getEffectClass() {
        return effectClass;
    }
    public Serializer<?> getSerializer() {
        return serializer;
    }
}