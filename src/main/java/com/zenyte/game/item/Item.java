package com.zenyte.game.item;

import com.zenyte.game.content.grandexchange.JSONGEItemDefinitions;
import com.zenyte.game.content.grandexchange.JSONGEItemDefinitionsLoader;
import com.zenyte.game.util.Utils;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single item.
 *
 * @author Graham
 * @author Kris
 */
public class Item {
    private static final int[] alchemyRestrictedItems = new int[]{ItemId.COINS_995, ItemId.LOOTING_BAG, ItemId.LOOTING_BAG_22586, ItemId.RUNE_POUCH, ItemId.ARCLIGHT};
    private int id;
    private int amount;
    private Map<String, Object> attributes;

    /**
     * Creates an item with the amount of 1.
     *
     * @param id
     */
    public Item(final int id) {
        this(id, 1);
    }

    /**
     * Creates an item with the desired amount.
     *
     * @param id
     * @param amount
     */
    public Item(final int id, final int amount) {
        this(id, amount, 0);
    }

    /**
     * Creates an item with the desired amount and charges.
     *
     * @param id
     * @param amount
     * @param charges
     */
    public Item(final int id, final int amount, final int charges) {
        this(id, amount, charges == 0 ? null : new HashMap<>() {
            {
                put("charges", charges);
            }
        });
    }

    public Item(final int id, final int amount, final Map<String, Object> attributes) {
		/*if (ItemDefinitions.isInvalid(id))
	        throw new ExceptionInInitializerError("Item " + id + " is invalid!");*/
        this.id = id;
        this.amount = amount;
        this.attributes = attributes;
    }

    /**
     * Constructs a new item object replica based off of the argument.
     *
     * @param item
     */
    public Item(final Item item) {
        id = item.getId();
        amount = item.getAmount();
        if (item.attributes != null) {
            attributes = new HashMap<>(item.attributes);
        }
    }

    public static int notedId(final int id) {
        return ItemDefinitions.getOrThrow(id).getNotedOrDefault();
    }

    public Map<String, Object> getAttributesCopy() {
        if (attributes == null) return null;
        return new HashMap<>(attributes);
    }

    public void resetAttributes() {
        attributes = null;
    }

    public boolean hasAttributes() {
        return attributes != null;
    }

    public Item toNote() {
        if (attributes != null) {
            return this;
        }
        final int notedId = getDefinitions().getNotedOrDefault();
        if (notedId == id) return this;
        return new Item(notedId, amount, attributes);
    }

    public void setAttribute(final String key, final Object value) {
        if (value == null || value instanceof Integer && (Integer) value == 0) {
            if (attributes == null) {
                return;
            }
            attributes.remove(key);
            if (attributes.isEmpty()) {
                attributes = null;
            }
        } else {
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            attributes.put(key, value);
        }
    }

    public Number getNumericAttribute(final String key) {
        final Object val = attributes == null ? null : attributes.get(key);
        if (!(val instanceof Number)) {
            return 0;
        }
        return (Number) val;
    }

    public boolean getBooleanAttribute(final String key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericAttribute(key).intValue();
        return value == 1;
    }

    public Object getAttribute(final String key) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }

    public int getCharges() {
        return getNumericAttribute("charges").intValue();
    }

    public void setCharges(final int amount) {
        if (amount == getCharges()) {
            return;
        }
        setAttribute("charges", amount);
    }

    public boolean hasCharges() {
        return getCharges() > 0;
    }

    public ItemDefinitions getDefinitions() {
        return ItemDefinitions.get(id);
    }

    public JSONGEItemDefinitions getGEDefinitions() {
        return JSONGEItemDefinitionsLoader.lookup(id);
    }

    public int getSellPrice() {
        return ItemDefinitions.getSellPrice(id);
    }

    /**
     * Creates a string based on the amount and name of item. If amount is greater than one than it pluralizes the name.
     * e.g "5 buckets", "1 bucket"
     */
    public String toPrettyString() {
        final String name = getName();
        final String prettyName = name.endsWith("s") ? name : Utils.plural(amount);
        return amount + " " + prettyName;
    }

    @Override
    public String toString() {
        if (attributes != null) {
            return getName() + " [id: " + id + ", amount: " + amount + ", attributes: " + attributes.toString() + "]";
        }
        return getName() + " [id: " + id + ", amount: " + amount + "]";
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Item item)) {
            return false;
        }
        return item.getId() == id && item.getAmount() == amount && Objects.equals(item.getAttributes(), attributes);
    }

    public String getName() {
        final ItemDefinitions defs = getDefinitions();
        if (defs == null) {
            return "null";
        }
        return defs.getName();
    }

    public boolean isStackable() {
        return getDefinitions().isStackable();
    }

    public boolean isTradable() {
        if (attributes != null && Boolean.TRUE.equals(attributes.get("Tradability"))) {
            return true;
        }
        if (id >= 2683 && id <= 2688) {
            //zenyte armour
            return true;
        }
        if (attributes != null) return false;
        if (id == 13204 || id == 995 || id == 13307) return true;
        final ItemDefinitions definitions = getDefinitions();
        if (definitions == null) return false;
        return definitions.isGrandExchange() || definitions.getNotedTemplate() > 0 || definitions.getNotedId() > 0;
    }

    public boolean isWieldable() {
        final ItemDefinitions defs = getDefinitions();
        if (defs == null) {
            return false;
        }
        return defs.containsOption("Wield") || defs.containsOption("Wear") || defs.containsOption("Equip") || defs.containsOption("Hold") || defs.containsOption("Chill");
    }

    public boolean isAlchemisable() {
        return !ArrayUtils.contains(alchemyRestrictedItems, id);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
