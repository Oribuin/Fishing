package xyz.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.fishing.skill.Skill;
import xyz.oribuin.fishing.skill.SkillRegistry;

import java.util.List;

public class SkillArgument extends ArgumentHandler<Skill> {

    public SkillArgument() {
        super(Skill.class);
    }

    @Override
    public Skill handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Skill skill = SkillRegistry.get(input);
        if (skill != null) return skill;

        throw new HandledArgumentException("argument-handler-skill", StringPlaceholders.of("input", input));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return SkillRegistry.all().values().stream().map(Skill::name).toList();
    }

}