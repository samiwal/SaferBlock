package net.whale.SaferBlock.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.whale.SaferBlock.SaferBlock;

import static net.minecraftforge.registries.ForgeRegistries.RECIPE_SERIALIZERS;

public class ModRecipeSerializers<T extends Recipe<?>> {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(RECIPE_SERIALIZERS, SaferBlock.MOD_ID);
    public static final RegistryObject<RecipeSerializer<SaferBlockRecipe>> SAFER_BLOCK_EXTENDED_RECIPE =
            SERIALIZERS.register("safer_block_extended", () -> new SimpleCraftingRecipeSerializer<>(SaferBlockRecipe::new));
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
