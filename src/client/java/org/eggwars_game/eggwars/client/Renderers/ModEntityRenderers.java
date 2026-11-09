package org.eggwars_game.eggwars.client.Renderers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities.ModEntities;


public class ModEntityRenderers implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
            ModEntities.COBWEB_ARROW,
            ArrowEntityRenderer::new
        );

        EntityRendererRegistry.register(
            ModEntities.SPONGE_ARROW,
            ArrowEntityRenderer::new
        );
    }
}