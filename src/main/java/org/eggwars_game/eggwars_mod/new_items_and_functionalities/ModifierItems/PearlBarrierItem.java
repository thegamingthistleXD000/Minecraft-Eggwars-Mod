package org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.modItem;

public class PearlBarrierItem extends modItem implements ModifierItem {

    public PearlBarrierItem(Settings settings) {
        super(settings);
    }

    @Override
    public modifiers.mods getModifierType() {
        return null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public boolean placeBlock(ItemUsageContext context, BlockPos pos) {
        return false;
    }

    @Override
    public void onPlaced(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {

    }


    @Override
    public void onControlCrystal(
            EggwarsPlayer player,
            ControlCrystal crystal,
            EndCrystalEntity crystalEntity,
            ItemStack stack
    ) {
        EggwarsMatch match = player.getMatch();


        player.getPlayer().sendMessage(Text.literal("on control crystal boi"));

        if (match == null) {
            System.out.println("[Eggwars] match is null cant do pearlbarrier");

            return;
        }


        match.createPearlBarrier(
                crystal,
                player
        );

        if (!player.getPlayer().isCreative()) {
            stack.decrement(1);
        }
    }
}