package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public class MixinHarvesterBehaviourCSO {

    @Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true)
    private void cso_visitNewPosition(MovementContext context, BlockPos pos, CallbackInfo ci) {
        if (ci.isCancelled()) return;
        if (context.world == null) return;
        java.util.UUID actor = (context.contraption != null && context.contraption.entity != null)
                ? context.contraption.entity.getControllingPlayer().orElse(null) : null;
        if (!CreatePermissionChecker.isAllowed(context.world, pos, actor, CreateMachineType.HARVESTER))
            ci.cancel();
    }
}
