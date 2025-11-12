package com.awesoft.ccx;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CCXConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Main");

        //more future stuff ik im really preparing arent i

        builder.pop();

        SPEC = builder.build();
    }

    public static void register(ModLoadingContext context){
        context.registerConfig(ModConfig.Type.SERVER, SPEC, "CCX/"+CCX.MODID+"-config.toml");
    }
}
