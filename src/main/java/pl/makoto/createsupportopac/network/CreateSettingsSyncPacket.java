package pl.makoto.createsupportopac.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import pl.makoto.createsupportopac.CreateOPAC;
import pl.makoto.createsupportopac.permission.CreateAdminPermissions;
import pl.makoto.createsupportopac.settings.ClaimRelationship;
import pl.makoto.createsupportopac.settings.CreateClaimSettingsManager;
import pl.makoto.createsupportopac.settings.CreateMachineType;

import java.util.UUID;

public record CreateSettingsSyncPacket(UUID targetUUID, int machineOrdinal, int relOrdinal, boolean allow)
        implements CustomPacketPayload {

    public static final Type<CreateSettingsSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateOPAC.MODID, "settings_sync"));

    public static final StreamCodec<ByteBuf, CreateSettingsSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeLong(p.targetUUID.getMostSignificantBits());
                buf.writeLong(p.targetUUID.getLeastSignificantBits());
                buf.writeByte(p.machineOrdinal);
                buf.writeByte(p.relOrdinal);
                buf.writeBoolean(p.allow);
            },
            buf -> new CreateSettingsSyncPacket(
                    new UUID(buf.readLong(), buf.readLong()),
                    buf.readByte() & 0xFF,
                    buf.readByte() & 0xFF,
                    buf.readBoolean()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (!canAccess(player, targetUUID)) return;
            CreateMachineType[] machines = CreateMachineType.values();
            ClaimRelationship[] rels = ClaimRelationship.values();
            if (machineOrdinal >= machines.length || relOrdinal >= rels.length) return;
            CreateClaimSettingsManager.get(player.server)
                    .setAllowed(targetUUID, machines[machineOrdinal], rels[relOrdinal], allow);
        });
    }

    private static boolean canAccess(ServerPlayer player, UUID target) {
        if (target.equals(player.getUUID())) return true;
        return CreateAdminPermissions.hasAdminAccess(player);
    }
}
