package net.whale.SaferBlock.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.whale.SaferBlock.component.ModDataComponentTypes;
import net.whale.SaferBlock.data.SaferBlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SaferBlockItem extends Item{
    private final boolean consumable;
    private final int maxblocks;
    public SaferBlockItem(Properties properties, boolean consumable, int maxblocks) {
        super(properties);
        this.consumable = consumable;
        this.maxblocks = maxblocks;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        boolean tookblock = false;
        BlockHitResult traceResult = level.clip(new ClipContext(player.getEyePosition(1f),
                (player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f))),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (Objects.requireNonNull(player.getItemInHand(hand).get(ModDataComponentTypes.BLOCKSTATE.get())).equals(level.getBlockState(traceResult.getBlockPos()).getBlock().defaultBlockState()) || player.getItemInHand(hand).get(ModDataComponentTypes.COUNT.get()) <= 0){
            if (traceResult.getType() != HitResult.Type.MISS) {
                tookblock = takeBlockLookedAt(level,player,traceResult,hand);
            }
        }
        if(!tookblock){
            if(placeBlockUnderneathPlayer(player, level, hand) && !level.isClientSide()){
                level.playSound(null,player.blockPosition().below(),player.getItemInHand(hand).get(ModDataComponentTypes.BLOCKSTATE.get()).getSoundType().getPlaceSound(),SoundSource.BLOCKS,0.8f,0.8f+level.getRandom().nextFloat()*0.4f);
                if(player.hasInfiniteMaterials()) return InteractionResultHolder.success(player.getItemInHand(hand));
                player.getItemInHand(hand).hurtAndBreak(1, player.getRandom(),
                        (ServerPlayer) player,
                        () -> {
                            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, player.getItemInHand(hand), LivingEntity.getSlotForHand(hand));
                            if (player.getUseItem() == player.getItemInHand(hand)) {
                                player.stopUsingItem();
                            }
                            ItemStack newItem = new ItemStack(player.getItemInHand(hand).get(ModDataComponentTypes.BLOCKSTATE.get()).getBlock().asItem());
                            newItem.setCount(player.getItemInHand(hand).get(ModDataComponentTypes.COUNT.get()));
                            player.broadcastBreakEvent(LivingEntity.getSlotForHand(hand));
                            player.awardStat(net.minecraft.stats.Stats.ITEM_BROKEN.get(player.getItemInHand(hand).getItem()));
                            if (!player.getInventory().add(newItem)) {
                                player.drop(newItem, false);
                            } else {
                                level.playSound(
                                        null,
                                        player.getX(), player.getY() + 0.5, player.getZ(),
                                        SoundEvents.ITEM_PICKUP,
                                        SoundSource.PLAYERS,
                                        0.2F,
                                        ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                            }
                            player.getItemInHand(hand).shrink(1);
                            player.getItemInHand(hand).setDamageValue(0);

                            level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK,
                                    SoundSource.PLAYERS, 0.8f, 0.8f + level.getRandom().nextFloat() * 0.4f);
                        });
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private boolean placeBlockUnderneathPlayer(Player player,Level level,InteractionHand hand) {
        if(player.getItemInHand(hand).get(ModDataComponentTypes.COUNT.get()) <= 0)return false;
        Vec3 playerPos = player.position();
        BlockPos blockBelow = BlockPos.containing(playerPos).below();
        BlockState state = player.getItemInHand(hand).get(ModDataComponentTypes.BLOCKSTATE.get());
        if(state.hasProperty(BlockStateProperties.FACING) && !state.getBlock().equals(Blocks.BIG_DRIPLEAF)){
            state = state.setValue(BlockStateProperties.FACING,player.getDirection());
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && !state.getBlock().equals(Blocks.BIG_DRIPLEAF)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.fromYRot(player.getYRot()));
        }
        if (level.getBlockState(blockBelow).canBeReplaced()) {
            player.getItemInHand(hand).set(ModDataComponentTypes.COUNT.get(),(player.getItemInHand(hand).get(ModDataComponentTypes.COUNT.get())-1));
            level.setBlockAndUpdate(blockBelow, state);
            level.playSound(
                    null,
                    blockBelow,
                    SoundEvents.BONE_BLOCK_PLACE, // Oder state.getSoundType().getPlaceSound()
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F);
            if (!state.canSurvive(level, blockBelow)) {
                level.destroyBlock(blockBelow, true);
            }
            if(consumable){
                player.getItemInHand(hand).consume(1,player);
            }else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return !this.consumable;
    }

    private boolean takeBlockLookedAt(Level level, Player player, BlockHitResult hitResult, InteractionHand hand) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(blockPos);
        ItemStack item = player.getItemInHand(hand);
        if (!(item.get(ModDataComponentTypes.COUNT.get()) >= maxblocks) && !level.isClientSide()) {
            if(isForbidden(state,level.getBlockEntity(blockPos), (ServerLevel) level)) return false;
            item.set(ModDataComponentTypes.COUNT.get(),(item.get(ModDataComponentTypes.COUNT.get())+1));
            item.set(ModDataComponentTypes.BLOCKSTATE.get(),state.getBlock().defaultBlockState());
            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            level.playSound(
                    null,
                    blockPos,
                    SoundEvents.AXE_STRIP,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            return true;
        }
        return false;
    }

    public static boolean isForbidden(BlockState state, BlockEntity blockEntity,ServerLevel level) {
        Block block = state.getBlock();
        if(state.canBeReplaced())return true;
        if (block instanceof BedBlock || block instanceof DoorBlock) return true;
        if(SaferBlockData.get(level).isDefaultlisted(block) && state != block.defaultBlockState()) return true;
        if(state.getBlock() instanceof ShulkerBoxBlock && blockEntity instanceof ShulkerBoxBlockEntity entity&& !entity.isEmpty()) return true;
        if (SaferBlockData.get(level).isBlacklisted(block)) return true;
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        Integer count = stack.get(ModDataComponentTypes.COUNT.get());
        BlockState state = stack.get(ModDataComponentTypes.BLOCKSTATE.get());
        Component blockName = state.getBlock().getName();
        if (count > 1) {
            tooltip.add(Component.literal("Contents: " + count + " ").append(blockName).withStyle(ChatFormatting.GRAY));
        } else if(count==1){
            tooltip.add(Component.literal("Contents: ").append(blockName).withStyle(ChatFormatting.GRAY));
        }
    }
}
