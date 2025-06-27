package net.whale.SaferBlock.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SaferBlockData extends SavedData {
    private static final String ID = "saferblock_blacklist";
    public Set<ResourceLocation> blacklist = new HashSet<>();
    public Set<ResourceLocation> defaultlist = new HashSet<>();

    public SaferBlockData() {
        addBlacklist(Blocks.BEDROCK);
        addBlacklist(Blocks.OBSIDIAN);
        addBlacklist(Blocks.CRYING_OBSIDIAN);
        addBlacklist(Blocks.END_PORTAL_FRAME);
        addBlacklist(Blocks.PISTON_HEAD);
        addBlacklist(Blocks.MOVING_PISTON);
        addBlacklist(Blocks.END_PORTAL);
        addBlacklist(Blocks.END_GATEWAY);
        addBlacklist(Blocks.NETHER_PORTAL);
        addDefaultlist(Blocks.CAMPFIRE);
        addDefaultlist(Blocks.SOUL_CAMPFIRE);
        addDefaultlist(Blocks.CAKE);
        addDefaultlist(Blocks.CANDLE_CAKE);
    }

    public SaferBlockData(CompoundTag tag, HolderLookup.Provider provider) {
        tag.getList("blacklist", 8).forEach(entry -> {
            blacklist.add(ResourceLocation.parse(entry.getAsString()));
        });
        tag.getList("defaultlist", 8).forEach(entry -> {
            defaultlist.add(ResourceLocation.parse(entry.getAsString()));
        });
    }

    public static final SavedData.Factory<SaferBlockData> FACTORY = new SavedData.Factory<>(
            SaferBlockData::new,
            SaferBlockData::new,
            DataFixTypes.LEVEL
    );

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("blacklist", blacklist.stream()
                .map(loc -> net.minecraft.nbt.StringTag.valueOf(loc.toString()))
                .collect(net.minecraft.nbt.ListTag::new, net.minecraft.nbt.ListTag::add, net.minecraft.nbt.ListTag::addAll));
        tag.put("defaultlist", defaultlist.stream()
                .map(loc -> net.minecraft.nbt.StringTag.valueOf(loc.toString()))
                .collect(net.minecraft.nbt.ListTag::new, net.minecraft.nbt.ListTag::add, net.minecraft.nbt.ListTag::addAll));
        return tag;
    }

    public static SaferBlockData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(FACTORY, ID);
    }
    public boolean isBlacklisted(Block block) {
        return blacklist.contains(BuiltInRegistries.BLOCK.getKey(block));
    }
    public boolean isDefaultlisted(Block block){
        return defaultlist.contains(BuiltInRegistries.BLOCK.getKey(block));
    }
    public boolean addBlacklist(Block block) {
        boolean added = blacklist.add(BuiltInRegistries.BLOCK.getKey(block));
        if (added) setDirty();
        return added;
    }
    public boolean addDefaultlist(Block block){
        boolean added = defaultlist.add(BuiltInRegistries.BLOCK.getKey(block));
        if (added) setDirty();
        return added;
    }
    public boolean removeBlacklist(Block block) {
        boolean removed = blacklist.remove(BuiltInRegistries.BLOCK.getKey(block));
        if (removed) setDirty();
        return removed;
    }
    public boolean removeDefaultlist(Block block){
        boolean removed = defaultlist.remove(BuiltInRegistries.BLOCK.getKey(block));
        if (removed) setDirty();
        return removed;
    }

    public Set<Block> getBlacklistedBlocks() {
        return blacklist.stream()
                .map(BuiltInRegistries.BLOCK::get)
                .collect(Collectors.toSet());
    }
    public Set<Block> getDefaultlistedBlocks(){
        return defaultlist.stream()
                .map(BuiltInRegistries.BLOCK::get)
                .collect(Collectors.toSet());
    }
}