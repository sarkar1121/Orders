/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída CategoryD umožňuje práci categoriemi v databázi
 * Zajišťuje práci s transakcemi
 * Vyžaduje připojení k databázi předané přes parametr v konstruktoru
 * 
 * @author hrusk
 */
public class CategoryD {

    private final java.sql.Connection conn;

    public CategoryD(java.sql.Connection conn) {
        this.conn = conn;
    }

    /*
     * Přidá novou category do databáze a nastaví její vygenerované ID.
     */
    public void addCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Category (Name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getCatName());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();  // Rollback v případě neúspěchu
                throw new SQLException("Vytvoření kategorie selhalo, žádný řádek nebyl přidán.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryID(generatedKeys.getInt(1));
                } else {
                    conn.rollback();  // Rollback v případě neúspěchu
                    throw new SQLException("Vytvoření kategorie selhalo, ID nebylo získáno.");
                }
            }
            conn.commit();  // Commit transakce
        } catch (SQLException e) {
            conn.rollback();  // Rollback v případě výjimky
            throw e;
        }
    }

    // Načtení všech kategorií z databáze
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("CatID"), rs.getString("Name")));
            }
        }
        return categories;
    }
}
