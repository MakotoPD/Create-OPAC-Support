package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(targets = "com.simibubi.create.content.contraptions.glue.SuperGlueRemovalPacket", remap = false)
public class MixinSuperGlueRemovalPacketCSO {

    @Shadow private int entityId;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void cso_handle(ServerPlayer player, CallbackInfo ci) {
        if (ci.isCancelled() || player == null) return;
        Entity entity = player.level().getEntity(entityId);
        if (!(entity instanceof SuperGlueEntity glue)) return;
        if (!CreatePermissionChecker.isAllowed(player.level(), glue.blockPosition(), player.getUUID(), CreateMachineType.SUPERGLUE))
            ci.cancel();
    }
}
