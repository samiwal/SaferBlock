package net.whale.SaferBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.whale.SaferBlock.command.ModCommands;
import net.whale.SaferBlock.component.ModDataComponentTypes;
import net.whale.SaferBlock.data.SaferBlockData;
import net.whale.SaferBlock.item.ModItems;
import net.whale.SaferBlock.item.custom.SaferBlockItem;
import net.whale.SaferBlock.item.util.ModItemProperties;
import net.whale.SaferBlock.recipe.ModRecipeSerializers;

@Mod(SaferBlock.MOD_ID)
public class SaferBlock
{
    public static final String MOD_ID = "saferblock";

    public SaferBlock(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        ModRecipeSerializers.register(modEventBus);
        ModDataComponentTypes.register(modEventBus);
        ModItems.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModItemProperties.register();
            DispenserBlock.registerBehavior(ModItems.ROTTEN_BLOCK.get(), new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    boolean tookblock = false;
                    Level level = source.level();
                    Direction direction = source.state().getValue(DispenserBlock.FACING);
                    BlockPos dispenserPos = source.pos();
                    BlockPos targetPos = dispenserPos.relative(direction);
                    if(stack.get(ModDataComponentTypes.BLOCKSTATE.get()).equals(level.getBlockState(targetPos).getBlock().defaultBlockState()) || stack.get(ModDataComponentTypes.COUNT.get())<=0){
                        tookblock = takeBlockLookedAt(stack,level,targetPos,1);
                    }
                    if (!tookblock) {
                        placeBlockBeforeDispenser(stack,level,targetPos,true,direction);
                    }
                    return stack;
                }
            });
            DispenserBlock.registerBehavior(ModItems.CURED_BLOCK.get(), new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    boolean tookblock = false;
                    Level level = source.level();
                    Direction direction = source.state().getValue(DispenserBlock.FACING);
                    BlockPos dispenserPos = source.pos();
                    BlockPos targetPos = dispenserPos.relative(direction);
                    if(stack.get(ModDataComponentTypes.BLOCKSTATE.get()).equals(level.getBlockState(targetPos).getBlock().defaultBlockState()) || stack.get(ModDataComponentTypes.COUNT.get())<=0){
                        tookblock = takeBlockLookedAt(stack,level,targetPos,1);
                    }
                    if (!tookblock) {
                        placeBlockBeforeDispenser(stack,level,targetPos,false,direction);
                    }
                    return stack;
                }
            });
            DispenserBlock.registerBehavior(ModItems.SAFER_BLOCK.get(), new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    boolean tookblock = false;
                    Level level = source.level();
                    Direction direction = source.state().getValue(DispenserBlock.FACING);
                    BlockPos dispenserPos = source.pos();
                    BlockPos targetPos = dispenserPos.relative(direction);
                    if(stack.get(ModDataComponentTypes.BLOCKSTATE.get()).equals(level.getBlockState(targetPos).getBlock().defaultBlockState()) || stack.get(ModDataComponentTypes.COUNT.get())<=0){
                        tookblock = takeBlockLookedAt(stack,level,targetPos,128);
                    }
                    if (!tookblock) {
                        placeBlockBeforeDispenser(stack,level,targetPos,false,direction);
                    }
                    return stack;
                }
            });
        }
    }
    private static boolean takeBlockLookedAt(ItemStack stack, Level level, BlockPos targetpos, int i) {
        if(level.isClientSide)return false;
        if(stack.get(ModDataComponentTypes.COUNT.get()) >= i) return false;
        if(SaferBlockItem.isForbidden(level.getBlockState(targetpos),level.getBlockEntity(targetpos),((ServerLevel) level))) return false;
        stack.set(ModDataComponentTypes.COUNT.get(), stack.get(ModDataComponentTypes.COUNT.get()) + 1);
        stack.set(ModDataComponentTypes.BLOCKSTATE.get(), level.getBlockState(targetpos).getBlock().defaultBlockState());
        level.setBlockAndUpdate(targetpos, Blocks.AIR.defaultBlockState());
        return true;
    }
    private static void placeBlockBeforeDispenser(ItemStack stack, Level level, BlockPos beforepos, boolean consumeable,Direction direction){
        BlockState state = stack.get(ModDataComponentTypes.BLOCKSTATE.get());
        if(state.hasProperty(BlockStateProperties.FACING) && !state.getBlock().equals(Blocks.BIG_DRIPLEAF)){
            state = state.setValue(BlockStateProperties.FACING,direction);
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && !state.getBlock().equals(Blocks.BIG_DRIPLEAF)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING,direction);
        }
        if(level.getBlockState(beforepos).canBeReplaced() && stack.get(ModDataComponentTypes.COUNT.get())>=1){
            stack.set(ModDataComponentTypes.COUNT.get(), stack.get(ModDataComponentTypes.COUNT.get()) - 1);
            level.setBlockAndUpdate(beforepos, state);
            if(consumeable)stack.shrink(1);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS || event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){
            event.accept(ModItems.ROTTEN_BLOCK);
            event.accept(ModItems.CURED_BLOCK);
            event.accept(ModItems.SAFER_BLOCK);
        }
    }
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MOD_ID)
    public static class CommonForgeEvents {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            ModCommands.register(event.getDispatcher(),event.getBuildContext());
        }
    }
}
