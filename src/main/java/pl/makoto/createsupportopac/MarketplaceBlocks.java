package pl.makoto.createsupportopac;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Identifies Create/Numismatics shop blocks governed by the MARKETPLACE setting. */
public final class MarketplaceBlocks {

    private MarketplaceBlocks() {}

    public static boolean isMarketplaceBlock(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            // A blaze burner acts as the shop checkout only while assigned to a stock ticker
            if (be instanceof BlazeBurnerBlockEntity burner && burner.stockKeeper)
                return true;
            String name = be.getClass().getName();
            if (name.contains("VendorBlockEntity")          // Numismatics vendor
                    || name.contains("TableClothBlockEntity")   // Create table cloth shop
                    || name.contains("DepositorBlockEntity")    // Numismatics andesite/brass depositor
                    || name.contains("BlazeBankerBlockEntity")) // Numismatics blaze banker
                return true;
        }
        // The Numismatics bank terminal has no block entity — match the block class itself
        return level.getBlockState(pos).getBlock().getClass().getName().contains("BankTerminalBlock");
    }
}
