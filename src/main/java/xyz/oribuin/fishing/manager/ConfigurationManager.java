package xyz.oribuin.fishing.manager;

import xyz.oribuin.fishing.FishingPlugin;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        SKILLS("skills", null, "The configuration settings for all the skills"),
        SKILLS_COMBO("skills.combo", null, ""),
        SKILLS_COMBO_MAX_FORMULA("skills.combo.max-formula", "%level% / 5 * 2", "Calculation for the maximum combo a player can have based on their level", "Placeholders:", "%level% - The current level of the player"),
        SKILLS_COMBO_CHANCE_INCREASE("skills.combo.chance-formula", "", "Calculation for how much "),
        SKILLS_COMBO_TIMEOUT("skills.combo.timeout", 30, "The time in seconds until the combo is reset after not catching a fish"),
        ;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return FishingPlugin.get().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[]{};
    }
}
