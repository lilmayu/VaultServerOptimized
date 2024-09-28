package dev.mayuna.vso;

import static dev.mayuna.vso.VsoMod.MODID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class VsoMod {

    public static final String MODID = "vso";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public VsoMod() {
        LOGGER.info("Hi :3 have also a great day");

        VsoConfig.load();
    }
}
