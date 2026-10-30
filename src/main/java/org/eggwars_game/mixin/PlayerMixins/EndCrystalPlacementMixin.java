package org.eggwars_game.mixin.PlayerMixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;
import org.eggwars_game.eggwars_mod.EggwarsTeam;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.control_point_crystals.SpecialSpot;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.IndestructibleCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.ModifierCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.ResourceCrystal;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.Crystals.SpawnCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(EndCrystalItem.class)
    public class EndCrystalPlacementMixin {

        @Inject(
                method = "useOnBlock",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
                        shift = At.Shift.AFTER
                ),
                locals = LocalCapture.CAPTURE_FAILHARD
        )
        private void eggwars$crystalPlaced(
                ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, World world, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, double d, double e, double f, List list, EndCrystalEntity endCrystalEntity
        ) {

            if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
                return;
            }

            if (!player.getCommandTags().contains("eggwars")) {
                player.sendMessage(Text.literal("no eggwars tag"), false);
                return;
            }

            EggwarsMatch match = Eggwars.getMatch(player);
            Set<SpecialSpot> blockSpots = match.getSpots(blockPos2);

            if (!blockSpots.contains(SpecialSpot.Control)) {
                player.sendMessage(Text.literal("not special spot!"), false);
                return;
            }


            EggwarsTeam team = Eggwars.getEggwarsPlayer(player).getTeam();
            assert team != null;

            player.sendMessage(Text.literal(team.toString()), false);


            ControlCrystal controlcrystal =
                    match.addControlCrystal(
                            endCrystalEntity,
                            team
                    );
            controlcrystal.addComponent(new ModifierCrystal());
            player.sendMessage(Text.literal("added controlcrystal"), false);


            if (blockSpots.contains(SpecialSpot.Iron)||blockSpots.contains(SpecialSpot.Gold)||blockSpots.contains(SpecialSpot.Diamond)||blockSpots.contains(SpecialSpot.Emerald)) {
                controlcrystal.addComponent(new ResourceCrystal(0,0,0,0));
                if (blockSpots.contains(SpecialSpot.Iron)) {
                    controlcrystal.getComponent(ResourceCrystal.class).setIron_rate(2);
                }
                if (blockSpots.contains(SpecialSpot.Gold)) {
                    controlcrystal.getComponent(ResourceCrystal.class).setGold_rate(5);
                }
                if  (blockSpots.contains(SpecialSpot.Diamond)) {
                    controlcrystal.getComponent(ResourceCrystal.class).setDiamond_rate(20);
                }
                if  (blockSpots.contains(SpecialSpot.Emerald)) {
                    controlcrystal.getComponent(ResourceCrystal.class).setEmerald_rate(30);
                }
            }

            if (blockSpots.contains(SpecialSpot.Game)) {
                controlcrystal.addComponent(new IndestructibleCrystal());
            }
            if  (blockSpots.contains(SpecialSpot.Spawn)) {
                controlcrystal.addComponent(new SpawnCrystal());
            }
        }

    }