package pl.makoto.createsupportopac.mixin;

import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

@Mixin(value = SchematicannonBlockEntity.class, remap = false)
public class MixinSchematicannonCSO {

    @Shadow public SchematicPrinter printer;
    @Shadow public String statusMsg;
    @Shadow private boolean blockSkipped;

    @Inject(method = "tickPrinter",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lcom/simibubi/create/content/schematics/SchematicPrinter;shouldPlaceCurrent(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/schematics/SchematicPrinter$PlacementPredicate;)Z"),
            cancellable = true)
    private void cso_tickPrinter(CallbackInfo ci) {
        if (ci.isCancelled()) return;
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getLevel() == null) return;
        if (!CreatePermissionChecker.isAllowedFromMachine(self.getLevel(), printer.getCurrentTarget(),
                self.getBlockPos(), CreateMachineType.CANNON)) {
            statusMsg = "searching";
            blockSkipped = true;
            ci.cancel();
        }
    }
}
