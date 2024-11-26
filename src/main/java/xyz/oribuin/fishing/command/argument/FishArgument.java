package xyz.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.manager.TierManager;

import java.util.List;

public class FishArgument extends ArgumentHandler<Fish> {

    public FishArgument() {
        super(Fish.class);
    }

    @Override
    public Fish handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Fish fish = FishingPlugin.get().getManager(TierManager.class).getFish(input);
        if (fish != null) return fish;

        throw new HandledArgumentException("argument-handler-fish", StringPlaceholders.of("input", input));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return FishingPlugin.get().getManager(TierManager.class).allFish().stream()
                .map(Fish::name)
                .toList();
    }

}
