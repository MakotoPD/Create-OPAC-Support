package pl.makoto.createsupportopac.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.level.ServerPlayer;

public final class CreateAdminPermissions {

    public static final String ADMIN_NODE = "createsupportopac.admin";

    private CreateAdminPermissions() {}

    /**
     * Returns true if the player may access admin config options.
     * Checks vanilla OP first, then LuckPerms if available.
     */
    public static boolean hasAdminAccess(ServerPlayer player) {
        if (player.hasPermissions(2)) return true;
        User user = getLuckPermsUser(player);
        if (user == null) return false;
        return user.getCachedData()
                .getPermissionData(QueryOptions.defaultContextualOptions())
                .checkPermission(ADMIN_NODE)
                .asBoolean();
    }

    /**
     * Resolves the LuckPerms User for the given player.
     * Mirrors OPAC's approach: checks isLoaded first, loads async (blocking) if needed.
     * Returns null if LuckPerms is unavailable or loading fails.
     */
    private static User getLuckPermsUser(ServerPlayer player) {
        LuckPerms luckPerms;
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (Exception ignored) {
            return null;
        }
        UserManager userManager = luckPerms.getUserManager();
        if (!userManager.isLoaded(player.getUUID())) {
            try {
                return userManager.loadUser(player.getUUID()).join();
            } catch (Throwable t) {
                return null;
            }
        }
        return userManager.getUser(player.getUUID());
    }
}
