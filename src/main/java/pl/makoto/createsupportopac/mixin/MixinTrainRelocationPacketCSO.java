package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

import java.util.UUID;

@Mixin(targets = "com.simibubi.create.content.trains.entity.TrainRelocationPacket", remap = false)
public class MixinTrainRelocationPacketCSO {

    @Shadow private UUID trainId;
    @Shadow private BlockPos pos;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void cso_handle(ServerPlayer player, CallbackInfo ci) {
        if (ci.isCancelled() || player == null) return;
        // Train owners can always relocate their own trains regardless of claim settings
        if (isTrainOwner(player)) return;
        if (!CreatePermissionChecker.isAllowed(player.level(), pos, player.getUUID(), CreateMachineType.TRAIN))
            ci.cancel();
    }

    private boolean isTrainOwner(ServerPlayer player) {
        try {
            if (trainId == null) return false;
            Train train = Create.RAILWAYS.trains.get(trainId);
            if (train == null || train.owner == null) return false;
            return player.getUUID().equals(train.owner);
        } catch (Exception ignored) {
            return false;
        }
    }
}
