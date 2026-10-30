package org.eggwars_game.mixin.EggwarsEntityMixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {

    @Inject(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;spawnFromItemStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;",
                    shift = At.Shift.AFTER
            )
    )
    private void eggwars$trackSpawnedEntity(
            ItemUsageContext context,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        if (!(context.getWorld() instanceof ServerWorld world)) return;

        EggwarsMatch match = Eggwars.getMatch(context.getPlayer());
        if (match == null) return;
        EggwarsPlayer eggwarsPlayer = match.getEggwarsPlayer((ServerPlayerEntity) context.getPlayer());
        if (eggwarsPlayer == null) return;
        
    }

    @ModifyExpressionValue(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;spawnFromItemStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity eggwars$trackSpawnedEntity(Entity entity, ItemUsageContext context) {
        if (entity != null && context.getPlayer() instanceof ServerPlayerEntity player) {
            EggwarsMatch match = Eggwars.getMatch(player);

            EggwarsMatch.addSpawnedEntity(entity, player);


        }

        return entity;
    }

    @ModifyExpressionValue(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;spawnFromItemStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity eggwars$trackWaterSpawn(Entity entity, World world, PlayerEntity user, Hand hand) {
        if (entity != null && user instanceof ServerPlayerEntity player) {
            EggwarsMatch.addSpawnedEntity(entity, player);
        }

        return entity;
    }

}