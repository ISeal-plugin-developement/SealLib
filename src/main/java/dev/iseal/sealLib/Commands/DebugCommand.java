package dev.iseal.sealLib.Commands;

import de.leonhard.storage.shaded.jetbrains.annotations.NotNull;
import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Systems.CustomPackets.CustomPacketSender;
import dev.iseal.sealLib.Systems.Effekts.Effekt;
import dev.iseal.sealLib.Systems.Effekts.EffektsSender;
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

                    plr.sendMessage("Done in " + ((Long) (System.currentTimeMillis() - time)).toString() + "ms");

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
                //CustomPacketSender.getInstance().sendPacket(new ScreenshakeHolder(new ScreenshakeInstance(40)), plr, "sealparticleplayer", "effekts", Effekt.SCREENSHAKE);
                new EffektsSender().sendEffect(new ScreenshakeInstance(40), Effekt.SCREENSHAKE, plr);
            }
            case "particle" -> {
                    // /d particle 1 0.3 0.5 0.1 0.9 255 0 0 0 0 0 0.5 linear 0.3 1 linear 100 0.2 0.2 0.2 true 0.6 0 0 0
                /*
                CustomPacketSender.getInstance().sendPacket(new ParticleEffectBuilder(
                        Integer.parseInt(args[1]), // particleID
                        Float.parseFloat(args[2]), // initialScale
                        Float.parseFloat(args[3]), // finalScale
                        Float.parseFloat(args[4]), // initialTransparency
                        Float.parseFloat(args[5]), // finalTransparency
                        new java.awt.Color(Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8])), // initialColor
                        new java.awt.Color(Integer.parseInt(args[9]), Integer.parseInt(args[10]), Integer.parseInt(args[11])), // finalColor
                        Float.parseFloat(args[12]), // colorCoefficient
                        args[13], // colorEasingName
                        Float.parseFloat(args[14]), // spinMin
                        Float.parseFloat(args[15]), // spinMax
                        args[16], // spinEasingName
                        Integer.parseInt(args[17]), // lifetime
                        Float.parseFloat(args[18]), // motionX
                        Float.parseFloat(args[19]), // motionY
                        Float.parseFloat(args[20]), // motionZ
                        Boolean.parseBoolean(args[21]), // noClip
                        Float.parseFloat(args[22]), // randomMotion
                        Float.parseFloat(args[23]), // posX
                        Float.parseFloat(args[24]), // posY
                        Float.parseFloat(args[25])  // posZ
                ).toByteArray()
                        , plr, "hadesfightparticleplayer", "test_packet");
                 */
            }
            case "randomPacket" -> {
                CustomPacketSender.getInstance().sendPacket(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, plr, GlobalUtils.generateRandomString(12), GlobalUtils.generateRandomString(12));
            }
        }

        return true;
    }
}