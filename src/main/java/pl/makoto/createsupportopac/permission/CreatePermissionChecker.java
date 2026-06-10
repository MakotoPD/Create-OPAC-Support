package pl.makoto.createsupportopac.permission;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import pl.makoto.createsupportopac.Config;
import pl.makoto.createsupportopac.settings.ClaimRelationship;
import pl.makoto.createsupportopac.settings.CreateClaimSettingsManager;
import pl.makoto.createsupportopac.settings.CreateMachineType;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.server.IServerData;
import xaero.pac.common.server.ServerData;
import xaero.pac.common.server.claims.api.IServerClaimsManagerAPI;
import xaero.pac.common.server.parties.system.IPlayerPartySystemManager;

import javax.annotation.Nullable;
import java.util.UUID;

public final class CreatePermissionChecker {

    private CreatePermissionChecker() {}

    /** Check without knowing the actor — uses stranger settings (conservative). */
    public static boolean isAllowed(Level level, BlockPos pos, CreateMachineType machineType) {
        return isAllowed(level, pos, (UUID) null, machineType);
    }

    /**
     * Check on behalf of a machine with no controlling player — the machine acts as the
     * owner of the claim it stands in (or its contraption anchor), so owners' machines
     * keep working on their own claims.
     */
    public static boolean isAllowedFromMachine(Level level, BlockPos targetPos,
                                               @Nullable BlockPos machinePos, CreateMachineType machineType) {
        return isAllowed(level, targetPos, claimOwnerAt(level, machinePos), machineType);
    }

    /** Same as above, but a known controlling player takes precedence over the machine's claim owner. */
    public static boolean isAllowedFromMachine(Level level, BlockPos targetPos,
                                               @Nullable UUID controllingPlayer,
                                               @Nullable BlockPos machinePos, CreateMachineType machineType) {
        UUID actor = controllingPlayer != null ? controllingPlayer : claimOwnerAt(level, machinePos);
        return isAllowed(level, targetPos, actor, machineType);
    }

    /** Owner UUID of the claim at the given position, or null for wilderness / unavailable OPAC. */
    @Nullable
    public static UUID claimOwnerAt(Level level, @Nullable BlockPos pos) {
        if (pos == null || !(level instanceof ServerLevel serverLevel)) return null;
        IServerClaimsManagerAPI claimsManager = claimsManager(serverLevel);
        if (claimsManager == null) return null;
        IPlayerChunkClaimAPI claim = claimsManager.get(level.dimension().location(), pos);
        return claim == null ? null : claim.getPlayerId();
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    private static IServerData serverData(ServerLevel level) {
        MinecraftServer server = level.getServer();
        if (server == null) return null;
        try {
            return ServerData.from(server);
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    private static IServerClaimsManagerAPI claimsManager(ServerLevel level) {
        IServerData serverData = serverData(level);
        if (serverData == null) return null;
        try {
            return (IServerClaimsManagerAPI) serverData.getServerClaimsManager();
        } catch (Throwable t) {
            return null;
        }
    }

    /** Check with a known actor UUID for relationship-based permission. */
    @SuppressWarnings("rawtypes")
    public static boolean isAllowed(Level level, BlockPos pos, @Nullable UUID actorUUID, CreateMachineType machineType) {
        if (!(level instanceof ServerLevel serverLevel)) return true;
        MinecraftServer server = serverLevel.getServer();
        if (server == null) return true;

        IServerData serverData = serverData(serverLevel);
        if (serverData == null) return true;

        IServerClaimsManagerAPI claimsManager = claimsManager(serverLevel);
        if (claimsManager == null) return true;

        ResourceLocation dimension = level.dimension().location();
        IPlayerChunkClaimAPI claim = claimsManager.get(dimension, pos);

        if (claim == null) return Config.ALLOW_CREATE_IN_WILDERNESS.getAsBoolean();

        UUID ownerUUID = claim.getPlayerId();

        // Owner always allowed
        if (ownerUUID != null && ownerUUID.equals(actorUUID)) return true;

        ClaimRelationship rel = resolveRelationship(serverData, ownerUUID, actorUUID);
        return CreateClaimSettingsManager.get(server).isAllowed(ownerUUID, machineType, rel);
    }

    @SuppressWarnings("rawtypes")
    private static ClaimRelationship resolveRelationship(IServerData serverData,
                                                          @Nullable UUID ownerUUID,
                                                          @Nullable UUID actorUUID) {
        if (actorUUID == null || ownerUUID == null) return ClaimRelationship.STRANGER;
        try {
            IPlayerPartySystemManager partySystem = serverData.getPlayerPartySystemManager();
            if (partySystem == null) return ClaimRelationship.STRANGER;
            if (partySystem.areInSameParty(actorUUID, ownerUUID)) return ClaimRelationship.MEMBER;
            if (partySystem.isPlayerAllying(ownerUUID, actorUUID)) return ClaimRelationship.ALLY;
        } catch (Throwable ignored) {}
        return ClaimRelationship.STRANGER;
    }
}
