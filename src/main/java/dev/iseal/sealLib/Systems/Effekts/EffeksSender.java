package dev.iseal.sealLib.Systems.Effekts;

import dev.iseal.ExtraKryoCodecs.Enums.Effekt;
import dev.iseal.ExtraKryoCodecs.Holders.WorldParticleBuilderHolder;
import dev.iseal.sealLib.Systems.CustomPackets.CustomPacketSender;
import dev.iseal.sealLib.Utils.UnsafeSerializer;
import org.bukkit.entity.Player;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

public class EffeksSender {

    public static void sendScreenshake(ScreenshakeInstance inst, Player plr) {
        byte[] screenshake = UnsafeSerializer.serialize(inst);
        CustomPacketSender.getInstance().sendPacket(
                screenshake,
                plr,
                "sealparticleplayer",
                "effekts",
                Effekt.SCREENSHAKE
        );
    }

    public static void sendParticle(WorldParticleBuilderHolder holder, Player plr) {
        byte[] holderParticle = UnsafeSerializer.serialize(holder);
        CustomPacketSender.getInstance().sendPacket(
                holderParticle,
                plr,
                "sealparticleplayer",
                "effekts",
                Effekt.PARTICLE
        );
    }

}
