package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(value = OpenEndedPipe.class, remap = false)
public class MixinOpenEndedPipeCSO {

    @Shadow private Level world;
    @Shadow private BlockPos pos;
    @Shadow private BlockPos outputPos;

    @Inject(method = "provideFluidToSpace",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 0),
            cancellable = true)
    private void cso_provideFluidToSpace(FluidStack fluid, boolean simulate,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (cir.isCancelled() || simulate) return;
        if (!CreatePermissionChecker.isAllowedFromMachine(world, outputPos, pos, CreateMachineType.PIPE))
            cir.setReturnValue(false);
    }

    @Inject(method = "removeFluidFromSpace",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 0),
            cancellable = true)
    private void cso_removeFluidFromSpace(boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        if (cir.isCancelled() || simulate) return;
        if (!CreatePermissionChecker.isAllowedFromMachine(world, outputPos, pos, CreateMachineType.PIPE))
            cir.setReturnValue(FluidStack.EMPTY);
    }
}
