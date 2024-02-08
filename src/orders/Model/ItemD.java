/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída ItemD umožňuje manipulaci s daty položek v databázi
 * Zajišťuje práci s transakcemi
 * Vyžaduje připojení k databázi předané přes parametr v konstruktoru
 * 
 * @author hrusk
 */
public class ItemD {

    private final java.sql.Connection conn;

    public ItemD(java.sql.Connection conn) {
        this.conn = conn;
    }

    /*
     * Přidá novou položku do databáze a nastaví její vygenerované ID.
     */
    public void addItem(Item item) throws SQLException {
        String sql = "INSERT INTO Item (Name, CatID, Active) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getCategoryID());
            stmt.setBoolean(3, item.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();  
                throw new SQLException("Vytvoření položky selhalo, žádný řádek nebyl přidán.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setItemID(generatedKeys.getInt(1));
                } else {
                    conn.rollback();  
                    throw new SQLException("Vytvoření položky selhalo, ID nebylo získáno.");
                }
            }
            conn.commit();  
        } catch (SQLException e) {
            conn.rollback();  
            throw e;
        }
    }

    /*
     * Aktualizuje informace o položce v databázi.
     */
    public void updateItem(Item item) throws SQLException {
        String sql = "UPDATE Item SET Name = ?, CatID = ?, Active = ? WHERE ItemID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getCategoryID());
            stmt.setBoolean(3, item.isActive());
            stmt.setInt(4, item.getItemID());

            stmt.executeUpdate();
            conn.commit(); 
        } catch (SQLException e) {
            conn.rollback(); 
            throw e;
        }
    }

    /*
     * Načte všechny položky z databáze.
     */
    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM Item";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(rs.getInt("ItemID"), rs.getString("Name"), rs.getInt("CatID"), rs.getBoolean("Active")));
            }
        }
        return items;
    }
}
