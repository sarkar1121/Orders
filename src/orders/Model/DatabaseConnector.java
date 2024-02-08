package orders.Model;

import java.sql.*;
import java.util.List;

/**
 * Třída DatabaseConnector spravuje spojení s databází a předává ho objektům DAO
 *
 * @author hrusk
 */
public class DatabaseConnector {

    private Connection conn;
    private ItemD itemD;
    private CategoryD categoryD;
    private ItemUsageD itemUsageD;

    
    /**
     * Konstruktor třídy DatabaseHelper, který inicializuje spojení s databází.
     * Připojí se k databázi pomocí poskytnutých přihlašovacích údajů.
     */
    public DatabaseConnector() {
        try {
            String url = "jdbc:mysql://localhost:3306/cleaning_management";
            String user = "user";
            String password = "passwd1121";
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false); 
            itemD=new ItemD(this.conn);
            categoryD= new CategoryD(this.conn);
            itemUsageD=new ItemUsageD(this.conn);
        } catch (SQLException e) {
            System.out.println("Chyba při připojování k databázi: " + e);
        }
    }

//--------------------------------Category-----------------------------------------  
    public void addCategory(Category category) throws SQLException {
        categoryD.addCategory(category);
    }
    public List<Category> getAllCategories() throws SQLException {
        return categoryD.getAllCategories();
    }
    
//--------------------------------Item-----------------------------------------

    public void addItem(Item item) throws SQLException {
        itemD.addItem(item);
    }

    public List<Item> getAllItems() throws SQLException {
        return itemD.getAllItems();
    }
    
    public void updateItem(Item item) throws SQLException {
        itemD.updateItem(item);
    }
    
//--------------------------------ItemUsage-----------------------------------------
    public void addItemUsage(ItemUsage itemUsage) throws SQLException {
        itemUsageD.addItemUsage(itemUsage);
    }

    public List<ItemUsage> getAllItemUsages(int year) throws SQLException {
        return itemUsageD.getAllItemUsages(year);
    }
    
    public void updateItemUsage(ItemUsage usage) throws SQLException {
        itemUsageD.updateItemUsage(usage);
    }
    
    public ItemD getItemD() {
        return itemD;
    }

    public CategoryD getCategoryD() {
        return categoryD;
    }

    public ItemUsageD getItemUsageD() {
        return itemUsageD;
    }
}
