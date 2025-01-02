package dev.oribuin.fishing.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import dev.oribuin.fishing.FishingPlugin;

import java.util.ArrayList;
import java.util.List;

import static dev.rosewood.rosegarden.config.RoseSettingSerializers.STRING;


public class Setting {
    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<String> REQUIRED_XP_FORMULA = create(
            "required-xp-formula",
            STRING,
            "%level% * 625",
            "The formula to calculate the required experience to level up per level"
    );


    private static <T> RoseSetting<T> create(String key, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.backed(FishingPlugin.get(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<CommentedConfigurationSection> create(String key, String... comments) {
        RoseSetting<CommentedConfigurationSection> setting = RoseSetting.backedSection(FishingPlugin.get(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return KEYS;
    }
}
