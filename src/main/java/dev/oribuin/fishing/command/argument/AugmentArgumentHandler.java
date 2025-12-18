package dev.oribuin.fishing.command.argument;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.augment.Augment;
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
 * Argument handler to match player input to a Augment.
 *
 * @see Augment The Fish class
 */
public class AugmentArgumentHandler implements ArgumentParser<CommandSender, Augment> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Augment> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        Augment augment = FishingPlugin.get().getAugmentManager().from(input);
        commandInput.readString();
        if (input.isEmpty() || augment == null) return ArgumentParseResult.failure(new AugmentParserException(input, commandContext));

        return ArgumentParseResult.success(augment);
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) ->
                FishingPlugin.get().getAugmentManager().getAugments().values()
                        .stream()
                        .map(x -> Suggestion.suggestion(x.getName()))
                        .toList()
        );
    }

    public static final class AugmentParserException extends ParserException {

        private final String input;

        public AugmentParserException(String input, CommandContext<?> context) {
            super(AugmentArgumentHandler.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));

            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }
}
