package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(value = ArmBlockEntity.class, remap = false)
public class MixinArmBlockEntityCSO {

    @Inject(method = "searchForItem", at = @At("HEAD"), cancellable = true)
    private void cso_searchForItem(CallbackInfo ci) {
        if (ci.isCancelled()) return;
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getLevel() == null) return;
        if (!CreatePermissionChecker.isAllowed(self.getLevel(), self.getBlockPos(), CreateMachineType.ARM))
            ci.cancel();
    }

    @Inject(method = "searchForDestination", at = @At("HEAD"), cancellable = true)
    private void cso_searchForDestination(CallbackInfo ci) {
        if (ci.isCancelled()) return;
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getLevel() == null) return;
        if (!CreatePermissionChecker.isAllowed(self.getLevel(), self.getBlockPos(), CreateMachineType.ARM))
            ci.cancel();
    }
}
