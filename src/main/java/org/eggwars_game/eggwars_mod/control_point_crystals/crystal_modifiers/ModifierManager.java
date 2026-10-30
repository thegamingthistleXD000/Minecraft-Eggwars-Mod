package org.eggwars_game.eggwars_mod.control_point_crystals.crystal_modifiers;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;












public class ModifierManager<T extends Modifier> {

    protected final Map<T, BlockPos> modifiers = new HashMap<>();

    public T addModifier(Supplier<T> constructor, BlockPos pos) {
        T modifier = constructor.get();
        modifiers.put(modifier, pos.toImmutable());
        return modifier;
    }

    public void removeModifier(T modifier) {
        modifiers.remove(modifier);
    }

    public Set<T> getModifiers() {
        return modifiers.keySet();
    }


    public BlockPos getPosition(T modifier) {
        return modifiers.get(modifier);
    }

    public void tick (){
        for (Modifier modifier : modifiers.keySet()){
            modifier.tick();
        }
    }


}
