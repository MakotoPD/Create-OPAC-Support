package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

import java.util.UUID;

@Mixin(value = DeployerBlockEntity.class, remap = false)
public class MixinDeployerBlockEntityCSO {

    @Shadow protected UUID owner;

    @Inject(method = "activate", at = @At("HEAD"), cancellable = true)
    private void cso_activate(CallbackInfo ci) {
        if (ci.isCancelled()) return;
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getLevel() == null) return;
        BlockPos clickedPos = self.getBlockPos().relative(
                self.getBlockState().getValue(BlockStateProperties.FACING), 2);
        if (!CreatePermissionChecker.isAllowedFromMachine(self.getLevel(), clickedPos, owner,
                self.getBlockPos(), CreateMachineType.DEPLOYER))
            ci.cancel();
    }
}
