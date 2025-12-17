package dev.oribuin.fishing.config.serializer;

import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationSerializer extends ScalarSerializer<Duration> {

    private static final DurationSerializer INSTANCE = new DurationSerializer();
    private static final Pattern DURATION_PATTERN = Pattern.compile("(([1-9][0-9]+|[1-9])[dhms])");

    private DurationSerializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(Type type, Object obj) {
        String value = obj.toString().toLowerCase();
        Matcher matcher = DURATION_PATTERN.matcher(value);
        Duration duration = Duration.ofNanos(0);

        while (matcher.find()) {
            String group = matcher.group();
            String timeUnit = String.valueOf(group.charAt(group.length() - 1));
            int timeValue = Integer.parseInt(group.substring(0, group.length() - 1));
            switch (timeUnit) {
                case "d" -> duration = duration.plusDays(timeValue);
                case "h" -> duration = duration.plusHours(timeValue);
                case "m" -> duration = duration.plusMinutes(timeValue);
                case "s" -> duration = duration.plusSeconds(timeValue);
                default -> throw new IllegalArgumentException("Invalid duration character. Use format 1d2h3m4s");
            }
        }

        return duration;
    }

    @Override
    public Object serialize(Duration item, Predicate<Class<?>> typeSupported) {
        return item.toString().substring(2);
    }

    public static DurationSerializer getInstance() {
        return INSTANCE;
    }

}
