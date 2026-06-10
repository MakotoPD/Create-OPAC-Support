package pl.makoto.createsupportopac.client;

import net.minecraft.client.Minecraft;
import pl.makoto.createsupportopac.client.screen.CreateMainMenuScreen;
import pl.makoto.createsupportopac.client.screen.CreateMachineSettingsScreen;

import java.util.UUID;

/**
 * All client-side packet handling lives here.
 * This class is never loaded on the dedicated server — it is only referenced
 * from lambdas (invokedynamic) that are never executed on the server dist.
 */
public final class ClientPacketHandler {

    private ClientPacketHandler() {}

    public static void handleOpenGui() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new CreateMainMenuScreen(mc.screen));
    }

    public static void handleSettingsData(UUID targetUUID, boolean[] values) {
        ClientSettingsCache.store(targetUUID, values);
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof CreateMachineSettingsScreen screen) {
            screen.onDataReceived(targetUUID, values);
        }
    }
}
