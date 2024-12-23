package xyz.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.manager.TierManager;

import java.util.List;

public class TierArgument extends ArgumentHandler<Tier> {

    public TierArgument() {
        super(Tier.class);
    }

    @Override
    public Tier handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Tier tier = FishingPlugin.get().getManager(TierManager.class).get(input);
        if (tier != null) return tier;

        throw new HandledArgumentException("argument-handler-tier", StringPlaceholders.of("input", input));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return FishingPlugin.get().getManager(TierManager.class).tiers().keySet().stream().toList();
    }

}
