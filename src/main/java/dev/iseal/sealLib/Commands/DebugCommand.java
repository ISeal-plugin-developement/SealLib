package dev.iseal.sealLib.Commands;

import de.leonhard.storage.shaded.jetbrains.annotations.NotNull;
import dev.iseal.ExtraKryoCodecs.Holders.ScreenFlashHolder;
import dev.iseal.ExtraKryoCodecs.Holders.WorldParticleBuilderHolder;
import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Systems.CustomPackets.CustomPacketSender;
import dev.iseal.sealLib.Systems.Effekts.EffeksSender;
import dev.iseal.sealLib.Utils.BlockDisplayUtil;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import dev.iseal.sealLib.Utils.GlobalUtils;
import dev.iseal.sealLib.Utils.ModelRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.util.List;
import java.util.logging.Level;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player plr)) {
            sender.sendMessage("Please be a player.");
            return true;
        }

        switch (args[0]) {
            case "renderer" -> Bukkit.getScheduler().runTaskAsynchronously(SealLib.getPlugin(), () -> {
                long time = System.currentTimeMillis();
                World world = plr.getWorld();
                Location location = new Location(world, Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]));

                try {
                    float precision = Float.parseFloat(args[1]);
                    float scale = Float.parseFloat(args[2]);
                    float blockScale = Float.parseFloat(args[3]);
                    float rotationAngle = Float.parseFloat(args[4]);

                    List<Vector> points = ModelRenderer.getVectors(
                            SealLib.getPlugin().getDataFolder().getAbsolutePath() + "/models/test.obj",
                            scale,
                            rotationAngle,
                            precision
                    );

                    Bukkit.getScheduler().runTask(SealLib.getPlugin(), () ->
                            BlockDisplayUtil.renderModel(location, points, Material.STONE, blockScale)
                    );

                    plr.sendMessage("Done in " + ((Long) (System.currentTimeMillis() - time)) + "ms");

                } catch (Exception ex) {
                    ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "SPAWNING_DEBUG_PARTICLES");
                }

            });
            case "coneGivenFOV" -> {
                // arg 1: range (like 100 is fine)
                // arg 2: fov (do in game fov+30)
                List<LivingEntity> entities = GlobalUtils.getEntitiesInCone(plr, Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                entities.forEach(entity ->
                        plr.sendMessage("entity name " + entity.getName() + " id " + entity.getEntityId() + " is in the cone.")
                );
                plr.sendMessage("Done. " + entities.size() + " entities found.");
            }
            case "screenshake" -> {
                // /d screenshake 20 0.3 linear
                EffeksSender.sendScreenshake(
                        new ScreenshakeInstance(Integer.valueOf(args[1]))
                                .setIntensity(Float.valueOf(args[2]))
                                .setEasing(Easing.valueOf(args[3])),
                        plr
                );
            }
            case "particle" -> {
                EffeksSender.sendParticle(
                        new WorldParticleBuilderHolder(1)
                                .setLifetime(1000)
                                .setLocation(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]))
                                .setScaleData(GenericParticleData.create(20f).build())
                                .setColorData(ColorParticleData.create(255, 0, 0).build())
                                .disableCull()
                                .enableForcedSpawn()
                                .enableNoClip(),
                    plr);
            }
            case "screenFlash" -> {
                EffeksSender.sendScreenflash(
                        new ScreenFlashHolder(
                                Integer.parseInt(args[1]), Float.parseFloat(args[2]),
                                255,255,255
                        ),
                        plr
                );
            }
            case "randomPacket" -> {
                CustomPacketSender.getInstance().sendPacket(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, plr, GlobalUtils.generateRandomString(12), GlobalUtils.generateRandomString(12));
            }
        }

        return true;
    }
}