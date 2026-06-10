package pl.makoto.createsupportopac.addon;

import net.neoforged.bus.api.SubscribeEvent;
import pl.makoto.createsupportopac.CreateOPAC;
import xaero.pac.common.event.api.OPACServerAddonRegisterEvent;

/**
 * Listens for OPAC's addon register event to hook into the OPAC system.
 * Registered on NeoForge.EVENT_BUS.
 */
public class CreateSupportAddon {

    @SubscribeEvent
    public void onOPACAddonRegister(OPACServerAddonRegisterEvent event) {
        OPACApiHolder.onServerStart(event.getServer());
        CreateOPAC.LOGGER.info("[CreateOPAC] Registered with Open Parties and Claims.");
    }
}
