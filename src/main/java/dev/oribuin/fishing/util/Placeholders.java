package dev.oribuin.fishing.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.lang.annotations.Subst;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable class that holds a map of placeholders and their values
 */
public final class Placeholders {

    private final static Placeholders EMPTY = new Placeholders(Collections.emptyMap());

    private final Map<String, Component> placeholders;
    private TagResolver resolvers;

    private Placeholders(Map<String, Component> placeholders) {
        this.placeholders = Collections.unmodifiableMap(placeholders);
        this.resolvers = FishUtils.RESOLVER;
    }

    /**
     * Applies the placeholders to the given string
     *
     * @param string the string to apply the placeholders to
     *
     * @return the string with the placeholders replaced
     */
    public Component apply(String string) {
        TagResolver.Builder builder = TagResolver.builder();
        builder.resolvers(this.resolvers);

        for (@Subst("") String key : this.placeholders.keySet()) {
            Component value = this.placeholders.get(key);
            builder.tag(key, Tag.selfClosingInserting(value));
        }

        return FishUtils.MINIMESSAGE.deserialize(string, builder.build())
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Applies the placeholders to the given string
     *
     * @param string the string to apply the placeholders to
     *
     * @return the string with the placeholders replaced
     */
    public String applyString(String string) {
        return FishUtils.PLAIN.serialize(this.apply(string));
    }

    /**
     * @return an unmodifiable map of the placeholders
     */
    public Map<String, Component> getPlaceholders() {
        return this.placeholders;
    }

    public TagResolver getResolvers() {
        return resolvers;
    }

    public void setResolvers(TagResolver resolvers) {
        this.resolvers = resolvers;
    }

    /**
     * @return a new Placeholders builder with delimiters initially set to %
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new builder with delimiters initially set to % and a placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new Placeholders builder with delimiters initially set to % and a placeholder added
     */
    public static Builder builder(String placeholder, Component value) {
        return new Builder().add(placeholder, value);
    }

    /**
     * Creates a new builder with delimiters initially set to % and a placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new Placeholders builder with delimiters initially set to % and a placeholder added
     */
    public static Builder builder(String placeholder, String value) {
        return new Builder().add(placeholder, value);
    }

    /**
     * Creates a new builder with delimiters initially set to % and a placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new Placeholders builder with delimiters initially set to % and a placeholder added
     */
    public static Builder builder(String placeholder, Object value) {
        return new Builder().add(placeholder, value.toString());
    }

    /**
     * @return the empty Placeholders instance
     */
    public static Placeholders empty() {
        return EMPTY;
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and one placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and one placeholder added
     */
    public static Placeholders of(String placeholder, Component value) {
        return builder(placeholder, value).build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and one placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and one placeholder added
     */
    public static Placeholders of(String placeholder, String value) {
        return builder(placeholder, Component.text(value)).build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and one placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and one placeholder added
     */
    public static Placeholders of(String placeholder, Object value) {
        return builder(placeholder, value).build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and two placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and two placeholders added
     */
    public static Placeholders of(String placeholder1, Component value1,
                                  String placeholder2, Component value2) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and two placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and two placeholders added
     */
    public static Placeholders of(String placeholder1, String value1,
                                  String placeholder2, String value2) {
        return builder(placeholder1, Component.text(value1))
                .add(placeholder2, Component.text(value2))
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and two placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and two placeholders added
     */
    public static Placeholders of(String placeholder1, Object value1,
                                  String placeholder2, Object value2) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and three placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and three placeholders added
     */
    public static Placeholders of(String placeholder1, Component value1,
                                  String placeholder2, Component value2,
                                  String placeholder3, Component value3) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and three placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and three placeholders added
     */
    public static Placeholders of(String placeholder1, String value1,
                                  String placeholder2, String value2,
                                  String placeholder3, String value3) {
        return builder(placeholder1, Component.text(value1))
                .add(placeholder2, Component.text(value2))
                .add(placeholder3, Component.text(value3))
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and three placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and three placeholders added
     */
    public static Placeholders of(String placeholder1, Object value1,
                                  String placeholder2, Object value2,
                                  String placeholder3, Object value3) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and four placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and four placeholders added
     */
    public static Placeholders of(String placeholder1, Component value1,
                                  String placeholder2, Component value2,
                                  String placeholder3, Component value3,
                                  String placeholder4, Component value4) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .add(placeholder4, value4)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and four placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and four placeholders added
     */
    public static Placeholders of(String placeholder1, String value1,
                                  String placeholder2, String value2,
                                  String placeholder3, String value3,
                                  String placeholder4, String value4) {
        return builder(placeholder1, Component.text(value1))
                .add(placeholder2, Component.text(value2))
                .add(placeholder3, Component.text(value3))
                .add(placeholder4, Component.text(value4))
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and four placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and four placeholders added
     */
    public static Placeholders of(String placeholder1, Object value1,
                                  String placeholder2, Object value2,
                                  String placeholder3, Object value3,
                                  String placeholder4, Object value4) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .add(placeholder4, value4)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and five placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     * @param placeholder5 the fifth placeholder to add
     * @param value5       the value to replace the fifth placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and five placeholders added
     */
    public static Placeholders of(String placeholder1, Component value1,
                                  String placeholder2, Component value2,
                                  String placeholder3, Component value3,
                                  String placeholder4, Component value4,
                                  String placeholder5, Component value5) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .add(placeholder4, value4)
                .add(placeholder5, value5)
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and five placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     * @param placeholder5 the fifth placeholder to add
     * @param value5       the value to replace the fifth placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and five placeholders added
     */
    public static Placeholders of(String placeholder1, String value1,
                                  String placeholder2, String value2,
                                  String placeholder3, String value3,
                                  String placeholder4, String value4,
                                  String placeholder5, String value5) {
        return builder(placeholder1, Component.text(value1))
                .add(placeholder2, Component.text(value2))
                .add(placeholder3, Component.text(value3))
                .add(placeholder4, Component.text(value4))
                .add(placeholder5, Component.text(value5))
                .build();
    }

    /**
     * Creates a new Placeholders instance with delimiters set to % and five placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     * @param placeholder5 the fifth placeholder to add
     * @param value5       the value to replace the fifth placeholder with
     *
     * @return a new Placeholders instance with delimiters set to % and five placeholders added
     */
    public static Placeholders of(String placeholder1, Object value1,
                                  String placeholder2, Object value2,
                                  String placeholder3, Object value3,
                                  String placeholder4, Object value4,
                                  String placeholder5, Object value5) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .add(placeholder4, value4)
                .add(placeholder5, value5)
                .build();
    }

    public static class Builder {

        private final Map<String, Component> placeholders;

        private Builder() {
            this.placeholders = new HashMap<>();
        }

        /**
         * Adds a placeholder
         *
         * @param placeholder The placeholder to add
         * @param value       The value to replace the placeholder with
         *
         * @return this
         */
        public Builder add(String placeholder, Component value) {
            this.placeholders.put(placeholder, value != null ? value : Component.empty());
            return this;
        }

        /**
         * Adds a placeholder
         *
         * @param placeholder The placeholder to add
         * @param value       The value to replace the placeholder with
         *
         * @return this
         */
        public Builder add(String placeholder, String value) {
            return this.add(placeholder, Component.text(value));
        }

        /**
         * Adds a placeholder
         *
         * @param placeholder The placeholder to add
         * @param value       The value to replace the placeholder with
         *
         * @return this
         */
        public Builder add(String placeholder, Object value) {
            if (value instanceof Component component) {
                return this.add(placeholder, component);
            }

            return this.add(placeholder, Objects.toString(value, "null"));
        }

        /**
         * Adds all placeholders from another Placeholders instance
         *
         * @param placeholders The Placeholders instance to add placeholders from
         *
         * @return this
         */
        public Builder addAll(Placeholders placeholders) {
            return this.addAll(placeholders.getPlaceholders());
        }

        /**
         * Adds all placeholders from a map
         *
         * @param placeholders The map to add placeholders from
         *
         * @return this
         */
        public Builder addAll(Map<String, Component> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        /**
         * @return a new Placeholders instance
         */
        public Placeholders build() {
            return new Placeholders(this.placeholders);
        }

    }

}
