package net.whale.SaferBlock.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.whale.SaferBlock.SaferBlock;
import net.whale.SaferBlock.component.ModDataComponentTypes;
import net.whale.SaferBlock.item.custom.SaferBlockItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SaferBlock.MOD_ID);

    public static final RegistryObject<Item> ROTTEN_BLOCK = ITEMS.register("rotten_block", () -> new SaferBlockItem(new Item.Properties().stacksTo(1).component(ModDataComponentTypes.COUNT.get(),0).component(ModDataComponentTypes.BLOCKSTATE.get(), Blocks.AIR.defaultBlockState()),true,1));
    public static final RegistryObject<Item> CURED_BLOCK = ITEMS.register("cured_block", () -> new SaferBlockItem(new Item.Properties().stacksTo(1).component(ModDataComponentTypes.COUNT.get(),0).component(ModDataComponentTypes.BLOCKSTATE.get(), Blocks.AIR.defaultBlockState()).durability(128),false,1));
    public static final RegistryObject<Item> SAFER_BLOCK = ITEMS.register("safer_block", () -> new SaferBlockItem(new Item.Properties().stacksTo(1).component(ModDataComponentTypes.COUNT.get(),0).component(ModDataComponentTypes.BLOCKSTATE.get(), Blocks.AIR.defaultBlockState()).durability(256),false,128));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
