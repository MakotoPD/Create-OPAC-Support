package pl.makoto.createsupportopac.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import pl.makoto.createsupportopac.settings.CreateClaimSettingsManager;
import xaero.pac.OpenPartiesAndClaims;

import javax.annotation.Nullable;

public class CreateMainMenuScreen extends Screen {

    private final Screen parent;

    public CreateMainMenuScreen(@Nullable Screen parent) {
        super(Component.literal("Create Machine Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y  = this.height / 7 + 8;

        addRenderableWidget(Button.builder(
                Component.literal("My Claim Settings"),
                btn -> openSettings(Minecraft.getInstance().player.getUUID(), "My Claim Settings")
        ).bounds(cx - 100, y, 200, 20).build());

        if (hasAdminAccess(Minecraft.getInstance().player)) {
            addRenderableWidget(Button.builder(
                    Component.literal("Server Claims Config"),
                    btn -> openSettings(CreateClaimSettingsManager.SERVER_UUID, "Server Claims Config")
            ).bounds(cx - 100, y + 24, 200, 20).build());

            addRenderableWidget(Button.builder(
                    Component.literal("Expired Claims Config"),
                    btn -> openSettings(CreateClaimSettingsManager.EXPIRED_UUID, "Expired Claims Config")
            ).bounds(cx - 100, y + 48, 200, 20).build());

            addRenderableWidget(Button.builder(
                    Component.literal("Wilderness Config"),
                    btn -> openSettings(CreateClaimSettingsManager.WILDERNESS_UUID, "Wilderness Config")
            ).bounds(cx - 100, y + 72, 200, 20).build());

            addRenderableWidget(Button.builder(
                    Component.literal("Default Player Config"),
                    btn -> openSettings(CreateClaimSettingsManager.DEFAULT_PLAYER_UUID, "Default Player Config")
            ).bounds(cx - 100, y + 96, 200, 20).build());

            addRenderableWidget(Button.builder(
                    Component.literal("Edit Player Settings..."),
                    btn -> Minecraft.getInstance().setScreen(new CreatePlayerListScreen(this))
            ).bounds(cx - 100, y + 120, 200, 20).build());
        }

        addRenderableWidget(Button.builder(
                Component.literal("Close"),
                btn -> onClose()
        ).bounds(cx - 50, this.height - 28, 100, 20).build());
    }

    private void openSettings(java.util.UUID targetUUID, String title) {
        Minecraft.getInstance().setScreen(new CreateMachineSettingsScreen(this, targetUUID, title));
    }

    /**
     * Client-side admin check: vanilla OP or OPAC admin-mode (/pac admin on).
     * LuckPerms is server-side only and cannot be queried here.
     */
    private static boolean hasAdminAccess(@Nullable LocalPlayer player) {
        if (player == null) return false;
        if (player.hasPermissions(2)) return true;
        try {
            return OpenPartiesAndClaims.INSTANCE.getClientDataInternal()
                    .getClaimsManager().isAdminMode();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
