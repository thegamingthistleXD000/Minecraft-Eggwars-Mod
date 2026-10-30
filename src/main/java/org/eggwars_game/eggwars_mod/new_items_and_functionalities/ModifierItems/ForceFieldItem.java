package org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems;

import net.minecraft.block.Block;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks.ModBlocks;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.modBlockItem;

public class ForceFieldItem extends modBlockItem implements ModifierItem {

    public ForceFieldItem(Settings settings) {
        super(ModBlocks.FORCE_FIELD_BLOCK, settings);

    }



    @Override
    public Block getBlock() {
        return ModBlocks.FORCE_FIELD_BLOCK;
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
        EggwarsMatch match = Eggwars.getMatch(player);
        if (match == null) return;

        Direction facing = player.getWorld()
                .getBlockState(pos)
                .get(Properties.FACING);

        match.createForceField(
                pos,
                match.getEggwarsPlayer(player),
                facing
        );
    }

    @Override
    public void onControlCrystal(EggwarsPlayer player, ControlCrystal crystal, EndCrystalEntity crystalEntity, ItemStack stack) {

    }


}
