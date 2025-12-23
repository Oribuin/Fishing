package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.argument.AugmentArgumentHandler;
import dev.oribuin.fishing.command.argument.FishArgumentHandler;
import dev.oribuin.fishing.command.argument.TierArgumentHandler;
import dev.oribuin.fishing.command.impl.ApplyCommand;
import dev.oribuin.fishing.command.impl.GiveCommand;
import dev.oribuin.fishing.command.impl.ListCommand;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.setting.Configurable;
import org.incendo.cloud.setting.ManagerSetting;

import javax.naming.NoPermissionException;
import java.util.function.Supplier;

public class CommandManager extends LegacyPaperCommandManager<CommandSender> implements Manager {

    private final FishingPlugin plugin;
    private AnnotationParser<CommandSender> parser;

    public CommandManager(@NonNull FishingPlugin owningPlugin) {
        super(
                owningPlugin,
                ExecutionCoordinator.asyncCoordinator(),
                SenderMapper.identity()
        );

        this.plugin = owningPlugin;
        this.reload(this.plugin);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        // Register the command manager

        Configurable<ManagerSetting> commandSettings = this.settings();
        commandSettings.set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        commandSettings.set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);

        // Register argument parser
        this.parser = new AnnotationParser<>(this, CommandSender.class);

        // Register capabilities
        if (this.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.registerBrigadier();
        } else if (this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.registerAsynchronousCompletions();
        }

        this.exceptionController()
                .registerHandler(NoPermissionException.class, x -> PluginMessages.get()
                        .getNoPermission().send(x.context().sender())
                )
                .registerHandler(InvalidSyntaxException.class, x -> PluginMessages.get()
                        .getInvalidSyntax().send(x.context().sender(), "syntax", x.exception().correctSyntax())
                )
                .registerHandler(InvalidCommandSenderException.class, x -> PluginMessages.get()
                        .getRequirePlayer().send(x.context().sender(), "sender", x.context().sender().getName())
                );

        // Register additional stuff down here :3
        this.registerParser(Augment.class, AugmentArgumentHandler::new);
        this.registerParser(Fish.class, FishArgumentHandler::new);
        this.registerParser(Tier.class, TierArgumentHandler::new);

        // Register all the plugin commands
        this.parser.parse(
                new ApplyCommand(this.plugin),
                new GiveCommand(this.plugin),
                new ListCommand(this.plugin)
        );

    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
    }

    /**
     * Register a command parser into the plugin command manager
     *
     * @param type   The type class being parsed
     * @param parser The argument parser
     * @param <T>    The type being parsed
     */
    public <T> void registerParser(Class<T> type, Supplier<ArgumentParser<CommandSender, T>> parser) {
        ParserDescriptor<CommandSender, T> descriptor = ParserDescriptor.of(parser.get(), type);
        this.parserRegistry().registerParser(descriptor);
    }

    public FishingPlugin getPlugin() {
        return plugin;
    }

    public AnnotationParser<CommandSender> getParser() {
        return parser;
    }
}
