package pl.makoto.createsupportopac;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import pl.makoto.createsupportopac.addon.CreateSupportAddon;
import pl.makoto.createsupportopac.addon.OPACApiHolder;
import pl.makoto.createsupportopac.commands.CreateSupportCommands;
import pl.makoto.createsupportopac.network.*;

@Mod(CreateOPAC.MODID)
public class CreateOPAC {

    public static final String MODID = "create_opac";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateOPAC(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new CreateSupportAddon());
        NeoForge.EVENT_BUS.register(new EntityInteractionEventHandler());
        NeoForge.EVENT_BUS.register(new MarketplaceEventHandler());
        modEventBus.addListener(this::onRegisterPayloads);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var reg = event.registrar("1.0");
        // S2C — handler references ClientPacketHandler via lazy lambda (invokedynamic);
        // ClientPacketHandler is never loaded on the dedicated server because these
        // handlers are only called on the client dist.
        reg.playToClient(CreateOpenGuiPacket.TYPE, CreateOpenGuiPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(pl.makoto.createsupportopac.client.ClientPacketHandler::handleOpenGui));
        reg.playToClient(CreateSettingsDataPacket.TYPE, CreateSettingsDataPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() ->
                        pl.makoto.createsupportopac.client.ClientPacketHandler.handleSettingsData(p.targetUUID(), p.values())));
        // C2S — safe on both sides
        reg.playToServer(CreateSettingsRequestPacket.TYPE, CreateSettingsRequestPacket.STREAM_CODEC, (p, ctx) -> p.handle(ctx));
        reg.playToServer(CreateSettingsSyncPacket.TYPE,  CreateSettingsSyncPacket.STREAM_CODEC,  (p, ctx) -> p.handle(ctx));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CreateSupportCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        OPACApiHolder.onServerStop();
    }
}
