package org.eggwars_game.mixin.EggwarsEntityMixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsEntity;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SummonCommand.class)
public class SummonCommandMixin {

    @Inject(
            method = "summon",
            at = @At("RETURN")
    )
    private static void eggwars$addSummonedEntity(
            ServerCommandSource source, RegistryEntry.Reference<EntityType<?>> entityType, Vec3d pos, NbtCompound nbt, boolean initialize, CallbackInfoReturnable<Entity> cir
    ) {
        Entity entity = cir.getReturnValue();

        if (entity == null) return;

        Entity executor = source.getEntity();

        EggwarsMatch match = Eggwars.getMatch(executor);
        if (match == null) return;
        EggwarsEntity eggwarsEntity = match.getEggwarsEntity(executor);

        if (eggwarsEntity != null) {
            match.addEntity(entity, eggwarsEntity.getTeam());
        }
    }
}