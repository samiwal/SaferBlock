package net.whale.SaferBlock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.whale.SaferBlock.data.SaferBlockData;

import java.util.Set;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("saferblock")
                .requires(source -> source.hasPermission(0))
                .then(Commands.literal("blacklist")
                        .then(Commands.literal("add")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("block", BlockStateArgument.block(ctx))
                                        .suggests((context, builder) -> {
                                            ServerLevel level = context.getSource().getLevel();
                                            Set<Block> current = SaferBlockData.get(level).getBlacklistedBlocks();
                                            BuiltInRegistries.BLOCK.entrySet().stream()
                                                    .filter(e -> !current.contains(e.getValue()))
                                                    .forEach(e -> builder.suggest(e.getKey().location().toString()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ModCommands::runAddBlacklist)
                                )
                        )
                        .then(Commands.literal("remove")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("block", BlockStateArgument.block(ctx))
                                        .suggests((context, builder) -> {
                                            ServerLevel level = context.getSource().getLevel();
                                            SaferBlockData.get(level).blacklist.forEach(loc -> builder.suggest(loc.toString()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ModCommands::runRemoveBlacklist)
                                )
                        )
                        .then(Commands.literal("list")
                                .executes(ModCommands::runBlacklist)
                        )
                )
                .then(Commands.literal("defaultlist")
                        .then(Commands.literal("add")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("block", BlockStateArgument.block(ctx))
                                        .suggests((context, builder) -> {
                                            ServerLevel level = context.getSource().getLevel();
                                            Set<Block> current = SaferBlockData.get(level).getDefaultlistedBlocks();
                                            BuiltInRegistries.BLOCK.entrySet().stream()
                                                    .filter(e -> !current.contains(e.getValue()))
                                                    .forEach(e -> builder.suggest(e.getKey().location().toString()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ModCommands::runAddDefaultlist)
                                )
                        )
                        .then(Commands.literal("remove")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("block", BlockStateArgument.block(ctx))
                                        .suggests((context, builder) -> {
                                            ServerLevel level = context.getSource().getLevel();
                                            SaferBlockData.get(level).defaultlist.forEach(loc -> builder.suggest(loc.toString()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ModCommands::runRemoveDefaultlist)
                                )
                        )
                        .then(Commands.literal("list")
                                .executes(ModCommands::runDefaultlist)
                        )
                )
        );
    }

    private static int runAddBlacklist(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Block block = BlockStateArgument.getBlock(context, "block").getState().getBlock();
        boolean added = SaferBlockData.get(level).addBlacklist(block);
        context.getSource().sendSuccess(() -> Component.literal((added ? "Added " : "Already present: ") + BuiltInRegistries.BLOCK.getKey(block)), true);
        return added ? 1 : 0;
    }

    private static int runRemoveBlacklist(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Block block = BlockStateArgument.getBlock(context, "block").getState().getBlock();
        boolean removed = SaferBlockData.get(level).removeBlacklist(block);
        context.getSource().sendSuccess(() -> Component.literal((removed ? "Removed " : "Not found: ") + BuiltInRegistries.BLOCK.getKey(block)), true);
        return removed ? 1 : 0;
    }

    private static int runBlacklist(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Set<Block> blocks = SaferBlockData.get(level).getBlacklistedBlocks();
        context.getSource().sendSuccess(() -> Component.literal("Blacklisted blocks (" + blocks.size() + "):"), false);
        blocks.forEach(block ->
                context.getSource().sendSuccess(() -> Component.literal(" - " + BuiltInRegistries.BLOCK.getKey(block)), false));
        return blocks.size();
    }
    private static int runAddDefaultlist(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Block block = BlockStateArgument.getBlock(context, "block").getState().getBlock();
        boolean added = SaferBlockData.get(level).addDefaultlist(block);
        context.getSource().sendSuccess(() -> Component.literal((added ? "Added " : "Already present: ") + BuiltInRegistries.BLOCK.getKey(block)), true);
        return added ? 1 : 0;
    }

    private static int runRemoveDefaultlist(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Block block = BlockStateArgument.getBlock(context, "block").getState().getBlock();
        boolean removed = SaferBlockData.get(level).removeDefaultlist(block);
        context.getSource().sendSuccess(() -> Component.literal((removed ? "Removed " : "Not found: ") + BuiltInRegistries.BLOCK.getKey(block)), true);
        return removed ? 1 : 0;
    }

    private static int runDefaultlist(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Set<Block> blocks = SaferBlockData.get(level).getDefaultlistedBlocks();
        context.getSource().sendSuccess(() -> Component.literal("Defaultlisted blocks (" + blocks.size() + "):"), false);
        blocks.forEach(block ->
                context.getSource().sendSuccess(() -> Component.literal(" - " + BuiltInRegistries.BLOCK.getKey(block)), false));
        return blocks.size();
    }
}
