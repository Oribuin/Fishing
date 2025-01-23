package dev.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.oribuin.fishing.skill.Skill;
import dev.oribuin.fishing.skill.SkillRegistry;

import java.util.List;

/**
 * Argument handler to match player input to a Skill.
 *
 * @see Skill The Skill class
 */
public class SkillArgument extends ArgumentHandler<Skill> {

    /**
     * The constructor for the argument handler.
     */
    public SkillArgument() {
        super(Skill.class);
    }

    /**
     * Serialize the player input to a Skill object if it exists. If the input does not exist, throw a HandledArgumentException.
     *
     * @param context       The context of the command
     * @param argument      The argument being handled
     * @param inputIterator The player input
     *
     * @return The Skill object
     *
     * @throws HandledArgumentException If the input does not exist
     */
    @Override
    public Skill handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Skill skill = SkillRegistry.get(input);
        if (skill != null) return skill;

        throw new HandledArgumentException("argument-handler-skill", StringPlaceholders.of("input", input));
    }

    /**
     * Suggest a list of Skills based on the player input.
     *
     * @param context  The context of the command
     * @param argument The argument being handled
     * @param args     The player input
     *
     * @return A list of Skills
     */
    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return SkillRegistry.all().values().stream().map(Skill::name).toList();
    }

}