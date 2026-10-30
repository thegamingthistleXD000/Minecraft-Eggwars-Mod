
package org.eggwars_game.eggwars_mod.Utils;


import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.Set;





public class Volume{


    Set<BlockPos> Blocks =  new HashSet<BlockPos>();

    Box extents;

    public void add(BlockPos pos){Blocks.add(pos);}
    public void remove(BlockPos pos){
        if (!Blocks.contains(pos)){return;}
        Blocks.remove(pos);
    }
    public void emptyBlocks(){Blocks.clear();}

    public Set<BlockPos> getBlocks(){return Blocks;}



}

