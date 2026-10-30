package org.eggwars_game.eggwars_mod.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModItems;

public class GiveModifierCommand {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
                CommandManager.literal("eggwars")
                        .then(CommandManager.literal("modifier")
                                .then(CommandManager.literal("give")
                                        .then(CommandManager.argument(
                                                        "entity",
                                                        EntityArgumentType.entity()
                                                )
                                                .then(CommandManager.argument(
                                                                "modifier",
                                                                StringArgumentType.word()
                                                        )
                                                        .executes(context -> {
                                                            Entity entity =
                                                                    EntityArgumentType.getEntity(context, "entity");

                                                            if (!(entity instanceof ServerPlayerEntity player))
                                                                return 0;

                                                            String modifier =
                                                                    StringArgumentType.getString(context, "modifier");

                                                            modifiers.mods mod;
                                                            ItemStack stack;

                                                            stack = switch (modifier) {
                                                                case "CrystalConnect" -> new ItemStack(ModItems.CRYSTAL_CONNECT);
                                                                case "PearlBarrier" -> new ItemStack(ModItems.PEARL_BARRIER);
                                                                case "ChorusWarp" -> new ItemStack(ModItems.CHORUS_WARP);
                                                                case "ForceField" -> new ItemStack(ModItems.FORCE_FIELD);
                                                                case "EnderSpirit" -> new ItemStack(ModItems.ENDER_SPIRIT);
                                                                default -> {
                                                                    context.getSource().sendError(
                                                                            Text.literal("Unknown modifier: " + modifier)
                                                                    );
                                                                    yield null;
                                                                }
                                                            };

                                                            if (stack == null)
                                                                return 0;

                                                            player.getInventory().insertStack(stack);
                                                            return 1;


                                                        })
                                                )
                                        )
                                )
                        )
        );
    }
}