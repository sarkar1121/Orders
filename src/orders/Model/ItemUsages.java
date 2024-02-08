/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Třída ItemUsages uchovává kolekci objektů ItemUsage. Poskytuje metody pro
 * práci s položkami v databázi a synchronizaci s interní kolekcí.
 *
 * @author hrusk
 */
public class ItemUsages {

    private List<ItemUsage> usages;
    private final ItemUsageD itemUsageD;

    public ItemUsages(ItemUsageD itemUsageD) throws SQLException {
        this.itemUsageD = itemUsageD;
        this.usages = new ArrayList<>();
    }

    //Načte položky z databáze do  kolekce pro zadaný rok.
    public void loadUsagesFromDatabase(int year) throws SQLException {
        usages.clear();
        this.usages = itemUsageD.getAllItemUsages(year);
    }

    //Přidá novou položku do databáze a synchronizuje s kolekcí.
    public void addItemUsage(ItemUsage usage) throws SQLException {
        itemUsageD.addItemUsage(usage);
        this.usages.add(usage);
    }
    
    // Aktualizuje existující položku v databázi a synchronizuje s  kolekcí.
    public void updateItemUsage(ItemUsage usage) throws SQLException {
        itemUsageD.updateItemUsage(usage);
        int index = this.usages.indexOf(usage);
        if (index != -1) {
            this.usages.set(index, usage);
        }
    }
    
    //Třídí položky v  kolekci podle data nákupu.
    public void sortUsagesByDate() {
        Collections.sort(usages, Comparator.comparing(ItemUsage::getPurchaseDate));
    }
    
    //Vrátí kolekci položek.
    public List<ItemUsage> getAllUsages() {
        return new ArrayList<>(usages);
    }

    //Vyhledá a vrátí položku na základě poskytnutých informací.
    public ItemUsage getItemUsageBySomeInfo(Object[] searchData, SimpleDateFormat dateFormat) throws ParseException {
        if (searchData != null && searchData.length == 3) {
            for (ItemUsage itemUsage : usages) {
                if (itemUsage.getItemId() == Integer.parseInt(searchData[0].toString())
                        && itemUsage.getQuantity() == Integer.parseInt(searchData[1].toString())
                        && itemUsage.getPurchaseDate().equals(dateFormat.parse(searchData[2].toString()))) {
                    return itemUsage;
                }
            }
        }
        return null;
    }

    //Vrátí položku podle jejího identifikátoru.
    public ItemUsage getItemUsageById(int id) {
        for (ItemUsage itemUsage : usages) {
            if (itemUsage.getId() == id) {
                return itemUsage;
            }
        }
        return null;
    }
}
