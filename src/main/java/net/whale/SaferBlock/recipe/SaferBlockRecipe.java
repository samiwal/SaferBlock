package net.whale.SaferBlock.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.whale.SaferBlock.component.ModDataComponentTypes;
import net.whale.SaferBlock.item.ModItems;
import net.whale.SaferBlock.item.custom.SaferBlockItem;

import java.util.Optional;

public class SaferBlockRecipe extends CustomRecipe {
    public SaferBlockRecipe(CraftingBookCategory p_249010_) {
        super(p_249010_);
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        boolean possible = false;
        for (int i = 0; i < pInv.getContainerSize(); i++) {
            if(!pInv.getItem(i).isEmpty()) {
                ItemStack itemstack = pInv.getItem(i);
                if(itemstack.getItem() instanceof SaferBlockItem){
                    if(possible){
                        return false;
                    }
                    possible = true;
                } else if (!(itemstack.getItem() instanceof BlockItem)) {
                    return false;
                }
            }
        }
        return possible;
    }

    @Override
    public ItemStack assemble(CraftingContainer pInv, HolderLookup.Provider p_332698_) {
        ItemStack blockstack = ItemStack.EMPTY;
        ItemStack saferstack = ItemStack.EMPTY;
        int count = 0;
        for (int i = 0; i < pInv.getContainerSize(); i++) {
            if(!pInv.getItem(i).isEmpty()) {
                ItemStack itemstack = pInv.getItem(i);
                if(itemstack.getItem() instanceof BlockItem){
                    if(!(blockstack.getItem() == itemstack.getItem() || count == 0)) return ItemStack.EMPTY;
                    blockstack = itemstack.copy();
                    if(blockstack.has(DataComponents.CONTAINER) && blockstack.get(DataComponents.CONTAINER) != ItemContainerContents.EMPTY) return ItemStack.EMPTY;
                    count++;
                }else if(itemstack.getItem() instanceof SaferBlockItem){
                    saferstack = itemstack.copy();
                }
                else return ItemStack.EMPTY;
            }
        }
        if (count == 0 || saferstack == ItemStack.EMPTY || blockstack == ItemStack.EMPTY)return ItemStack.EMPTY;
        if(saferstack.get(ModDataComponentTypes.BLOCKSTATE.get()) == ((BlockItem) blockstack.getItem()).getBlock().defaultBlockState() || saferstack.get(ModDataComponentTypes.COUNT.get()) == 0){
            saferstack.set(ModDataComponentTypes.BLOCKSTATE.get(),((BlockItem) blockstack.getItem()).getBlock().defaultBlockState());
            saferstack.set(ModDataComponentTypes.COUNT.get(),saferstack.get(ModDataComponentTypes.COUNT.get())+count);
            if(saferstack.get(ModDataComponentTypes.COUNT.get()) > 1 && (saferstack.get(ModDataComponentTypes.COUNT.get()) >128 || saferstack.getItem() != ModItems.SAFER_BLOCK.get())) return ItemStack.EMPTY;
            return saferstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return p_43999_>=2 && p_44000_>=2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SAFER_BLOCK_EXTENDED_RECIPE.get();
    }
}
