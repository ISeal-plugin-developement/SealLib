package dev.iseal.sealLib.Commands;

import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Utils.BlockDisplayUtil;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import dev.iseal.sealLib.Utils.ModelRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Please be a player.");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(SealLib.getPlugin(), () -> {
            long time = System.currentTimeMillis();
            Player player = (Player) sender;
            World world = player.getWorld();
            Location location = new Location(world, Float.parseFloat(args[4]), Float.parseFloat(args[5]), Float.parseFloat(args[6]));

            try {
                float precision = Float.parseFloat(args[0]);
                float scale = Float.parseFloat(args[1]);
                float blockScale = Float.parseFloat(args[2]);
                float rotationAngle = Float.parseFloat(args[3]);

                List<Vector> points = ModelRenderer.getVectors(
                        SealLib.getPlugin().getDataFolder().getAbsolutePath()+"/models/test.obj",
                        scale,
                        rotationAngle,
                        precision
                );

                Bukkit.getScheduler().runTask(SealLib.getPlugin(), () ->
                    BlockDisplayUtil.renderModel(location, points, Material.STONE, blockScale)
                );

                player.sendMessage("Done in " + ((Long) (System.currentTimeMillis() - time)).toString() + "ms");

            } catch (Exception ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "SPAWNING_DEBUG_PARTICLES");
            }

        });

        return true;
    }
}