package orders.Model;

import java.util.Objects;

/**
 * Třída reprezentující kategorii v aplikaci. Obsahuje identifikátor kategorie a
 * její název. Poskytuje konstruktory, gettery, settery a metody pro porovnání
 * objektů.
 *
 * @author hrusk
 */
public class Category {

    private int categoryID;
    private String catName;

    // Konstruktor s parametry pro identifikátor a název kategorie    
    public Category(int kategorieID, String nazevKategorie) {
        this.categoryID = kategorieID;
        this.catName = nazevKategorie;
    }
    // Konstruktor s parametrem pro název kategorie (pro použití při vytváření nové kategorie)   
    public Category(String nazevKategorie) {
        this.catName = nazevKategorie;
    }

    // Gettery a settery
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return categoryID == category.categoryID
                && Objects.equals(catName, category.catName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryID, catName);
    }
}
