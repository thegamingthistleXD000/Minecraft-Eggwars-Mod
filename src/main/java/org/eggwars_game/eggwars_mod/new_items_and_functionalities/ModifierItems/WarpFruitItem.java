package org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemUsageContext;

import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.ChorusWarp.ChorusWarp;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ConsumableItem;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks.ModBlocks;


import java.util.UUID;

public class WarpFruitItem extends ConsumableItem implements ModifierItem {

    private static final String LINK_ID = "LinkID";

    public WarpFruitItem(Settings settings) {
        super(settings);
    }

    @Override
    public modifiers.mods getModifierType() {
        return modifiers.mods.ChorusWarp;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity player))
            return ActionResult.PASS;

        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();

        EggwarsMatch match = Eggwars.getMatch(player);
        if (match == null) {
            player.sendMessage(Text.literal("No match"), true);
            return ActionResult.PASS;
        }

        BlockState state = context.getWorld().getBlockState(pos);

        if (!state.isOf(ModBlocks.CHORUS_WARP_BLOCK))
            return ActionResult.PASS;

        ChorusWarp warp = match.warpManager.getModifiers().stream()
                .filter(m -> pos.equals(match.warpManager.getPosition(m)))
                .findFirst()
                .orElse(null);

        if (warp == null) {
            player.sendMessage(
                    Text.literal("No active Chorus Warp exists here."),
                    true
            );
            return ActionResult.FAIL;
        }

        // If the fruit has a stale or existing link, clear it first.
        ChorusWarp linkedWarp = getLinkedWarp(stack, match);

        if (linkedWarp == null && NBTUtils.has(stack, LINK_ID)) {
            NBTUtils.remove(stack, LINK_ID);
        }

        // An unlinked fruit can link to this warp.
        if (!NBTUtils.has(stack, LINK_ID)) {
            NBTUtils.set(stack, LINK_ID, warp.getModifierID().toString());

            player.sendMessage(
                    Text.literal("Warp Fruit linked!"),
                    true
            );
            return ActionResult.SUCCESS;
        }

        // Already linked to a valid warp.
        player.sendMessage(
                Text.literal("Warp Fruit is already linked."),
                true
        );
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean placeBlock(ItemUsageContext context, BlockPos pos) {
        return false;
    }

    @Override
    public void onPlaced(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {

    }

    @Override
    public void onControlCrystal(EggwarsPlayer player, ControlCrystal crystal, EndCrystalEntity crystalEntity, ItemStack stack) {

    }

    @Override
    protected void onConsumed(
            ItemStack stack,
            World world,
            LivingEntity user
    ) {
        if (!(user instanceof ServerPlayerEntity player))
            return;

        EggwarsMatch match = Eggwars.getMatch(player);
        if (match == null)
            return;

        boolean hasLink = NBTUtils.has(stack, LINK_ID);
        ChorusWarp warp = getLinkedWarp(stack, match);

        if (warp == null) {
            if (hasLink) {
                NBTUtils.remove(stack, LINK_ID);

                player.sendMessage(
                        Text.literal("Linked warp no longer exists. Fruit unlinked."),
                        true
                );
            }

            // Your normal chorus-fruit behavior goes here.
            return;
        }

        switch (warp.getMode()) {
            case WARP -> warp.warp(player);
            case REVEAL -> warp.reveal(player);
        }
    }

    private ChorusWarp getLinkedWarp(ItemStack stack, EggwarsMatch match) {
        if (!NBTUtils.has(stack, LINK_ID))
            return null;

        String linkId = NBTUtils.getString(stack, LINK_ID);

        if (linkId == null)
            return null;

        UUID uuid;
        try {
            uuid = UUID.fromString(linkId);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return match.warpManager.getModifiers().stream()
                .filter(warp -> warp.getModifierID().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}