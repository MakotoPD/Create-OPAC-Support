package pl.makoto.createsupportopac.client;

import javax.annotation.Nullable;
import java.util.UUID;

public final class ClientSettingsCache {

    @Nullable private static UUID cachedUUID;
    @Nullable private static boolean[] cachedValues;

    private ClientSettingsCache() {}

    public static void store(UUID uuid, boolean[] values) {
        cachedUUID = uuid;
        cachedValues = values.clone();
    }

    public static boolean isLoaded(UUID uuid) {
        return uuid.equals(cachedUUID) && cachedValues != null;
    }

    @Nullable
    public static boolean[] get(UUID uuid) {
        return isLoaded(uuid) ? cachedValues.clone() : null;
    }

    public static void invalidate() {
        cachedUUID = null;
        cachedValues = null;
    }
}
