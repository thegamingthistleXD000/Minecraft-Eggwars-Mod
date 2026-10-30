package org.eggwars_game.eggwars_mod.new_items_and_functionalities;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems.ModifierItem;


public abstract class modBlockItem extends BlockItem {

    public modBlockItem(Block block, Settings settings) {
        super(block, settings);
    }



    @Override
    public ActionResult place(ItemPlacementContext context) {
        if (context.getPlayer() instanceof ServerPlayerEntity player) {
            EggwarsMatch match = Eggwars.getMatch(player);

            if (match == null) {
                player.sendMessage(Text.literal("no match"), true);

                return ActionResult.FAIL;
            }


            BlockPos pos = context.getBlockPos();

            if (!match.isInTeamControlZone(
                    pos,
                    match.getEggwarsPlayer(player).getTeam()
            )&& this instanceof ModifierItem) {
                player.sendMessage(Text.literal("not in zone"), true);

                return ActionResult.FAIL;
            }
        }

        ActionResult result = super.place(context);

        if (!result.isAccepted()) {
            return result;
        }

        if (context.getPlayer() instanceof ServerPlayerEntity player
                && this instanceof ModifierItem modifierItem) {
            modifierItem.onPlaced(
                    player,
                    context.getBlockPos(),
                    context.getStack()
            );
        }

        return result;
    }
}