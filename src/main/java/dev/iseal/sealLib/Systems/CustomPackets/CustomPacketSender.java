package dev.iseal.sealLib.Systems.CustomPackets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.esotericsoftware.kryo.Kryo;
import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Systems.CustomPackets.Packets.WrapperPlayServerCustomPayload;
import dev.iseal.sealUtils.systems.serializer.UnsafeSerializer;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.K;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomPacketSender {

    private static CustomPacketSender instance;
    public static CustomPacketSender getInstance() {
        if (instance == null) {
            instance = new CustomPacketSender();
        }
        return instance;
    }

    private final ProtocolManager protocolManager;
    private static final Logger log = SealLib.getPlugin().getLogger();
    private static final Kryo kryo = new Kryo();

    protected CustomPacketSender() {
        if (!SealLib.isDependencyLoaded("ProtocolLib")) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("ProtocolLib not found! Custom Packets will not work!"), Level.WARNING, "PROTOCOLLIB_DEPENDENCY_NOT_LOADED", log);
        }
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
        * Send a packet to a player
        * The packet is sent with the extra data at the front.
        * NOTE: The extra data is serialized with UnsafeSerializer
        * NOTE: The total lenght of the packet is attached to the start.
     */
    public void sendPacket(byte[] packet, Player receiver, String prefix, String channel, Object... extraData) {
        WrapperPlayServerCustomPayload customPayload = new WrapperPlayServerCustomPayload();

        // calculate size and add it to the packet
        try (ByteArrayOutputStream finalOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(finalOutputStream)) {

            // Terrible practice, but I cannot be bothered to make a decent serializer for this
            byte[] tempArray = UnsafeSerializer.serialize(kryo, extraData);

            if (SealLib.isDebug())
                SealLib.getPlugin().getLogger().info("Sending packet with extra data size: " + tempArray.length);

            // write the size of the extra data, ignore tempArray if it's empty
            if (tempArray.length > 0) {
                dataOutputStream.writeInt(tempArray.length+packet.length+4);
            } else {
                dataOutputStream.writeInt(packet.length+4);
            }

            if (SealLib.isDebug())
                SealLib.getPlugin().getLogger().info("Sending packet with total size: " + (packet.length+tempArray.length));

            // write the actual data
            dataOutputStream.write(tempArray);
            dataOutputStream.write(packet);
            dataOutputStream.flush();
            dataOutputStream.close();
            customPayload.setContents(finalOutputStream.toByteArray());
            if (SealLib.isDebug()) {
                SealLib.getPlugin().getLogger().info("Extra data: " + Arrays.toString(tempArray));
                SealLib.getPlugin().getLogger().info("Packet data: " + Arrays.toString(packet));
                SealLib.getPlugin().getLogger().info("Final packet: " + Arrays.toString(finalOutputStream.toByteArray()));
            }
            if (SealLib.isDebug())
                SealLib.getPlugin().getLogger().info("Sending packet with size: " + finalOutputStream.toByteArray().length);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
        // set channel and send packet
        customPayload.setChannel(new MinecraftKey(prefix, channel));
        customPayload.sendPacket(receiver);
    }

    public void receivePackets(Class<?> packet, Consumer<PacketEvent> consumer) {
        PacketType type = PacketType.fromClass(packet);
        receivePackets(type, consumer);
    }

    public void receivePackets(PacketType type, Consumer<PacketEvent> consumer) {
        protocolManager.addPacketListener(new PacketAdapter(SealLib.getPlugin(), ListenerPriority.NORMAL, type) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                consumer.accept(event);
            }
        });
    }

    public Kryo getKryo() {
        return kryo;
    }

}
