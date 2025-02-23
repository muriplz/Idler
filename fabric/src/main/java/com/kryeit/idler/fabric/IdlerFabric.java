package com.kryeit.idler.fabric;

import com.kryeit.idler.Idler;
import net.fabricmc.api.ModInitializer;

public final class IdlerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Idler.init();
    }
}
