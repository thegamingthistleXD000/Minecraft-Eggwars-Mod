package org.eggwars_game.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class VanillaFireworkMixin {

    @Shadow public abstract TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);

    private static void eggwars$launchRocket(PlayerEntity player, ItemStack stack) {
        Vec3d look = player.getRotationVec(1.0F);

        FireworkRocketEntity rocket = new FireworkRocketEntity(
                player.getWorld(),
                stack.copy(),
                player
        );

        rocket.setVelocity(
                look.x,
                look.y,
                look.z,
                1.5F,
                0.0F
        );

        player.getWorld().spawnEntity(rocket);

        float force = 1.5f;

        player.addVelocity(
                -look.x * force,
                -look.y * force,
                -look.z * force
        );

        player.velocityModified = true;
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void eggwars$rocketUse(
            World world,
            PlayerEntity user,
            Hand hand,
            CallbackInfoReturnable<TypedActionResult<ItemStack>> cir
    ) {
        if (user.isFallFlying()) return;

        ItemStack stack = user.getStackInHand(hand);

        eggwars$launchRocket(user, stack);

        stack.decrementUnlessCreative(1, user);
        user.incrementStat(Stats.USED.getOrCreateStat(Items.FIREWORK_ROCKET));

        if (user.getItemCooldownManager().isCoolingDown(Items.FIREWORK_ROCKET)) {
            cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
            return;
        }

        if (user.isFallFlying()) return;

        user.getItemCooldownManager().set(Items.FIREWORK_ROCKET, 10);
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void eggwars$rocketUseOnBlock(
            ItemUsageContext context,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        PlayerEntity user = context.getPlayer();
        ItemStack stack = context.getStack();

        if (user == null || user.isFallFlying()) {
            return;
        }

        eggwars$launchRocket(user, stack);

        if (user.getItemCooldownManager().isCoolingDown(Items.FIREWORK_ROCKET)) {
            cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(context.getHand())).getResult());
            return;
        }

        if (user.isFallFlying()) return;

        user.getItemCooldownManager().set(Items.FIREWORK_ROCKET, 10);
    }
}