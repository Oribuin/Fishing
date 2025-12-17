package dev.oribuin.fishing.command.argument;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.fish.Tier;
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
 * Argument handler to match player input to a Tier.
 *
 * @see Tier The Fish class
 */
public class TierArgumentHandler implements ArgumentParser<CommandSender, Tier> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Tier> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        Tier tier = FishingPlugin.get().getTierManager().get(input);
        commandInput.readString();
        if (input.isEmpty() || tier == null) return ArgumentParseResult.failure(new TierParserException(input, commandContext));

        return ArgumentParseResult.success(tier);
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) ->
                FishingPlugin.get().getTierManager().getTiers()
                        .values()
                        .stream()
                        .map(x -> Suggestion.suggestion(x.getName()))
                        .toList()
        );
    }

    public static final class TierParserException extends ParserException {

        private final String input;

        public TierParserException(String input, CommandContext<?> context) {
            super(TierArgumentHandler.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));

            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }
}
