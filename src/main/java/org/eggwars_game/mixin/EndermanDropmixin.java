package org.eggwars_game.mixin;  

import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ItemEntity;

@Mixin(LivingEntity.class)
public class EndermanDropmixin {

    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("TAIL"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity le = (LivingEntity)(Object)this;
        if (!(le instanceof EndermanEntity enderman)) return;
        if (enderman.getWorld() == null || enderman.getWorld().isClient) return;

        int count = enderman.getRandom().nextInt(3); // 0-2
        for (int i = 0; i < count; i++) {
            ItemStack stack = new ItemStack(ModItems.ENDER_SHARD);
            ItemEntity it = new ItemEntity(enderman.getWorld(), enderman.getX(), enderman.getY() + 0.5, enderman.getZ(), stack);
            enderman.getWorld().spawnEntity(it);
        }
    }
}
