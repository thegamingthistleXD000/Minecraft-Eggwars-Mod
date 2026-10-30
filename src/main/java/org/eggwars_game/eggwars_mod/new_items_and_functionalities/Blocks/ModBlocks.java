package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.eggwars_game.eggwars_mod.Eggwars;

public class ModBlocks {

    public static final Block CHORUS_WARP_BLOCK = registerBlock(
            "chorus_warp",
            new ChorusWarpBlock(
                    AbstractBlock.Settings.create()
                            .strength(2.0f)
            )
    );

    public static final Block FORCE_FIELD_BLOCK = registerBlock(
            "force_field",
            new ForceFieldBlock(
                    AbstractBlock.Settings.create()
                            .strength(2.0f)
            )
    );

    private static Block registerBlock(String name, Block block) {
        return Registry.register(
                Registries.BLOCK,
                Identifier.of(Eggwars.MOD_ID, name),
                block
        );
    }

    public static void registerBlocks() {
    }
}