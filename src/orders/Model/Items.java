/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Třída Items uchovává kolekci objektů Item
 * Pomocí instance ItemD umožňuje "práci" s položkami umístěnými v databázi
 * Zajišťuje synchronizaci kolekce Itemů a položek v databázi
 *
 * @author hrusk
 */
public final class Items {

    private List<Item> items;
    private final ItemD itemD;

    public Items(ItemD itemDAO) throws SQLException {
        this.itemD = itemDAO;
        this.items = new ArrayList<>();
    }

    /*
    * Metoda pro načtení všech dat z databáze
     */
    public void loadItemsFromDatabase() throws SQLException {
        items = itemD.getAllItems();
    }

    /*
    * Metoda pro vyhledávání Přidání do seznamu `items` po úspěšném vložení do databáze
     */
    public void addItemToDatabase(Item item) throws SQLException {
        itemD.addItem(item);
        items.add(item);
    }

    /*
    * Metoda pro update itemu
     */
    public void updateItemDatabase(Item item) throws SQLException {
        itemD.updateItem(item);
    }

    public void sortItems(){
        items.sort(Comparator.comparing(Item::isActive).reversed()
                        .thenComparing(Item::getCategoryID)
                        .thenComparing(Item::getName));
    }
    
    /*
    * Metoda vracející list itemů
     */
    public List<Item> getAllItems() {
        return items;
    }

    /*
    * Metoda vracející list itemů podle zadané kategorie
     */
    public List<Item> getItemsByCategory(int categoryId) {
       List<Item> itemsByCat = new ArrayList<>();
       for(Item item:items){
           if(categoryId==item.getCategoryID()){
               itemsByCat.add(item);
           }
       }
        return items;
    }
    
    /*
    * Metoda pro vyhledávání podle id itemu
     */
    public Item getItemById(int id) {
        for (Item item : items) {
            if (item.getItemID() == id) {
                return item;
            }
        }
        return null;
    }
    
    // Funkce pro vyhledání kategorie podle jména
    public Item getItemIdByName(String itemName) {
        if (itemName == null) {
            return null; 
        }
        for (Item item : items) {
            if (itemName.equals(item.getName())) {
                return item;
            }
        }
        return null;
    }
   
    
}
