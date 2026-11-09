package org.eggwars_game.eggwars_mod.new_items_and_functionalities.Entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.eggwars_game.eggwars_mod.Eggwars;

public class ModEntities {

    public static final EntityType<CobwebArrowEntity> COBWEB_ARROW =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(Eggwars.MOD_ID, "cobweb_arrow"),
                    EntityType.Builder.<CobwebArrowEntity>create(
                                    CobwebArrowEntity::new,
                                    SpawnGroup.MISC
                            )
                            .dimensions(0.5f, 0.5f)
                            .maxTrackingRange(4)
                            .trackingTickInterval(20)
                            .build()
            );

    public static final EntityType<SpongeArrowEntity> SPONGE_ARROW =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(Eggwars.MOD_ID, "sponge_arrow"),
                    EntityType.Builder.<SpongeArrowEntity>create(
                                    SpongeArrowEntity::new,
                                    SpawnGroup.MISC
                            )
                            .dimensions(0.5f, 0.5f)
                            .maxTrackingRange(4)
                            .trackingTickInterval(20)
                            .build()
            );


    public static final EntityType<MilkBallEntity> MILK_BALL =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(Eggwars.MOD_ID, "milk_ball"),
                    EntityType.Builder.<MilkBallEntity>create(
                                    MilkBallEntity::new,
                                    SpawnGroup.MISC
                            )
                            .dimensions(0.25f, 0.25f)
                            .maxTrackingRange(4)
                            .trackingTickInterval(10)
                            .build()
            );

    public static void registerModEntities() {
        Eggwars.LOGGER.info("Registering Mod Entities for " + Eggwars.MOD_ID);
    }


}