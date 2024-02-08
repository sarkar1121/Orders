package orders.Model;

import java.util.Objects;

/**
 * Třída uchovává data objektů Item
 *
 * @author hrusk
 */
public class Item {

    private int itemID;
    private final String name;
    private boolean active;
    private final int categoryID;

    /*
     * Konstruktor pro vytvoření instance položky bez id.
     */
    public Item(String name, int categoryID, boolean active) {
        this.name = name;
        this.categoryID = categoryID;
        this.active = active;
    }

    /*
     * Konstruktor pro vytvoření instance položky s id.
     */
    public Item(int itemID, String name, int categoryID, boolean active) {
        this.itemID = itemID;
        this.name = name;
        this.categoryID = categoryID;
        this.active = active;
    }

    /*
    * gettery a settery
    */
    public int getCategoryID() {
        return categoryID;
    }

    public void setItemID(int itemId) {
        itemID = itemId;
    }

    public int getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemID == item.itemID &&
                active == item.active &&
                categoryID == item.categoryID &&
                Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, name, active, categoryID);
    }
}
