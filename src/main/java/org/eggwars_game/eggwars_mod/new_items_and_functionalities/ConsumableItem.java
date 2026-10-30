package org.eggwars_game.eggwars_mod.new_items_and_functionalities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public abstract class ConsumableItem extends modItem {

    public ConsumableItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 60;
    }

    @Override
    public TypedActionResult<ItemStack> use(
            World world,
            PlayerEntity user,
            Hand hand
    ) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        return TypedActionResult.consume(stack);
    }

    @Override
    public ItemStack finishUsing(
            ItemStack stack,
            World world,
            LivingEntity user
    ) {
        if (world.isClient())
            return stack;

        onConsumed(stack, world, user);

        if (user instanceof ServerPlayerEntity player
                && !player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return stack;
    }

    protected abstract void onConsumed(
            ItemStack stack,
            World world,
            LivingEntity user
    );
}