package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items.ModifierItems;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Items.modItem;

import java.util.UUID;

public class CrystalConnectItem extends modItem implements ModifierItem {

    public CrystalConnectItem(Settings settings) {
        super(settings);
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
    public void onPlaced(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {

    }

    @Override
    public void onControlCrystal(
            EggwarsPlayer player,
            ControlCrystal crystal,
            EndCrystalEntity crystalEntity,
            ItemStack stack
    ) {
        String firstID = NBTUtils.getString(stack, "FirstCrystal");

        if (firstID == null || firstID.isEmpty()) {
            NBTUtils.set(
                    stack,
                    "FirstCrystal",
                    crystalEntity.getUuidAsString()
            );

            player.getPlayer().sendMessage(
                    Text.literal("Crystal 1 selected"),
                    false
            );

            return;
        }

        EggwarsMatch match = player.getMatch();

        ServerPlayerEntity serverPlayerEntity = player.getServerPlayer();
        if (serverPlayerEntity == null) return;

        ServerWorld world = serverPlayerEntity.getServerWorld();
        ControlCrystal sender = match.getControlCrystal((EndCrystalEntity) world.getEntity(UUID.fromString(firstID)));

        if (sender == null)
            return;

        match.createCrystalConnect(player, crystal, sender );


        NBTUtils.remove(stack, "FirstCrystal");
        stack.decrement(1);
    }
}