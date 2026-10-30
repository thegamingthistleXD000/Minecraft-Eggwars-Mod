package org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;

public interface ModifierItem {
    modifiers.mods getModifierType();

    default ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity player))
            return ActionResult.PASS;

        BlockPos pos = context.getBlockPos();
        EggwarsMatch match = Eggwars.getMatch(player);

        if (match == null ||
                !match.isInTeamControlZone(pos, match.getEggwarsPlayer(player).getTeam()))
            return ActionResult.FAIL;

        if (!placeBlock(context, pos))
            return ActionResult.FAIL;

        onPlaced(player, pos, context.getStack());

        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    boolean placeBlock(ItemUsageContext context, BlockPos pos);

    void onPlaced(ServerPlayerEntity player, BlockPos pos, ItemStack stack);


    default ActionResult useOnEntity(
            ItemStack stack,
            EggwarsPlayer user,
            Entity entity,
            Hand hand
    ) {
        if (!(user.getPlayer() instanceof ServerPlayerEntity player)) {
            return ActionResult.PASS;
        }

        if (!(entity instanceof EndCrystalEntity crystalEntity)) {
            return ActionResult.PASS;
        }

        EggwarsMatch match = Eggwars.getMatch(player);
        if (match == null) return ActionResult.FAIL;


        ControlCrystal crystal;
        try {
            crystal = match.getControlCrystal(crystalEntity);
        } catch (IllegalArgumentException e) {
            return ActionResult.PASS;
        }

        if (crystal == null) return ActionResult.PASS;

        useOnControlCrystal(user, crystal, stack);

        return ActionResult.SUCCESS;
    }

    default void useOnControlCrystal(
            EggwarsPlayer player,
            ControlCrystal crystal,
            ItemStack stack
    ) {


        player.getPlayer().sendMessage(Text.literal("used on cc"));

        EggwarsMatch crysmatch = crystal.getMatch();
        if (crystal.getTeam() != player.getTeam()) {
            player.getPlayer().sendMessage(Text.literal("not ur team !"));
            return;
        }

        onControlCrystal(player, crystal, crystal.getEndCrystal(), stack);
    }

    void onControlCrystal(
            EggwarsPlayer player,
            ControlCrystal crystal,
            EndCrystalEntity crystalEntity,
            ItemStack stack
    );
}


