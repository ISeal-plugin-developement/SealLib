package dev.iseal.sealLib.Commands;

import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Systems.CustomPackets.CustomPacketSender;
import dev.iseal.sealLib.Utils.BlockDisplayUtil;
import dev.iseal.sealLib.Utils.SpigotGlobalUtils;
import dev.iseal.sealLib.Utils.ModelRenderer;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import dev.iseal.sealUtils.utils.GlobalUtils;
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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugCommand implements CommandExecutor {

    private static final Logger log = SealLib.getPlugin().getLogger();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

                    plr.sendMessage("Done in " + (System.currentTimeMillis() - time) + "ms");

                } catch (Exception ex) {
                    ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "SPAWNING_DEBUG_PARTICLES", log);
                }
            });
            case "coneGivenFOV" -> {
                // arg 1: range (like 100 is fine)
                // arg 2: fov (do in game fov+30)
                List<LivingEntity> entities = SpigotGlobalUtils.getEntitiesInCone(plr, Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                entities.forEach(entity ->
                        plr.sendMessage("entity name " + entity.getName() + " id " + entity.getEntityId() + " is in the cone.")
                );
                plr.sendMessage("Done. " + entities.size() + " entities found.");
            }
            case "randomPacket" -> {
                CustomPacketSender.getInstance().sendPacket(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, plr, GlobalUtils.generateRandomString(12), GlobalUtils.generateRandomString(12));
            }
            case "dump" -> {
                // /d dump
                log.info("Dumping classes...");
                ExceptionHandler.getInstance().dumpAllClasses(true);
                log.info("Dumping done.");
            }
            case "fakeException" -> {
                // /d fakeException <optional: level>
                if (args.length < 2) {
                    ExceptionHandler.getInstance().dealWithException(new RuntimeException("This is a fake exception for debugging purposes."), Level.SEVERE, "FAKE_EXCEPTION", log);

                } else {
                    ExceptionHandler.getInstance().dealWithException(new RuntimeException("This is a fake exception for debugging purposes."), Level.parse(args[1]), "FAKE_EXCEPTION", log);
                }
            }
            default -> plr.sendMessage("Unknown debug command.");
        }

        return true;
    }
}