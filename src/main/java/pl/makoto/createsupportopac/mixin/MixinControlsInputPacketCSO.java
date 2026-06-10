package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(targets = "com.simibubi.create.content.contraptions.actors.trainControls.ControlsInputPacket", remap = false)
public class MixinControlsInputPacketCSO {

    @Shadow private BlockPos controlsPos;
    @Shadow private int contraptionEntityId;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void cso_handle(ServerPlayer player, CallbackInfo ci) {
        if (ci.isCancelled() || player == null) return;
        if (controlsPos == null) return;
        // Train owners can always drive their own trains regardless of claim settings
        if (isTrainOwner(player)) return;
        if (!CreatePermissionChecker.isAllowed(player.level(), controlsPos, player.getUUID(), CreateMachineType.TRAIN))
            ci.cancel();
    }

    private boolean isTrainOwner(ServerPlayer player) {
        try {
            Entity entity = player.level().getEntity(contraptionEntityId);
            if (!(entity instanceof CarriageContraptionEntity carriageEntity)) return false;
            Train train = Create.RAILWAYS.trains.get(carriageEntity.trainId);
            if (train == null || train.owner == null) return false;
            return player.getUUID().equals(train.owner);
        } catch (Exception ignored) {
            return false;
        }
    }
}
