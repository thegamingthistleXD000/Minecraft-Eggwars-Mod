package org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks.ModBlocks;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.modBlockItem;

public class ChorusWarpItem extends modBlockItem implements ModifierItem{


    public ChorusWarpItem(Settings settings) {
        super(ModBlocks.CHORUS_WARP_BLOCK, settings);
    }


    @Override
    public modifiers.mods getModifierType() {
        return null;
    }

    @Override
    public boolean placeBlock(ItemUsageContext context, BlockPos pos) {
        return false;
    }

    @Override
    public void onPlaced(
            ServerPlayerEntity player,
            BlockPos pos,
            ItemStack stack
    ) {
        player.sendMessage(Text.literal("choruswarp placed"), true);

        EggwarsMatch match = Eggwars.getMatch(player);
        match.createChorusWarp(pos,match.getEggwarsPlayer(player));
    }

    @Override
    public void onControlCrystal(EggwarsPlayer player, ControlCrystal crystal, EndCrystalEntity crystalEntity, ItemStack stack) {

    }




}
