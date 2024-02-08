/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.SQLException;

/**
/**
 * Globální model aplikace, který poskytuje přístup k datům kategorií, položek a použití položek.
 * Inicializuje a udržuje spojení s databází.
 *
 * @author hrusk
 */
public class MasterModel {
    private final DatabaseConnector databaseConnector ;
    private final Categories categories ;
    private final Items items;
    private final ItemUsages itemUsages;

    //Konstruktor pro inicializaci datových tříd a připojení k databázi.
    public MasterModel() throws SQLException   {
         databaseConnector = new DatabaseConnector();
         categories = new Categories(databaseConnector.getCategoryD());
         items = new Items(databaseConnector.getItemD());
         itemUsages = new ItemUsages(databaseConnector.getItemUsageD());
    }
    
    //gettery
    public Categories getCategories() {
        return categories;
    }

    public Items getItems() {
        return items;
    }

    public ItemUsages getItemUsages() {
        return itemUsages;
    }
}
