package pl.makoto.createsupportopac.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import pl.makoto.createsupportopac.CreateOPAC;

import java.util.UUID;

public record CreateSettingsDataPacket(UUID targetUUID, boolean[] values) implements CustomPacketPayload {

    public static final Type<CreateSettingsDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateOPAC.MODID, "settings_data"));

    public static final StreamCodec<ByteBuf, CreateSettingsDataPacket> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeLong(p.targetUUID.getMostSignificantBits());
                buf.writeLong(p.targetUUID.getLeastSignificantBits());
                buf.writeShort(p.values.length);
                for (boolean b : p.values) buf.writeBoolean(b);
            },
            buf -> {
                UUID uuid = new UUID(buf.readLong(), buf.readLong());
                int len = buf.readShort() & 0xFFFF;
                boolean[] vals = new boolean[len];
                for (int i = 0; i < len; i++) vals[i] = buf.readBoolean();
                return new CreateSettingsDataPacket(uuid, vals);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
