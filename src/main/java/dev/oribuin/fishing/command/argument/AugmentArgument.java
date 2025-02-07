package dev.oribuin.fishing.command.argument;

import dev.oribuin.fishing.augment.Augment;
import dev.oribuin.fishing.augment.AugmentRegistry;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import java.util.List;

/**
 * Argument handler to match player input to an Augment.
 *
 * @see Augment The Augment class
 */
public class AugmentArgument extends ArgumentHandler<Augment> {

    /**
     * The constructor for the argument handler.
     */
    public AugmentArgument() {
        super(Augment.class);
    }

    /**
     * Serialize the player input to an Augment object if it exists. If the input does not exist, throw a HandledArgumentException.
     *
     * @param context       The context of the command
     * @param argument      The argument being handled
     * @param inputIterator The player input
     *
     * @return The Augment object
     *
     * @throws HandledArgumentException If the input does not exist
     */
    @Override
    public Augment handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Augment augment = AugmentRegistry.all().get(input);
        if (augment != null) return augment;

        throw new HandledArgumentException("argument-handler-augments", StringPlaceholders.of("input", input));
    }

    /**
     * Suggest a list of Augments based on the player input.
     *
     * @param context  The context of the command
     * @param argument The argument being handled
     * @param args     The player input
     *
     * @return A list of Augments
     */
    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return AugmentRegistry.all().keySet().stream().toList();
    }

}
