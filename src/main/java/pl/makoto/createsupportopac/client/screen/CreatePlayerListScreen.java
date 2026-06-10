package pl.makoto.createsupportopac.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CreatePlayerListScreen extends Screen {

    private static final int ROW_H = 22;

    private final Screen parent;
    private final List<PlayerInfo> players = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int VISIBLE_ROWS = 10;

    public CreatePlayerListScreen(Screen parent) {
        super(Component.literal("Select Player"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        players.clear();
        if (Minecraft.getInstance().getConnection() != null) {
            players.addAll(Minecraft.getInstance().getConnection().getOnlinePlayers());
            players.sort((a, b) -> a.getProfile().getName().compareToIgnoreCase(b.getProfile().getName()));
        }

        rebuildPlayerButtons();

        // Scroll buttons if needed
        if (players.size() > VISIBLE_ROWS) {
            addRenderableWidget(Button.builder(Component.literal("▲"), btn -> {
                if (scrollOffset > 0) { scrollOffset--; rebuildWidgets(); }
            }).bounds(this.width / 2 + 115, 40, 20, 18).build());
            addRenderableWidget(Button.builder(Component.literal("▼"), btn -> {
                if (scrollOffset + VISIBLE_ROWS < players.size()) { scrollOffset++; rebuildWidgets(); }
            }).bounds(this.width / 2 + 115, 40 + VISIBLE_ROWS * ROW_H - 18, 20, 18).build());
        }

        addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> Minecraft.getInstance().setScreen(parent)
        ).bounds(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    private void rebuildPlayerButtons() {
        int cx = this.width / 2;
        int start = Math.min(scrollOffset, Math.max(0, players.size() - VISIBLE_ROWS));
        int end = Math.min(start + VISIBLE_ROWS, players.size());
        for (int i = start; i < end; i++) {
            final PlayerInfo info = players.get(i);
            String name = info.getProfile().getName();
            addRenderableWidget(Button.builder(
                    Component.literal(name),
                    btn -> Minecraft.getInstance().setScreen(
                            new CreateMachineSettingsScreen(this, info.getProfile().getId(), name))
            ).bounds(cx - 110, 40 + (i - start) * ROW_H, 220, 18).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
