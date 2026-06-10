package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

// Stationary drills and saws (SawBlockEntity.canBreak calls super, so this covers both).
@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public class MixinBlockBreakingKineticBlockEntityCSO {

    @Shadow protected BlockPos breakingPos;

    @Inject(method = "canBreak", at = @At("HEAD"), cancellable = true)
    private void cso_canBreak(BlockState stateToBreak, float blockHardness,
                              CallbackInfoReturnable<Boolean> cir) {
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide() || breakingPos == null) return;
        if (!CreatePermissionChecker.isAllowedFromMachine(level, breakingPos,
                self.getBlockPos(), CreateMachineType.DRILL))
            cir.setReturnValue(false);
    }
}
