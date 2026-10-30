
package org.eggwars_game.mixin.PlayerMixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.EggwarsMatch;

import org.eggwars_game.eggwars_mod.Players.EggwarsPlayer;
import org.eggwars_game.eggwars_mod.Utils.NBTUtils;
import org.eggwars_game.eggwars_mod.control_point_crystals.controlcrystals.ControlCrystal;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems.ModifierItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ModifierEntityInteractionMixin {

    @Redirect(
            method = "onPlayerInteractEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;handle(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$Handler;)V"
            )
    )
    private void eggwars$handleModifierInteraction(
            PlayerInteractEntityC2SPacket packet,
            PlayerInteractEntityC2SPacket.Handler originalHandler
    ) {
        System.out.println("[Eggwars] Mixin TRIGGERED!");

        ServerPlayNetworkHandler networkHandler =
                (ServerPlayNetworkHandler) (Object) this;

        packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
            @Override
            public void attack() {
                originalHandler.attack();
            }

            @Override
            public void interact(Hand hand) {
                System.out.println("[Eggwars] Entity interact callback: " + hand);

                if (!eggwars$useModifier(packet, hand, networkHandler)) {

                    originalHandler.interact(hand);
                }
            }
            
            @Override
            public void interactAt(Hand hand, Vec3d hitPos) {
                System.out.println("[Eggwars] Entity interactAt callback: " + hand);

                if (!eggwars$useModifier(packet, hand, networkHandler)) {
                    originalHandler.interactAt(hand, hitPos);
                }
            }
        });
    }
    private boolean eggwars$useModifier(
            PlayerInteractEntityC2SPacket packet,
            Hand hand,
            ServerPlayNetworkHandler handler
    ) {
        ServerPlayerEntity player = handler.player;
        Entity entity = packet.getEntity(player.getServerWorld());

        if (!(entity instanceof EndCrystalEntity crystalEntity)) {
            System.out.println("[Eggwars] not endcrys");

            return false;
        }

        ItemStack stack = player.getStackInHand(hand);

        if (!(stack.getItem() instanceof ModifierItem modifierItem)) {
            System.out.println("[Eggwars] not modifier");
            System.out.println("[Eggwars] Item: "
                    + Registries.ITEM.getId(stack.getItem()));

            System.out.println("[Eggwars] Class: "
                    + stack.getItem().getClass().getName());

            System.out.println("[Eggwars] Implements interface: "
                    + (stack.getItem() instanceof ModifierItem));

            return false;
        }

        EggwarsMatch match = Eggwars.getMatch(player);
        if (match == null) {
            System.out.println("[Eggwars] no match for placing onto crystal");

            return false;
        }
        

        ControlCrystal crystal;
        try {
            crystal = match.getControlCrystal(crystalEntity);
        } catch (IllegalArgumentException e) {
            System.out.println("[Eggwars] Invalid control crystal");
            return false;
        }

        if (crystal == null) {
            System.out.println("[Eggwars] null  control crystal");

            return false;
        }

        EggwarsPlayer eggwarsPlayer = match.getEggwarsPlayer(player);
        if (eggwarsPlayer == null) {
            return false;
        }

        modifierItem.useOnControlCrystal(
                eggwarsPlayer, crystal, stack
        );

        return true;
    }
}
