package dev.oribuin.fishing.model.economy;

import dev.oribuin.fishing.model.economy.impl.EntropyCurrency;
import dev.oribuin.fishing.model.economy.impl.FishExpCurrency;
import dev.oribuin.fishing.model.economy.impl.PlayerExpCurrency;
import dev.oribuin.fishing.model.economy.impl.SkillpointCurrency;
import dev.oribuin.fishing.model.economy.impl.VaultCurrency;

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
