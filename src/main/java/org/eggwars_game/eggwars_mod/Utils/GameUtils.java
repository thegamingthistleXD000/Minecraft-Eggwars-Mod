package org.eggwars_game.eggwars_mod.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;









public class GameUtils {

    public static Entity getNearest(BlockPos pos, Entity entity){
        return entity;
    }

    public static Entity getNearest(BlockPos pos){return getNearest(pos,null);}


}
