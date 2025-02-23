package com.kryeit.idler.neoforge;

import com.kryeit.idler.Idler;
import net.neoforged.fml.common.Mod;

@Mod(Idler.MOD_ID)
public final class IdlerNeoForge {
    public IdlerNeoForge() {
        // Run our common setup.
        Idler.init();
    }
}
