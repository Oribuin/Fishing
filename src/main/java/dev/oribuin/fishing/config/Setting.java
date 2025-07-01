package dev.oribuin.fishing.config;

import dev.oribuin.fishing.FishingPlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.SettingHolder;
import dev.rosewood.rosegarden.config.SettingSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;


public class Setting implements SettingHolder {
    public static final Setting INSTANCE = new Setting();
    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<String> REQUIRED_XP_FORMULA = create(
            "required-xp-formula",
            STRING,
            "%level% * 625",
            "The formula to calculate the required experience to level up per level"
    );


    private static <T> RoseSetting<T> create(String key, SettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.ofBackedValue(key, FishingPlugin.get(), serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<ConfigurationSection> create(String key, String... comments) {
        RoseSetting<ConfigurationSection> setting = RoseSetting.ofBackedSection(key, FishingPlugin.get(), comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return KEYS;
    }

    /**
     * @return 
     */
    @Override
    public List<RoseSetting<?>> get() {
        return List.of();
    }
}
