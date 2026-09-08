package dev.oribuin.fishing.model.totem.upgrade;

public interface Toggleable {

    /**
     * Check whether a totem upgrade has been toggled on
     *
     * @return Whether the totem upgrade is activated
     */
    boolean isActivated();

    /**
     * Set a totem upgrade to activated or not
     *
     * @param activated Whether the totem upgrade is activated
     */
    void setActivated(boolean activated);

}
