package dev.iseal.sealLib.Commands;

import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Systems.CustomPackets.CustomPacketSender;
import dev.iseal.sealLib.Systems.Gui.components.impl.ButtonComponent;
import dev.iseal.sealLib.Systems.Gui.components.impl.StaticComponent;
import dev.iseal.sealLib.Systems.Gui.inventory.impl.ChestGui;
import dev.iseal.sealLib.Systems.Gui.inventory.impl.PagedGui;
import dev.iseal.sealLib.Systems.Gui.patterns.impl.BorderPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.impl.LoopingBorderPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.impl.MarchingAntsBorderPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.impl.SweepingBorderPattern;
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
import org.bukkit.inventory.ItemStack;
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
            case "gui" -> {
                if (args.length < 2) {
                    plr.sendMessage("Usage: /d gui <simple|paged|animated>");
                    return true;
                }
                switch (args[1]) {
                    case "simple" -> {
                        ChestGui gui = new ChestGui(3, "Simple GUI");
                        gui.applyPattern(new BorderPattern(9, 3), new StaticComponent(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
                        gui.setComponent(13, new ButtonComponent(new ItemStack(Material.DIAMOND), (player, clickType) -> {
                            player.sendMessage("You clicked the diamond!");
                            player.closeInventory();
                            return true;
                        }));
                        gui.open(plr);
                    }
                    case "paged" -> {
                        PagedGui pagedGui = new PagedGui(5, "Paged GUI");
                        for (int i = 0; i < 50; i++) {
                            pagedGui.addPageItem(new StaticComponent(new ItemStack(Material.DIRT, i + 1)));
                        }
                        pagedGui.open(plr);
                    }
                    case "animated" -> {
                        ChestGui animatedGui = new ChestGui(6, "Animated Patterns");

                        // Use offset 0 (outer border) for marching ants
                        MarchingAntsBorderPattern marchingPattern = new MarchingAntsBorderPattern(9, 6, 10, 0,
                                new StaticComponent(new ItemStack(Material.RED_STAINED_GLASS_PANE)),
                                new StaticComponent(new ItemStack(Material.BLUE_STAINED_GLASS_PANE))
                        );

                        // Use offset 1 (inner border) for sweeping pattern
                        SweepingBorderPattern sweepingPattern = new SweepingBorderPattern(9, 6, 1, 1);
                        sweepingPattern.setComponents(
                                new StaticComponent(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)),
                                new StaticComponent(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE))
                        );

                        LoopingBorderPattern loopingPattern = new LoopingBorderPattern(9, 6, 5, 2);

                        if (args.length > 2) {
                            switch (args[2]) {
                                case "marching" -> animatedGui.applyPattern(marchingPattern);
                                case "sweeping" -> animatedGui.applyPattern(sweepingPattern);
                                case "looping" -> animatedGui.applyPattern(loopingPattern);
                                case "all" -> {
                                    animatedGui.applyPatterns(marchingPattern, sweepingPattern);
                                    animatedGui.applyPattern(loopingPattern, new StaticComponent(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)));
                                }
                                default -> animatedGui.applyPattern(sweepingPattern);
                            }
                        } else {
                            animatedGui.applyPattern(sweepingPattern);
                        }

                        animatedGui.open(plr);
                    }
                    default -> plr.sendMessage("Unknown gui command: " + args[1]);
                }
            }
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
            case "optimizedRenderer" -> Bukkit.getScheduler().runTaskAsynchronously(SealLib.getPlugin(), () -> {
                long time = System.currentTimeMillis();
                World world = plr.getWorld();
                Location location = new Location(world, Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]));

                try {
                    float precision = Float.parseFloat(args[1]);
                    float scale = Float.parseFloat(args[2]);
                    float blockScale = Float.parseFloat(args[3]);
                    float rotationAngle = Float.parseFloat(args[4]);

                    List<Vector> points = ModelRenderer.getVectors(
                            SealLib.getPlugin().getDataFolder().getAbsolutePath() + "/models/"+args[8]+".obj",
                            scale,
                            rotationAngle,
                            precision
                    );

                    Bukkit.getScheduler().runTask(SealLib.getPlugin(), () ->
                            BlockDisplayUtil.renderOptimizedModel(location, points, Material.STONE, blockScale)
                    );

                    plr.sendMessage("Done in " + (System.currentTimeMillis() - time) + "ms with optimized renderer");

                } catch (Exception ex) {
                    ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "SPAWNING_DEBUG_PARTICLES", log);
                }
            });
            case "lodRenderer" -> Bukkit.getScheduler().runTaskAsynchronously(SealLib.getPlugin(), () -> {
                long time = System.currentTimeMillis();
                World world = plr.getWorld();
                Location location = new Location(world, Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]));
                double maxDistance = args.length > 8 ? Double.parseDouble(args[8]) : 50.0;

                try {
                    float precision = Float.parseFloat(args[1]);
                    float scale = Float.parseFloat(args[2]);
                    float blockScale = Float.parseFloat(args[3]);
                    float rotationAngle = Float.parseFloat(args[4]);

                    List<Vector> points = ModelRenderer.getVectors(
                            SealLib.getPlugin().getDataFolder().getAbsolutePath() + "/models/"+args[9]+".obj",
                            scale,
                            rotationAngle,
                            precision
                    );

                    Bukkit.getScheduler().runTask(SealLib.getPlugin(), () ->
                            BlockDisplayUtil.renderModelWithLOD(location, points, Material.STONE, blockScale, plr, maxDistance)
                    );

                    plr.sendMessage("Done in " + (System.currentTimeMillis() - time) + "ms with LOD renderer");

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
