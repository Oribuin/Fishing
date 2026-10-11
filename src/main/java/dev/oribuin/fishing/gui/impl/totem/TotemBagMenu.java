package dev.oribuin.fishing.gui.impl.totem;

@Deprecated
public class TotemBagMenu { // extends PluginMenu<Gui, TotemBagMenu.Config> 

    //    private final Supplier<Totem> totemSupplier;
    //
    //    public TotemBagMenu(FishingPlugin plugin, Supplier<Totem> totemSupplier) {
    //        super(plugin, TotemBagMenu.Config.class);
    //        this.totemSupplier = totemSupplier;
    //        this.gui = this.createMenu().get();
    //
    //        Totem totem = this.totemSupplier.get();
    //        Fisher fisher = plugin.getDataManager().get(totem.getOwner());
    //        Placeholders placeholders = Placeholders.builder()
    //                .addAll(fisher.getPlaceholders())
    //                .addAll(totem.getPlaceholders())
    //                .build();
    //
    //        // add empty slot
    //        this.placeBagContent(placeholders);
    //    }
    //
    //    public void placeBagContent(Placeholders placeholders) {
    //        this.gui.clearItems();
    //
    //        int capacity = (this.gui.getRows() * 9) - 1;
    //        List<Integer> menuSlots = FishUtils.parseList("0-" + capacity);
    //        this.gui.setItem(menuSlots, new GuiItem(BORDER.create()));
    //        this.setDummyIcons(placeholders);
    //
    //        Totem currentTotem = this.totemSupplier.get();
    //        for (Map.Entry<Integer, ItemStack> stackEntry : currentTotem.getBag().entrySet()) {
    //            int slot = stackEntry.getKey();
    //            ItemStack stack = stackEntry.getValue();
    //
    //            gui.setItem(slot, new GuiItem(stack.clone(), event -> {
    //                Player player = (Player) event.getWhoClicked();
    //                if (!player.isOnline() || player.isSleeping()) return;
    //
    //                PlayerInventory inventory = player.getInventory();
    //                if (inventory.firstEmpty() == -1) return;
    //
    //                Totem totem = this.totemSupplier.get();
    //                ItemStack target = totem.getBag().get(slot);
    //                if (target == null || target.getType().isAir() || target.getAmount() == 0) {
    //                    player.sendMessage("Item does not exist");
    //                    return;
    //                }
    //
    //                ItemStack withdrawn = totem.withdrawBag(slot);
    //                if (withdrawn != null) inventory.addItem(stack);
    //
    //                this.placeBagContent(placeholders);
    //            }));
    //        }
    //
    //        this.gui.update();
    //    }
    //
    //    /**
    //     * Creates the menu for the plugin
    //     *
    //     * @return the resulting menu
    //     */
    //    @Override
    //    public Supplier<Gui> createMenu() {
    //        return () -> Gui.gui()
    //                .title(Component.text(this.config.getTitle()))
    //                .rows(slotToRows(this.totemSupplier.get().getBagCapacity()))
    //                .disableAllInteractions()
    //                .create();
    //    }
    //
    //    @Deprecated
    //    @ConfigSerializable
    //    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    //    public static class Config extends GuiConfig {
    //
    //        public Config() {
    //            this.title = "Totem Bag";
    //            this.rows = -1;
    //        }
    //    }

}
