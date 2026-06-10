package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(value = DeployerMovementBehaviour.class, remap = false)
public class MixinDeployerBehaviourCSO {

    // Mode is package-private — declared as Object with @Coerce so Mixin handles the type at bytecode level
    @Inject(method = "activate", at = @At("HEAD"), cancellable = true)
    private void cso_activate(MovementContext context, BlockPos pos, DeployerFakePlayer player,
                              @org.spongepowered.asm.mixin.injection.Coerce Object mode, CallbackInfo ci) {
        if (ci.isCancelled()) return;
        if (context.world == null) return;
        java.util.UUID actor = (context.contraption != null && context.contraption.entity != null)
                ? context.contraption.entity.getControllingPlayer().orElse(null) : null;
        if (!CreatePermissionChecker.isAllowed(context.world, pos, actor, CreateMachineType.DEPLOYER))
            ci.cancel();
    }
}
