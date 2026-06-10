package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(value = Contraption.class, remap = false)
public class MixinContraptionCSO {

    @Inject(method = "movementAllowed", at = @At("HEAD"), cancellable = true)
    private void cso_movementAllowed(BlockState state, Level level, BlockPos pos,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (cir.isCancelled()) return;
        if (!CreatePermissionChecker.isAllowed(level, pos, CreateMachineType.CONTRAPTION))
            cir.setReturnValue(false);
    }
}
