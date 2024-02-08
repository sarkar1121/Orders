/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída ItemUsageD umožňuje manipulaci s daty položek v databázi
 * Zajišťuje práci s transakcemi
 * Vyžaduje připojení k databázi předané přes parametr v konstruktoru
 * 
 * @author hrusk
 */
public class ItemUsageD {

    private java.sql.Connection conn;

    public ItemUsageD(java.sql.Connection conn) {
        this.conn = conn;
    }

    /**
     * Načte všechna využití položek z databáze pro zadaný rok. 
     */
    public List<ItemUsage> getAllItemUsages(int year) throws SQLException {
        List<ItemUsage> itemUsages = new ArrayList<>();
        String sql = "SELECT * FROM ItemUsage WHERE YEAR(purchase_date) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemUsage usage = new ItemUsage(rs.getInt("id"), rs.getInt("id_Item"), rs.getInt("quantity"),
                            rs.getBigDecimal("price_per_piece"), rs.getBigDecimal("sum"),
                            rs.getDate("purchase_date"), rs.getDate("usage_date"));
                    itemUsages.add(usage);
                }
            }
        }
        return itemUsages;
    }

    /**
     * Přidá záznam o využití položky do databáze a nastaví jeho vygenerované ID.
     */
    public void addItemUsage(ItemUsage usage) throws SQLException {
        String sql = "INSERT INTO ItemUsage (id_Item, quantity, price_per_piece, sum, purchase_date, usage_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, usage.getItemId());
            stmt.setInt(2, usage.getQuantity());
            stmt.setBigDecimal(3, usage.getPricePerPiece());
            stmt.setBigDecimal(4, usage.getSum());
            stmt.setDate(5, convertToSqlDate(usage.getPurchaseDate()));
            stmt.setDate(6, convertToSqlDate(usage.getUsageDate()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();  
                throw new SQLException("Vytvoření využití položky selhalo, žádný řádek nebyl přidán.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usage.setId(generatedKeys.getInt(1));
                } else {
                    conn.rollback();  
                    throw new SQLException("Vytvoření využití položky selhalo, ID nebylo získáno.");
                }
            }

            conn.commit();  
        } catch (SQLException e) {
            conn.rollback();  
            throw e;
        }
    }

    /**
     * Aktualizuje záznam o využití položky v databázi.
     *
     */
    public void updateItemUsage(ItemUsage usage) throws SQLException {
        String sql = "UPDATE ItemUsage SET id_Item = ?, quantity = ?, price_per_piece = ?, sum = ?, purchase_date = ?, usage_date = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, usage.getItemId());
        stmt.setInt(2, usage.getQuantity());
        stmt.setBigDecimal(3, usage.getPricePerPiece());
        stmt.setBigDecimal(4, usage.getSum());
        stmt.setDate(5, convertToSqlDate(usage.getPurchaseDate()));
        stmt.setDate(6, convertToSqlDate(usage.getUsageDate()));
        stmt.setInt(7, usage.getId());
        stmt.executeUpdate();

    }

    /**
     * Převede java.util.Date na java.sql.Date pro použití v SQL dotazech.
     */
    private java.sql.Date convertToSqlDate(java.util.Date utilDate) {
        return utilDate == null ? null : new java.sql.Date(utilDate.getTime());
    }

}


