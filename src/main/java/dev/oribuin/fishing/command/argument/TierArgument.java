package dev.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.fish.Tier;
import dev.oribuin.fishing.manager.TierManager;

import java.util.List;

/**
 * Argument handler to match player input to a Tier.
 *
 * @see Tier The Tier class
 */
public class TierArgument extends ArgumentHandler<Tier> {

    /**
     * The constructor for the argument handler.
     */
    public TierArgument() {
        super(Tier.class);
    }

    /**
     * Serialize the player input to a Tier object if it exists. If the input does not exist, throw a HandledArgumentException.
     *
     * @param context       The context of the command
     * @param argument      The argument being handled
     * @param inputIterator The player input
     *
     * @return The Tier object
     *
     * @throws HandledArgumentException If the input does not exist
     */
    @Override
    public Tier handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Tier tier = FishingPlugin.get().getManager(TierManager.class).get(input);
        if (tier != null) return tier;

        throw new HandledArgumentException("argument-handler-tier", StringPlaceholders.of("input", input));
    }

    /**
     * Suggest a list of Tiers based on the player input.
     *
     * @param context  The context of the command
     * @param argument The argument being handled
     * @param args     The player input
     *
     * @return A list of Tiers
     */
    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return FishingPlugin.get().getManager(TierManager.class).tiers().keySet().stream().toList();
    }

}
