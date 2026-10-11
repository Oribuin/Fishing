package dev.oribuin.fishing.storage.util;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.persistence.PersistentDataContainer;

public interface PDCSerializable {

    /**
     * Write data into a data container
     *
     * @param container The container to write into
     */
    void writeContainer(PersistentDataContainer container);

    /**
     * Load and deserialize data from a data container
     *
     * @param container The container to read from
     */
    void readContainer(PersistentDataContainerView container);

}
