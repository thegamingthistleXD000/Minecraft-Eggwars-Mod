package org.eggwars_game.eggwars_mod.new_items_and_functionalities;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.eggwars_game.eggwars_mod.Eggwars;
import org.eggwars_game.eggwars_mod.new_items_and_functionalities.ModifierItems.*;

public class ModItems {


    public static final EnderSpiritItem ENDER_SPIRIT = registerItem("ender_spirit", new EnderSpiritItem(new Item.Settings().maxCount(1)));
    public static final CrystalConnectItem CRYSTAL_CONNECT = registerItem("crystal_connect", new CrystalConnectItem(new Item.Settings().maxCount(1)));
    public static final PearlBarrierItem PEARL_BARRIER = registerItem("pearl_barrier", new PearlBarrierItem(new Item.Settings().maxCount(1)));
    public static final ForceFieldItem FORCE_FIELD = registerItem("force_field", new ForceFieldItem(new Item.Settings().maxCount(1)));
    public static final ChorusWarpItem CHORUS_WARP = registerItem("chorus_warp", new ChorusWarpItem(new Item.Settings().maxCount(1)));
    public static final WarpFruitItem WARP_FRUIT = registerItem("warp_fruit", new WarpFruitItem(new Item.Settings().maxCount(64)));

    public static final modItem ENDER_SHARD =  registerItem("endershard", new modItem(new Item.Settings().maxCount(64)));
    public static final modItem COBWEB_ARROW = registerItem("cobweb_arrow", new modItem(new Item.Settings().maxCount(64)));
    public static final modItem SPONGE_ARROW = registerItem("sponge_arrow", new modItem(new Item.Settings().maxCount(64)));
    public static final modItem MILK_BALL = registerItem("milk_ball", new modItem(new Item.Settings().maxCount(16)));
    public static final modItem END_OMEN_POTION = registerItem("end_omen_potion", new modItem (new Item.Settings().maxCount(1)));


    // public static final Item COBWEB_ARROW = registerItem("cobweb_arrow", new CobwebArrowItem(new Item.Settings().maxCount(16)));
    // public static final Item MILK_BALL = registerItem("milk_ball", new MilkBallItem(new Item.Settings().maxCount(16)));
    // public static final Item SPONGE_ARROW = registerItem("sponge_arrow", new SpongeArrowItem(new Item.Settings().maxCount(16)));

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(Eggwars.MOD_ID, name),
                item
        );
    }

    public static void registerModItems() {
        System.out.println("Registering Mod Items for EggWars Mod");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ENDER_SHARD);
        });
    }
}
