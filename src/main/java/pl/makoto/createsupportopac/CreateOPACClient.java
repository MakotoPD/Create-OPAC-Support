package pl.makoto.createsupportopac;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import pl.makoto.createsupportopac.client.screen.CreateMainMenuScreen;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateOPAC.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateOPAC.MODID, value = Dist.CLIENT)
public class CreateOPACClient {
    public CreateOPACClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        CreateOPAC.LOGGER.info("HELLO FROM CLIENT SETUP");
        CreateOPAC.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!"xaero.pac.client.gui.MainMenu".equals(screen.getClass().getName())) return;

        event.addListener(
            Button.builder(
                Component.literal("Create Settings"),
                btn -> Minecraft.getInstance().setScreen(new CreateMainMenuScreen(screen))
            ).bounds(10, screen.height - 28, 120, 20).build()
        );
    }
}
