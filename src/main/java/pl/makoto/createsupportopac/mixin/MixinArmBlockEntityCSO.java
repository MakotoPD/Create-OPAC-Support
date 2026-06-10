package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

import java.util.List;

@Mixin(value = ArmBlockEntity.class, remap = false)
public class MixinArmBlockEntityCSO {

    @Shadow List<ArmInteractionPoint> inputs;
    @Shadow List<ArmInteractionPoint> outputs;

    @Inject(method = "searchForItem", at = @At("HEAD"), cancellable = true)
    private void cso_searchForItem(CallbackInfo ci) {
        if (ci.isCancelled()) return;
        if (cso_anyPointDenied()) ci.cancel();
    }

    @Inject(method = "searchForDestination", at = @At("HEAD"), cancellable = true)
    private void cso_searchForDestination(CallbackInfo ci) {
        if (ci.isCancelled()) return;
        if (cso_anyPointDenied()) ci.cancel();
    }

    // The permission must be checked at each interaction point, not at the arm itself —
    // an arm standing outside a claim can reach inventories inside it. Both lists are
    // checked in both searches so the arm never picks up an item it cannot deposit.
    private boolean cso_anyPointDenied() {
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return false;
        BlockPos armPos = self.getBlockPos();
        return cso_anyDenied(level, armPos, inputs) || cso_anyDenied(level, armPos, outputs);
    }

    private static boolean cso_anyDenied(Level level, BlockPos armPos, List<ArmInteractionPoint> points) {
        if (points == null) return false;
        for (ArmInteractionPoint point : points) {
            if (point == null) continue;
            if (!CreatePermissionChecker.isAllowedFromMachine(level, point.getPos(), armPos, CreateMachineType.ARM))
                return true;
        }
        return false;
    }
}
