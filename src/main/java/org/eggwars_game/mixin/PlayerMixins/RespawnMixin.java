package org.eggwars_game.mixin.PlayerMixins;

import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
    public class RespawnMixin {

        @Inject(
                method = "onClientStatus",
                at = @At("HEAD"),
                cancellable = true
        )
        private void eggwars$disableRespawn(
                ClientStatusC2SPacket packet, CallbackInfo ci
        ) {

            ServerPlayNetworkHandler handler =
                    (ServerPlayNetworkHandler) (Object) this;

            if (!handler.player.getCommandTags().contains("eggwars_player")) {
                return;
            }

            if (packet.getMode() ==
                    ClientStatusC2SPacket.Mode.PERFORM_RESPAWN) {

                ci.cancel();
            }
        }
    }
