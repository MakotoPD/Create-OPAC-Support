package pl.makoto.createsupportopac.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(targets = "com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket", remap = false)
public class MixinBlockEntityConfigPacketCSO {

    @Shadow protected BlockPos pos;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void cso_handle(ServerPlayer player, CallbackInfo ci) {
        if (ci.isCancelled() || player == null) return;
        if (!CreatePermissionChecker.isAllowed(player.level(), pos, player.getUUID(), CreateMachineType.CONTRAPTION))
            ci.cancel();
    }
}
