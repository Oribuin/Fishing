package dev.oribuin.fishing.command.argument;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.fish.Fish;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

/**
 * Argument handler to match player input to a Fish.
 *
 * @see Fish The Fish class
 */
public class FishArgumentHandler implements ArgumentParser<CommandSender, Fish> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Fish> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        Fish fish = FishingPlugin.get().getTierManager().getFish(input);
        commandInput.readString();
        if (input.isEmpty() || fish == null) return ArgumentParseResult.failure(new FishParserException(input, commandContext));

        return ArgumentParseResult.success(fish);
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) ->
                FishingPlugin.get().getTierManager().getAllFish()
                        .stream()
                        .map(x -> Suggestion.suggestion(x.getName()))
                        .toList()
        );
    }

    public static final class FishParserException extends ParserException {

        private final String input;

        public FishParserException(String input, CommandContext<?> context) {
            super(FishArgumentHandler.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));

            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }
}
