package xyz.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.AugmentRegistry;

import java.util.List;

public class AugmentArgument extends ArgumentHandler<Augment> {

    public AugmentArgument() {
        super(Augment.class);
    }

    @Override
    public Augment handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Augment augment = AugmentRegistry.all().get(input);
        if (augment != null) return augment;

        throw new HandledArgumentException("argument-handler-augments", StringPlaceholders.of("augment", input));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return AugmentRegistry.all().keySet().stream().toList();
    }

}
