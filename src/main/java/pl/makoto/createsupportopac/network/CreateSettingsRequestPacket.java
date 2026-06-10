package pl.makoto.createsupportopac.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import pl.makoto.createsupportopac.CreateOPAC;
import pl.makoto.createsupportopac.permission.CreateAdminPermissions;
import pl.makoto.createsupportopac.settings.CreateClaimSettings;
import pl.makoto.createsupportopac.settings.CreateClaimSettingsManager;

import java.util.UUID;

public record CreateSettingsRequestPacket(UUID targetUUID) implements CustomPacketPayload {

    public static final Type<CreateSettingsRequestPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateOPAC.MODID, "settings_request"));

    public static final StreamCodec<ByteBuf, CreateSettingsRequestPacket> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeLong(p.targetUUID.getMostSignificantBits()); buf.writeLong(p.targetUUID.getLeastSignificantBits()); },
            buf -> new CreateSettingsRequestPacket(new UUID(buf.readLong(), buf.readLong()))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (!canAccess(player, targetUUID)) return;
            CreateClaimSettings settings = CreateClaimSettingsManager.get(player.server).getOrCreate(targetUUID);
            player.connection.send(new net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket(
                    new CreateSettingsDataPacket(targetUUID, settings.toArray())
            ));
        });
    }

    private static boolean canAccess(ServerPlayer player, UUID target) {
        if (target.equals(player.getUUID())) return true;
        return CreateAdminPermissions.hasAdminAccess(player);
    }
}
