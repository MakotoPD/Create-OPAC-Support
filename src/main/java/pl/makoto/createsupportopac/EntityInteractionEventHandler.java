package pl.makoto.createsupportopac;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

public class EntityInteractionEventHandler {

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Entity target = event.getTarget();

        if (CreatePermissionChecker.isAllowed(
                event.getLevel(), target.blockPosition(), player.getUUID(), resolveMachineType(target)))
            return;

        event.setCanceled(true);
    }

    private static CreateMachineType resolveMachineType(Entity target) {
        if (isEasyNPCEntity(target))
            return CreateMachineType.NPC;
        Entity vehicle = target.getVehicle();
        if (vehicle != null && vehicle.getClass().getName().contains("SeatEntity"))
            return CreateMachineType.MARKETPLACE;
        return CreateMachineType.ENTITY;
    }

    public static boolean isEasyNPCEntity(Entity entity) {
        return "easy_npc".equals(
            entity.getType()
                  .builtInRegistryHolder()
                  .key()
                  .location()
                  .getNamespace()
        );
    }
}
