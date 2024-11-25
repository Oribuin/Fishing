package xyz.oribuin.fishing.api.economy;

import xyz.oribuin.fishing.api.economy.impl.EntropyCurrency;
import xyz.oribuin.fishing.api.economy.impl.FishExpCurrency;
import xyz.oribuin.fishing.api.economy.impl.PlayerExpCurrency;
import xyz.oribuin.fishing.api.economy.impl.SkillpointCurrency;
import xyz.oribuin.fishing.api.economy.impl.VaultCurrency;

import java.util.PrimitiveIterator;

public enum Currencies {
    ENTROPY(new EntropyCurrency()),
    FISH_EXP(new FishExpCurrency()),
    PLAYER_XP(new PlayerExpCurrency()),
    SKILL_POINTS(new SkillpointCurrency()),
    VAULT(new VaultCurrency());

    private final Currency currency;

    Currencies(Currency currency) {
        this.currency = currency;
    }

    public Currency get() {
        return this.currency;
    }

}
