package com.kryeit.idler.forge;

import com.kryeit.idler.Idler;
import net.minecraftforge.fml.common.Mod;

@Mod(Idler.MOD_ID)
public final class IdlerForge {
    public IdlerForge() {
        Idler.init();
    }
}
