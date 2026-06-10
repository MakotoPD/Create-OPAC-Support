package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Bypasses OPAC's train protection check for the train owner.
// OPAC's MixinNeoForgeControlsInputPacket does NOT check ci.isCancelled(), so our
// HEAD injection in MixinControlsInputPacketCSO cannot prevent OPAC from sending
// "not allowed" messages. Injecting here returns true (allowed) before OPAC even checks.
@Mixin(targets = "xaero.pac.common.server.core.ServerCore", remap = false)
public class MixinServerCoreCSO {

    @Inject(method = "isCreateTrainControlsPacketAllowed", at = @At("HEAD"), cancellable = true)
    private static void cso_trainControls(int contraptionId, ServerPlayer player,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (isTrainOwnerByEntityId(contraptionId, player))
            cir.setReturnValue(true);
    }

    @Inject(method = "isCreateTrainRelocationPacketAllowed", at = @At("HEAD"), cancellable = true)
    private static void cso_trainRelocation(int entityId, BlockPos pos, ServerPlayer player,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (isTrainOwnerByEntityId(entityId, player))
            cir.setReturnValue(true);
    }

    private static boolean isTrainOwnerByEntityId(int entityId, ServerPlayer player) {
        try {
            Entity entity = player.level().getEntity(entityId);
            if (!(entity instanceof CarriageContraptionEntity carriageEntity)) return false;
            Train train = Create.RAILWAYS.trains.get(carriageEntity.trainId);
            if (train == null || train.owner == null) return false;
            return player.getUUID().equals(train.owner);
        } catch (Exception ignored) {
            return false;
        }
    }
}
