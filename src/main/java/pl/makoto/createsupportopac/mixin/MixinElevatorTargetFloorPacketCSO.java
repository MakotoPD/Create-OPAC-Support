package pl.makoto.createsupportopac.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

// String target avoids compiler loading Catnip ServerboundPacketPayload hierarchy
@Mixin(targets = "com.simibubi.create.content.contraptions.elevator.ElevatorTargetFloorPacket", remap = false)
public class MixinElevatorTargetFloorPacketCSO {

    @Shadow private int entityId;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void cso_handle(ServerPlayer player, CallbackInfo ci) {
        if (ci.isCancelled() || player == null) return;
        Entity entity = player.serverLevel().getEntity(entityId);
        if (entity == null) return;
        if (!CreatePermissionChecker.isAllowed(player.level(), entity.blockPosition(), player.getUUID(), CreateMachineType.ELEVATOR))
            ci.cancel();
    }
}
