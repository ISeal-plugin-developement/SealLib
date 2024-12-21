package dev.iseal.sealLib.Systems.Effekts;

import dev.iseal.sealLib.Systems.CustomPackets.CustomPacketSender;
import dev.iseal.sealLib.Utils.UnsafeSerializer;
import org.bukkit.entity.Player;

public class EffektsSender {

    public void sendEffect(Object effect, Effekt effektEnum, Player player) {
        if (!effektEnum.getEffectClass().isInstance(effect)) {
            return;
        }
        byte[] serializedObject = UnsafeSerializer.serialize(effect);
        CustomPacketSender.getInstance().sendPacket(serializedObject, player,"sealparticleplayer", "effekts", effektEnum);
    }

}
