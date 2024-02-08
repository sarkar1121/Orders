/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.SQLException;
import java.util.List;

/**
 * Třída Categories uchovává kolekci objektů category
 * Pomocí instance CategoryD umožňuje "práci" s položkami umístěnými v databázi
 *
 * @author hrusk
 */
public class Categories {

    private final CategoryD categoryD;
    private List<Category> categories;

    public Categories(CategoryD categoryD) throws SQLException {
        this.categoryD = categoryD;
    }

    // Načte kategorie z databáze
    public void loadCategoriesFromDatabase() throws SQLException {
        this.categories = categoryD.getAllCategories();
    }

    // Přidá novou kategorii
    public void addCategory(Category category) throws SQLException {
        categoryD.addCategory(category);
        this.categories.add(category);

    }

    // Funkce pro vyhledání kategorie podle ID
    public Category getCategoryById(int categoryId) {
        for (Category category : categories) {
            if (category.getCategoryID() == categoryId) {
                return category;
            }
        }
        return null; 
    }
    
    // Funkce pro vyhledání kategorie podle jména
    public Integer getCategoryIdByName(String categoryName) {
        if (categoryName == null) {
            return null; 
        }

        for (Category category : categories) {
            if (categoryName.equals(category.getCatName())) {
                return category.getCategoryID();
            }
        }

        return null;
    }

    // Vrátí seznam všech kategorií
    public List<Category> getAllCategories() {
        return categories;
    }

}
