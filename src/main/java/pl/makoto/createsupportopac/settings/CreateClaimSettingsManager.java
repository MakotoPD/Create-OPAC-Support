package pl.makoto.createsupportopac.settings;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateClaimSettingsManager extends SavedData {

    public static final UUID SERVER_UUID         = new UUID(0L, 0L);
    public static final UUID EXPIRED_UUID        = new UUID(0L, 1L);
    public static final UUID WILDERNESS_UUID     = new UUID(0L, 2L);
    public static final UUID DEFAULT_PLAYER_UUID = new UUID(0L, 3L);

    private static final String DATA_KEY = "createsupportopac_settings";
    private static final SavedData.Factory<CreateClaimSettingsManager> FACTORY =
            new SavedData.Factory<>(CreateClaimSettingsManager::new, CreateClaimSettingsManager::load);

    private final Map<UUID, CreateClaimSettings> playerSettings = new HashMap<>();

    public static CreateClaimSettingsManager get(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(FACTORY, DATA_KEY);
    }

    public CreateClaimSettings getOrCreate(UUID playerId) {
        return playerSettings.computeIfAbsent(playerId, k -> new CreateClaimSettings());
    }

    /** Returns settings for the owner, falling back to default player config, then server defaults, then true. */
    public boolean isAllowed(UUID claimOwner, CreateMachineType machineType, ClaimRelationship relationship) {
        CreateClaimSettings settings = playerSettings.get(claimOwner);
        if (settings != null) return settings.isAllowed(machineType, relationship);
        CreateClaimSettings defaultPlayer = playerSettings.get(DEFAULT_PLAYER_UUID);
        if (defaultPlayer != null) return defaultPlayer.isAllowed(machineType, relationship);
        CreateClaimSettings serverDefaults = playerSettings.get(SERVER_UUID);
        if (serverDefaults != null) return serverDefaults.isAllowed(machineType, relationship);
        return true;
    }

    public boolean isAllowed(UUID claimOwner, CreateMachineType machineType) {
        return isAllowed(claimOwner, machineType, ClaimRelationship.STRANGER);
    }

    public void setAllowed(UUID playerId, CreateMachineType machineType, ClaimRelationship relationship, boolean value) {
        getOrCreate(playerId).setAllowed(machineType, relationship, value);
        setDirty();
    }

    public void setAllowed(UUID playerId, CreateMachineType machineType, boolean value) {
        setAllowed(playerId, machineType, ClaimRelationship.STRANGER, value);
    }

    public void reset(UUID playerId) {
        CreateClaimSettings settings = playerSettings.get(playerId);
        if (settings != null) {
            settings.reset();
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag playersTag = new CompoundTag();
        playerSettings.forEach((uuid, settings) -> playersTag.put(uuid.toString(), settings.save()));
        tag.put("players", playersTag);
        return tag;
    }

    private static CreateClaimSettingsManager load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        CreateClaimSettingsManager manager = new CreateClaimSettingsManager();
        CompoundTag playersTag = tag.getCompound("players");
        for (String key : playersTag.getAllKeys()) {
            try {
                manager.playerSettings.put(UUID.fromString(key),
                        CreateClaimSettings.load(playersTag.getCompound(key)));
            } catch (IllegalArgumentException ignored) {}
        }
        return manager;
    }
}
