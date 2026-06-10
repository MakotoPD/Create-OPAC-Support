package pl.makoto.createsupportopac.permission;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import pl.makoto.createsupportopac.Config;
import pl.makoto.createsupportopac.addon.OPACApiHolder;
import pl.makoto.createsupportopac.settings.ClaimRelationship;
import pl.makoto.createsupportopac.settings.CreateClaimSettingsManager;
import pl.makoto.createsupportopac.settings.CreateMachineType;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
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

    /** Check with a known actor UUID for relationship-based permission. */
    public static boolean isAllowed(Level level, BlockPos pos, @Nullable UUID actorUUID, CreateMachineType machineType) {
        if (!(level instanceof ServerLevel)) return true;
        MinecraftServer server = level.getServer();
        if (server == null) return true;

        IServerClaimsManagerAPI claimsManager = OPACApiHolder.getClaimsManager();
        if (claimsManager == null) return true;

        ResourceLocation dimension = level.dimension().location();
        @SuppressWarnings("unchecked")
        IPlayerChunkClaimAPI claim = claimsManager.get(dimension, pos);

        if (claim == null) return Config.ALLOW_CREATE_IN_WILDERNESS.getAsBoolean();

        UUID ownerUUID = claim.getPlayerId();

        // Owner always allowed
        if (ownerUUID != null && ownerUUID.equals(actorUUID)) return true;

        ClaimRelationship rel = resolveRelationship(server, ownerUUID, actorUUID);
        return CreateClaimSettingsManager.get(server).isAllowed(ownerUUID, machineType, rel);
    }

    private static ClaimRelationship resolveRelationship(MinecraftServer server,
                                                          @Nullable UUID ownerUUID,
                                                          @Nullable UUID actorUUID) {
        if (actorUUID == null || ownerUUID == null) return ClaimRelationship.STRANGER;
        try {
            @SuppressWarnings("rawtypes")
            IPlayerPartySystemManager partySystem = OPACApiHolder.getPartySystemManager();
            if (partySystem == null) return ClaimRelationship.STRANGER;
            if (partySystem.areInSameParty(actorUUID, ownerUUID)) return ClaimRelationship.MEMBER;
            if (partySystem.isPlayerAllying(ownerUUID, actorUUID)) return ClaimRelationship.ALLY;
        } catch (Throwable ignored) {}
        return ClaimRelationship.STRANGER;
    }
}
