package pl.makoto.createsupportopac.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import pl.makoto.createsupportopac.network.CreateOpenGuiPacket;
import pl.makoto.createsupportopac.settings.CreateClaimSettings;
import pl.makoto.createsupportopac.settings.CreateClaimSettingsManager;
import pl.makoto.createsupportopac.settings.CreateMachineType;

import java.util.Map;

public class CreateSupportCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("createsupport")
            .then(Commands.literal("gui")
                .executes(CreateSupportCommands::openGui))
            .then(Commands.literal("settings")
                .then(Commands.literal("view")
                    .executes(CreateSupportCommands::view))
                .then(Commands.literal("set")
                    .then(Commands.argument("machine_type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (CreateMachineType type : CreateMachineType.values())
                                builder.suggest(type.id);
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("value", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("allow");
                                builder.suggest("deny");
                                return builder.buildFuture();
                            })
                            .executes(CreateSupportCommands::set)
                        )
                    )
                )
                .then(Commands.literal("reset")
                    .executes(CreateSupportCommands::reset))
            )
        );
    }

    private static int openGui(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }
        player.connection.send(new ClientboundCustomPayloadPacket(new CreateOpenGuiPacket()));
        return 1;
    }

    private static int view(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }
        CreateClaimSettings settings = CreateClaimSettingsManager.get(source.getServer())
                .getOrCreate(player.getUUID());
        Map<CreateMachineType, Boolean> all = settings.getAllSettings();
        source.sendSuccess(() -> Component.literal("§6Create machine settings for your claims:"), false);
        for (CreateMachineType type : CreateMachineType.values()) {
            boolean allowed = all.getOrDefault(type, true);
            String line = "  " + type.displayName + ": " + (allowed ? "§aallow" : "§cdeny");
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }
        String typeArg = StringArgumentType.getString(ctx, "machine_type");
        String valueArg = StringArgumentType.getString(ctx, "value");

        CreateMachineType type = CreateMachineType.fromId(typeArg);
        if (type == null) {
            source.sendFailure(Component.literal("Unknown machine type: " + typeArg));
            return 0;
        }
        boolean allow;
        if (valueArg.equalsIgnoreCase("allow")) {
            allow = true;
        } else if (valueArg.equalsIgnoreCase("deny")) {
            allow = false;
        } else {
            source.sendFailure(Component.literal("Value must be 'allow' or 'deny'."));
            return 0;
        }

        CreateClaimSettingsManager.get(source.getServer()).setAllowed(player.getUUID(), type, allow);
        String status = allow ? "§aallowed" : "§cdenied";
        String msg = type.displayName + " is now " + status + " on your claims.";
        source.sendSuccess(() -> Component.literal(msg), false);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }
        CreateClaimSettingsManager.get(source.getServer()).reset(player.getUUID());
        source.sendSuccess(() -> Component.literal("All Create machine settings reset to default (allow)."), false);
        return 1;
    }
}
