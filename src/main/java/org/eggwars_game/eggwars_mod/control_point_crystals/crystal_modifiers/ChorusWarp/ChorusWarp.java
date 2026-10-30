package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.ChorusWarp;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.Modifier;
import org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers.modifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import java.util.List;


public class ChorusWarp extends Modifier {

    @Override
    public void tick() {

    }

    public enum Mode {
        WARP,
        REVEAL
    }

    // CONSTANTS
    private static final int WARP_COOLDOWN = 1;
    private static final int REVEAL_COOLDOWN = 2;

    private static final int REVEAL_DURATION = 60; // 3 seconds

    
    // MODIFIER DATA

    private float range;
    private float revealrange;

    private Mode mode;

    @Override
    public void register(EggwarsMatch match, BlockPos pos) {
        match.modManager.addModifier(() -> this, pos);
        match.warpManager.addModifier(() -> this, pos);
    }

    public ChorusWarp(BlockPos position,  EggwarsPlayer player) {
        super(position, modifiers.mods.ChorusWarp,  player);
        this.mode = Mode.WARP;
    }

    // MODE
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    public Mode getMode() {
        return mode;
    }

    // WARP
    public void warp(
            ServerPlayerEntity player
    ) {

        BlockPos destination = position;

        ServerWorld world = player.getServerWorld();

        double x = destination.getX() + 0.5;
        double y = destination.getY() + 1.0;
        double z = destination.getZ() + 0.5;

        player.teleport(
                world,
                x,
                y,
                z,
                player.getYaw(),
                player.getPitch()
        );
    }

    //* WARP OBSTRUCTION CHECK
    public boolean canWarp(
            ServerPlayerEntity player
    ) {

        ServerWorld world = player.getServerWorld();
        BlockPos destination = position;

        BlockPos oneAbove = destination.up();
        BlockPos twoAbove = destination.up(2);

        return world.getBlockState(oneAbove).isAir()
                && world.getBlockState(twoAbove).isAir();
    }

    public void reveal(
            ServerPlayerEntity player
    ) {

        ServerWorld world = player.getServerWorld();
        Vec3d center = Vec3d.ofCenter(position);

        List<ServerPlayerEntity> players =
                world.getPlayers(
                        p -> p != player
                                && !p.isSpectator()
                                && p.squaredDistanceTo(center)
                                <= revealrange*revealrange
                );

        // Reveal enemies.
        Team playerTeam = player.getScoreboardTeam();

        for (ServerPlayerEntity target : players) {
            Team targetTeam = target.getScoreboardTeam();
            if (!playerTeam.equals(targetTeam)) {

                target.addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.GLOWING,
                                REVEAL_DURATION,
                                0,
                                false,
                                false,
                                true
                        )
                );
            }
        }
    }


    public boolean inRange(ServerPlayerEntity player) {
        return !(position.getSquaredDistance(player.getPos()) > range * range);
    }





}








