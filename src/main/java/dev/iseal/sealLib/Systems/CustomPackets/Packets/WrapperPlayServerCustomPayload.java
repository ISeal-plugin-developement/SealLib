/**
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 * <p>
 * Props to them for this impl, I suck at this stuff
 */

package dev.iseal.sealLib.Systems.CustomPackets.Packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WrapperPlayServerCustomPayload extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CUSTOM_PAYLOAD;

    public WrapperPlayServerCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Channel.
     * <p>
     * Notes: name of the "channel" used to send the data.
     *
     * @return The current Channel
     */
    public MinecraftKey getChannel() {
        return handle.getMinecraftKeys().read(0);
    }

    /**
     * Starting in 1.13, channel names need to be lower case, in the new identifier format,
     * i.e. {@code minecraft:brand}. The previously standard {@code |} is no longer allowed.
     */
    public void setChannel(MinecraftKey value) {
        handle.getMinecraftKeys().write(0, value);
    }

    /**
     * Retrieve payload contents as a raw Netty buffer
     *
     * @return Payload contents as a Netty buffer
     */
    public ByteBuf getContentsBuffer() {
        return (ByteBuf) handle.getModifier().withType(ByteBuf.class).read(0);
    }

    /**
     * Retrieve payload contents
     *
     * @return Payload contents as a byte array
     */
    public byte[] getContents() {
        ByteBuf buffer = getContentsBuffer();
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        return array;
    }

    /**
     * Update payload contents with a Netty buffer
     *
     * @param contents - new payload content
     */
    public void setContentsBuffer(ByteBuf contents) {
        if (MinecraftReflection.is(MinecraftReflection.getPacketDataSerializerClass(), contents)) {
            handle.getModifier().withType(ByteBuf.class).write(0, contents);
        } else {
            Object serializer = MinecraftReflection.getPacketDataSerializer(contents);
            handle.getModifier().withType(ByteBuf.class).write(0, serializer);
        }
    }

    /**
     * Update payload contents with a byte array
     *
     * @param content - new payload content
     */
    public void setContents(byte[] content) {
        setContentsBuffer(Unpooled.copiedBuffer(content));
    }
}