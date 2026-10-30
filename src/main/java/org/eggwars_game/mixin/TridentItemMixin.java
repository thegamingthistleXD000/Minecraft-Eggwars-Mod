package org.eggwars_game.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.simple_item_changes.TemporaryRiptide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {

    @Shadow
    private static boolean isAboutToBreak(ItemStack stack) {
        throw new AssertionError();
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void eggwars$allowTemporaryRiptide(
            World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {

        if (Eggwars.getMatch(user) == null && !NBTUtils.has(user,"eggwars_god")) return;

        ItemStack stack = user.getStackInHand(hand);

        if (isAboutToBreak(stack))
            return;

        if (EnchantmentHelper.getTridentSpinAttackStrength(stack, user) > 0.0F
                && !user.isTouchingWaterOrRain()
                && (!(user instanceof ServerPlayerEntity player)
                    || !TemporaryRiptide.isActive(player))) {
            cir.setReturnValue(TypedActionResult.fail(stack));
            return;
        }

        user.setCurrentHand(hand);
        cir.setReturnValue(TypedActionResult.consume(stack));
    }

    @ModifyExpressionValue(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"
            )
    )
    private boolean eggwars$allowTemporaryRiptide(boolean inWater, ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return inWater || (user instanceof ServerPlayerEntity player
                && TemporaryRiptide.isActive(player));
    }


    @ModifyVariable(
            method = "onStoppedUsing",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private float eggwars$modifyRiptideForce(float f) {
        return f + 5f;
    }
}