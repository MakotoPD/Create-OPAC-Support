package pl.makoto.createsupportopac.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import pl.makoto.createsupportopac.CreateOPAC;

public record CreateOpenGuiPacket() implements CustomPacketPayload {

    public static final Type<CreateOpenGuiPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateOPAC.MODID, "open_gui"));

    public static final StreamCodec<ByteBuf, CreateOpenGuiPacket> STREAM_CODEC =
            StreamCodec.unit(new CreateOpenGuiPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
