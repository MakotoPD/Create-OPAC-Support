package pl.makoto.createsupportopac.addon;

import net.minecraft.server.MinecraftServer;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.server.claims.api.IServerClaimsManagerAPI;
import xaero.pac.common.server.parties.system.IPlayerPartySystemManager;

import javax.annotation.Nullable;

public final class OPACApiHolder {

    @Nullable public static MinecraftServer server;

    private OPACApiHolder() {}

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static IServerClaimsManagerAPI getClaimsManager() {
        if (server == null) return null;
        try {
            xaero.pac.common.server.IServerData serverData =
                    xaero.pac.common.server.ServerData.from(server);
            if (serverData == null) return null;
            return (IServerClaimsManagerAPI) serverData.getServerClaimsManager();
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    public static IPlayerPartySystemManager getPartySystemManager() {
        if (server == null) return null;
        try {
            xaero.pac.common.server.IServerData serverData =
                    xaero.pac.common.server.ServerData.from(server);
            if (serverData == null) return null;
            return serverData.getPlayerPartySystemManager();
        } catch (Throwable t) {
            return null;
        }
    }

    public static void onServerStart(MinecraftServer s) { server = s; }
    public static void onServerStop()                  { server = null; }
}
