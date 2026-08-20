package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.TextMessage;
import net.kyori.adventure.sound.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class PluginMessages {

    public static final String PREFIX = "<#94bc80><b>Fish</b> <gray>| <white>";

    public static PluginMessages get() {
        return FishingPlugin.get().getConfigLoader().get(PluginMessages.class);
    }

    @Comment("The message sent when a user reloads the plugin")
    private TextMessage reload = new TextMessage(PREFIX + "You have reloaded the plugin in <#93bc80><time><white>ms");

    @Comment("The message sent when a player does not have permission to do something.")
    private TextMessage noPermission = new TextMessage(PREFIX + "You do not have permission to do this");

    @Comment("The message sent when a player does not have permission to do something.")
    private TextMessage requirePlayer = new TextMessage(PREFIX + "You need to be sender type of <#94bc80><sender><white> to run this command");

    @Comment("The message sent when a player gets the syntax for a message wrong")
    private TextMessage invalidSyntax = new TextMessage(PREFIX + "You have provided invalid syntax. The correct usage is: <#94bc80><syntax>");

    @Comment("The target has a full inventory")
    private TextMessage fullInventory = new TextMessage(PREFIX + "The target you have provided has a full inventory.");

    @Comment("The target has been given a specified item")
    private TextMessage givenItem = new TextMessage(PREFIX + "You have provided the <#94bc80><target><white> with [<#94bc80>x<amount> <type><white>]: <white><name>");
    
    @Comment("The target has been given a specified item")
    private TextMessage givenAmount = new TextMessage(PREFIX + "You have provided the <#94bc80><target><white> with [<#94bc80>x<amount> <type><white>]: <white>Total: <#93bc80><total>");

    @Comment("The player has caught a specific fish")
    private TextMessage caughtFish = new TextMessage(PREFIX + "You have caught a <#94bc80><item><white>!");

    @Comment("The player has levelled up in their fishing stats")
    private TextMessage levelUp = new TextMessage(PREFIX + "You have levelled up to level <#94bc80><level>")
            .sound("entity_player_levelup")
            .source(Sound.Source.PLAYER);

    @Comment("Player has gutted fish for entropy")
    private TextMessage guttedFish = new TextMessage(PREFIX + "You have gutted <#93bc80><total> <white>fish for <#93bc80><entropy> <white>entropy!");

    @Comment("Player has not got any fish to gut")
    private TextMessage noGuttedFish = new TextMessage(PREFIX + "There are no fish that could be gutted");

    @Comment("Player has sold a fish for money")
    private TextMessage soldFish = new TextMessage(PREFIX + "You have sold <#93bc80><total> <white>fish for <#93bc80>$<money><white>!");

    @Comment("Player has not got any fish to sell")
    private TextMessage noSoldFish = new TextMessage(PREFIX + "There are no fish that could be sold");

    @Comment("Player has hit the maximum level for the upgrade")
    private TextMessage hitMaxLevel = new TextMessage(PREFIX + "You cannot level up your upgrade past level <#93bc80><max>");

    @Comment("All the messages regarding a fishing totem")
    private TotemMessages totem = new TotemMessages();

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class TotemMessages {

        @Comment("Player has placed a fishing totem")
        private TextMessage placed = new TextMessage(PREFIX + "You have placed a fishing totem down");

        @Comment("Player has removed a fishing totem")
        private TextMessage removed = new TextMessage(PREFIX + "You have removed a fishing totem");

        @Comment("Player has no space for the totem")
        private TextMessage noSpace = new TextMessage(PREFIX + "There is not enough space available for the fishing totem");

        @Comment("Player has activated the totem")
        private TextMessage activated = new TextMessage(PREFIX + "Your fishing totem will be active for <#93bc80><time>");

        @Comment("Totem is currently on cooldown")
        private TextMessage onCooldown = new TextMessage(PREFIX + "You cannot activate this totem as it is on cooldown");

        @Comment("Totem is already active")
        private TextMessage alreadyActive = new TextMessage(PREFIX + "This totem is already active");

        @Comment("Player has levelled up an upgrade on the totem")
        private TextMessage upgradeLevelUp = new TextMessage(PREFIX + "You have increased the level of <#93bc80><upgrade> <white>to <#93bc80><level><white>/<#93bc80><max>");

        @Comment("Player cannot access the totems menu")
        private TextMessage cannotAccess = new TextMessage(PREFIX + "You cannot interact with this totem as it belongs to someone else.");

        @Comment("Player cannot activate the totem")
        private TextMessage cannotActivate = new TextMessage(PREFIX + "You cannot activate this totem due to it's privacy settings.");

        @Comment("Other player activated totem")
        private TextMessage otherPlayerActivated = new TextMessage(PREFIX + "The player <#93bc80><activator> <white>has activated your totem");

        @Comment("Another totem is active in the nearby area")
        private TextMessage otherActiveNearby = new TextMessage(PREFIX + "There is already a totem active within this totem's range which may conflict, Activate this totem again to confirm");

        public TextMessage getPlaced() {
            return placed;
        }

        public TextMessage getRemoved() {
            return removed;
        }

        public TextMessage getNoSpace() {
            return noSpace;
        }

        public TextMessage getActivated() {
            return activated;
        }

        public TextMessage getOnCooldown() {
            return onCooldown;
        }

        public TextMessage getAlreadyActive() {
            return alreadyActive;
        }

        public TextMessage getUpgradeLevelUp() {
            return upgradeLevelUp;
        }

        public TextMessage getCannotAccess() {
            return cannotAccess;
        }

        public TextMessage getCannotActivate() {
            return cannotActivate;
        }

        public TextMessage getOtherPlayerActivated() {
            return otherPlayerActivated;
        }

        public TextMessage getOtherActiveNearby() {
            return otherActiveNearby;
        }
    }

    public TotemMessages getTotem() {
        return totem;
    }

    public TextMessage getReload() {
        return reload;
    }

    public TextMessage getNoPermission() {
        return noPermission;
    }

    public TextMessage getRequirePlayer() {
        return requirePlayer;
    }

    public TextMessage getInvalidSyntax() {
        return invalidSyntax;
    }

    public TextMessage getFullInventory() {
        return fullInventory;
    }

    public TextMessage getGivenItem() {
        return givenItem;
    }

    public TextMessage getGivenAmount() {
        return givenAmount;
    }

    public TextMessage getCaughtFish() {
        return caughtFish;
    }

    public TextMessage getLevelUp() {
        return levelUp;
    }

    public TextMessage getGuttedFish() {
        return guttedFish;
    }

    public TextMessage getNoGuttedFish() {
        return noGuttedFish;
    }

    public TextMessage getNoSoldFish() {
        return noSoldFish;
    }

    public TextMessage getSoldFish() {
        return soldFish;
    }

    public TextMessage getHitMaxLevel() {
        return hitMaxLevel;
    }

}
