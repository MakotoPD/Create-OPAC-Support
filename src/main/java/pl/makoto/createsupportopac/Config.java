package pl.makoto.createsupportopac;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ALLOW_CREATE_IN_WILDERNESS = BUILDER
            .comment("Whether Create machines can operate freely in unclaimed chunks (wilderness)")
            .define("allowCreateInWilderness", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
