package pl.makoto.createsupportopac.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.makoto.createsupportopac.permission.CreatePermissionChecker;
import pl.makoto.createsupportopac.settings.CreateMachineType;

// Prevents OPAC (HIGHEST priority) from blocking marketplace interactions when MARKETPLACE is allowed.
// OPAC would cancel the event and show "not allowed" before our HIGH-priority handler could run.
@Mixin(targets = "xaero.pac.common.event.CommonEventsNeoForge", remap = false)
public class MixinCommonEventsNeoForgeCSO {

    @Inject(method = "onRightClickBlock", at = @At("HEAD"), cancellable = true)
    private void cso_onRightClickBlock(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BlockEntity be = level.getBlockEntity(event.getPos());
        if (be == null) return;
        String name = be.getClass().getName();
        if (!name.contains("VendorBlockEntity") && !name.contains("TableClothBlockEntity") && !name.contains("BlazeBurnerBlockEntity")) return;
        if (CreatePermissionChecker.isAllowed(level, event.getPos(), player.getUUID(), CreateMachineType.MARKETPLACE))
            ci.cancel();
    }

    // Handles right-click on entity — OPAC blocks when player holds an item ("empty hand required").
    // We bypass OPAC when the player is holding a Create ShoppingListItem (marketplace payment).
    @Inject(method = "onEntityInteract", at = @At("HEAD"), cancellable = true)
    private void cso_onEntityInteract(PlayerInteractEvent.EntityInteract event, CallbackInfo ci) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!isHoldingShoppingList(player, event.getHand())) return;
        if (CreatePermissionChecker.isAllowed(level, event.getTarget().blockPosition(), player.getUUID(), CreateMachineType.MARKETPLACE))
            ci.cancel();
    }

    // OPAC fires a second handler (onInteractEntitySpecific) when a player holds an item and
    // right-clicks an entity — this produces the "try again empty-handed" message.
    @Inject(method = "onInteractEntitySpecific", at = @At("HEAD"), cancellable = true)
    private void cso_onInteractEntitySpecific(PlayerInteractEvent.EntityInteractSpecific event, CallbackInfo ci) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!isHoldingShoppingList(player, event.getHand())) return;
        if (CreatePermissionChecker.isAllowed(level, event.getTarget().blockPosition(), player.getUUID(), CreateMachineType.MARKETPLACE))
            ci.cancel();
    }

    private static boolean isHoldingShoppingList(ServerPlayer player, InteractionHand hand) {
        return player.getItemInHand(hand).getItem().getClass().getName().contains("ShoppingListItem");
    }
}
