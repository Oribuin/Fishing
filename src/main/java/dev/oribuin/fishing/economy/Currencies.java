package dev.oribuin.fishing.economy;

import dev.oribuin.fishing.economy.impl.EntropyCurrency;
import dev.oribuin.fishing.economy.impl.FishExpCurrency;
import dev.oribuin.fishing.economy.impl.PlayerExpCurrency;
import dev.oribuin.fishing.economy.impl.SkillpointCurrency;
import dev.oribuin.fishing.economy.impl.VaultCurrency;

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
