package pl.makoto.createsupportopac.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import pl.makoto.createsupportopac.client.ClientSettingsCache;
import pl.makoto.createsupportopac.network.CreateSettingsRequestPacket;
import pl.makoto.createsupportopac.network.CreateSettingsSyncPacket;
import pl.makoto.createsupportopac.settings.ClaimRelationship;
import pl.makoto.createsupportopac.settings.CreateMachineType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateMachineSettingsScreen extends Screen {

    private static final int ROW_H    = 22;
    private static final int BTN_W    = 205;
    private static final int TOGGLE_H = 18;

    private final Screen parent;
    private final UUID targetUUID;
    private final String contextTitle;

    private ClaimRelationship currentTab = ClaimRelationship.STRANGER;
    private boolean loading = true;
    private boolean[] data; // flat array from CreateClaimSettings.toArray()

    private final List<Button> toggleButtons = new ArrayList<>();

    public CreateMachineSettingsScreen(Screen parent, UUID targetUUID, String contextTitle) {
        super(Component.literal("Create Settings — " + contextTitle));
        this.parent = parent;
        this.targetUUID = targetUUID;
        this.contextTitle = contextTitle;
    }

    @Override
    protected void init() {
        toggleButtons.clear();

        int cx = this.width / 2;

        // Tab buttons
        ClaimRelationship[] tabs = ClaimRelationship.values();
        int tabW = 65;
        int tabStart = cx - (tabs.length * (tabW + 2)) / 2;
        for (int i = 0; i < tabs.length; i++) {
            final ClaimRelationship tab = tabs[i];
            Button tabBtn = Button.builder(
                    Component.literal(tab.displayName()),
                    btn -> { currentTab = tab; rebuildWidgets(); }
            ).bounds(tabStart + i * (tabW + 2), 25, tabW, 18).build();
            addRenderableWidget(tabBtn);
        }

        // Toggle buttons (2 columns)
        CreateMachineType[] machines = CreateMachineType.values();
        int startY = 50;
        int col1X = cx - BTN_W - 5;
        int col2X = cx + 5;
        int colSize = (machines.length + 1) / 2; // rows in col1

        for (int i = 0; i < machines.length; i++) {
            final CreateMachineType machine = machines[i];
            int col = i / colSize;
            int row = i % colSize;
            int x = col == 0 ? col1X : col2X;
            int y = startY + row * ROW_H;

            boolean currentValue = getValue(machine);
            Button toggle = Button.builder(
                    makeLabel(machine, currentValue),
                    btn -> onToggle(btn, machine)
            ).bounds(x, y, BTN_W, TOGGLE_H).build();

            if (loading) toggle.active = false;
            toggleButtons.add(toggle);
            addRenderableWidget(toggle);
        }

        // Back button
        addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> Minecraft.getInstance().setScreen(parent)
        ).bounds(cx - 50, this.height - 28, 100, 20).build());

        // Request fresh data from server
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(
                    new ServerboundCustomPayloadPacket(new CreateSettingsRequestPacket(targetUUID))
            );
        }
    }

    private boolean getValue(CreateMachineType machine) {
        if (data == null) return true;
        int idx = machine.ordinal() * 3 + currentTab.ordinal();
        return idx < data.length ? data[idx] : true;
    }

    private void onToggle(Button btn, CreateMachineType machine) {
        if (data == null) return;
        int idx = machine.ordinal() * 3 + currentTab.ordinal();
        boolean newVal = !data[idx];
        data[idx] = newVal;
        ClientSettingsCache.store(targetUUID, data);
        btn.setMessage(makeLabel(machine, newVal));
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(
                    new ServerboundCustomPayloadPacket(new CreateSettingsSyncPacket(
                            targetUUID, machine.ordinal(), currentTab.ordinal(), newVal
                    ))
            );
        }
    }

    private Component makeLabel(CreateMachineType machine, boolean allowed) {
        String status = allowed ? "§aAllow" : "§cDeny";
        return Component.literal(machine.displayName + ": " + status);
    }

    /** Called when CreateSettingsDataPacket arrives on the client thread. */
    public void onDataReceived(UUID uuid, boolean[] values) {
        if (!uuid.equals(targetUUID)) return;
        this.data = values.clone();
        this.loading = false;
        rebuildWidgets();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        // Tab underline for selected tab
        ClaimRelationship[] tabs = ClaimRelationship.values();
        int tabW = 65;
        int tabStart = this.width / 2 - (tabs.length * (tabW + 2)) / 2;
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] == currentTab) {
                int x = tabStart + i * (tabW + 2);
                graphics.fill(x, 43, x + tabW, 44, 0xFFFFFFFF);
            }
        }
        if (loading) {
            graphics.drawCenteredString(this.font, "Loading...", this.width / 2, this.height / 2, 0xAAAAAA);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
