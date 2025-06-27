package net.whale.SaferBlock.item.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.whale.SaferBlock.SaferBlock;
import net.whale.SaferBlock.component.ModDataComponentTypes;
import net.whale.SaferBlock.item.ModItems;

public class ModItemProperties {
    public static void register() {
        ItemProperties.register(
                ModItems.ROTTEN_BLOCK.get(),
                ResourceLocation.fromNamespaceAndPath(SaferBlock.MOD_ID,"rotten_has_block"),
                (stack, world, entity, seed) -> {
                    Integer count = stack.get(ModDataComponentTypes.COUNT.get());
                    return (count != null && count > 0) ? 1.0F : 0.0F;
                }
        );
        ItemProperties.register(
                ModItems.CURED_BLOCK.get(),
                ResourceLocation.fromNamespaceAndPath(SaferBlock.MOD_ID,"cured_has_block"),
                (stack, world, entity, seed) -> {
                    Integer count = stack.get(ModDataComponentTypes.COUNT.get());
                    return (count != null && count > 0) ? 1.0F : 0.0F;
                }
        );
    }
}
