/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Presenter;

import javax.swing.JPanel;
import orders.Model.*;
import orders.View.*;
import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.*;

/**
 * Zodpovídá za komunikaci mezi modelem a uživatelským rozhraním (view) souvisejícím s položkami.
 * 
 * Tato třída řídí operace jako je zobrazení seznamu položek, přidání nových položek, aktualizace
 * a zobrazování detailních informací o položkách. Třída také zpracovává události generované
 * uživatelským rozhraním a zajišťuje aktualizaci modelu a view v souladu s uživatelskými akcemi.
 * 
 * ItemController využívá objekty třídy MasterModel pro přístup a manipulaci s daty
 * a třídy ItemView pro zobrazení a interakci s uživatelem.
 *
 *
 * @author hrusk
 */
public class ItemPresenter implements IPresenter {

    private final MasterModel model;
    private final ItemView view;
    private Item currentlySelectedItem;
    private int selectedRowIndex;

    /**
     * Konstruktor třídy ItemController.
     */
    public ItemPresenter(MasterModel model) {
        this.model = model;
        view = new ItemView("Položka");
        initialize();
    }

    // Nastavuje event handlery, řazení tabulky a naplňuje ji daty.
    private void initialize() {
        addEventHandlers();
        sortInTable();
        fillTable();
    }

    // Naplní tabulku daty položek ze získaného seznamu.
    private void fillTable() {
        model.getItems().getAllItems().forEach(this::addRowIntoTable);
    }

    //Přidá řádek s položkou do tabulky.
    private void addRowIntoTable(Item item) {
        Object[] row = {
            item.getName(),
            model.getCategories().getCategoryById(item.getCategoryID()).getCatName(),
            item.isActive() ? "Ano" : "Ne"
        };
        view.getTableModel().addRow(row);
    }

    // Přiřadí event handlery k tlačítkům a komponentám v pohledu.
    private void addEventHandlers() {
        view.getNewItemButt().addActionListener(e -> createNewItem());
        view.getSaveChangesButt().addActionListener(e -> saveChanges());
        view.getTable().getSelectionModel().addListSelectionListener(e -> showItemInfo());
    }

    // Vytvoří novou položku.
    private void createNewItem() {
        SwingUtilities.invokeLater(() -> {
            AddItemDialog addItem = prepareAddItemDialog();
            addItem.getAddButt().addActionListener(ae -> addItemAction(addItem));
            addItem.setVisible(true);
        });
    }

    // Připraví dialog pro přidání nové položky.
    private AddItemDialog prepareAddItemDialog() {
        AddItemDialog addItem = new AddItemDialog(null, true);
        JComboBox<String> categoryComboBox = addItem.getCategoryComboBox();
        categoryComboBox.removeAllItems();
        for (Category category : model.getCategories().getAllCategories()) {
            categoryComboBox.addItem(category.getCatName());
        }
        return addItem;
    }

    // Zpracuje akci přidání položky po stisknutí tlačítka "Přidat".
    private void addItemAction(AddItemDialog addItem) {
        String itemName = addItem.getNameField().getText();
        JComboBox<String> categoryComboBox = addItem.getCategoryComboBox();
        int idCategory = model.getCategories().getCategoryIdByName((String) categoryComboBox.getSelectedItem());
        if (itemName.isEmpty()) {
            showErrorDialog("Název položky nesmí být prázdný.");
        } else if (model.getItems().getItemIdByName(itemName) != null) {
            showErrorDialog("Položka s tímto názvem již existuje.");
        } else {
            createItemInBackground(itemName, idCategory, addItem);
        }
    }

    // Vytvoří novou položku na pozadí.
    private void createItemInBackground(String itemName, int idCategory, AddItemDialog addItem) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            Item newItem;

            @Override
            protected Void doInBackground() throws Exception {
                newItem = new Item(itemName, idCategory, true);
                model.getItems().addItemToDatabase(newItem);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    postProcessNewItem(addItem, newItem);
                } catch (Exception ex) {
                    showErrorDialog("Vyskytla se chyba a položka nebyla přidána");
                }
            }
        };
        worker.execute();
    }

    //Zpracuje nově vytvořenou položku.
    private void postProcessNewItem(AddItemDialog addItem, Item newItem) {
        addRowIntoTable(newItem);
        showDialog("Položka úspěšně přidána");
        addItem.dispose();
        view.getSorter().sort();
    }

    // Zobrazí informace o vybrané položce v postranním panelu.
    private void showItemInfo() {
        setPanelForShowItemInfo(model.getItems().getItemIdByName(selectItemFromTable()));
    }

    //Vybere název položky z tabulky.
    private String selectItemFromTable() {
        selectedRowIndex = view.getTable().getSelectedRow();
        if (selectedRowIndex != -1) {
            int modelIndex = view.getTable().convertRowIndexToModel(selectedRowIndex);
            return (String) view.getTableModel().getValueAt(modelIndex, 0);
        }
        return null;
    }


    //Nastaví panel na zobrazení informací o položce.
    private void setPanelForShowItemInfo(Item item) {
        if (item != null) {
            view.getNameField().setText(item.getName());
            view.getCategoryField().setText(model.getCategories().getCategoryById(item.getCategoryID()).getCatName());
            view.getIsActiveCheckBox().setSelected(item.isActive());
            currentlySelectedItem = item;
        }
    }


    // Uloží provedené změny u vybrané položky.
    private void saveChanges() {
        if (currentlySelectedItem != null) {
            updateItem(currentlySelectedItem, view.getIsActiveCheckBox().isSelected());
        } else {
            showErrorDialog("Nebyla vybrána žádná položka");
        }
    }


    //Aktualizuje stav vybrané položky v databázi.
    private void updateItem(Item item, boolean isActive) {
        try {
            item.setActive(isActive);
            model.getItems().updateItemDatabase(item);
            SwingUtilities.invokeLater(() -> {
                view.getTableModel().setValueAt(item.isActive() ? "Ano" : "Ne", view.getTable().convertRowIndexToModel(selectedRowIndex), 2);
                showDialog("Změny byly provedeny");
                view.getSorter().sort();
            });
        } catch (SQLException ex) {
            showErrorDialog("Došlo k problému při ukládání");
        }
    }


    // Nastaví řazení položek v tabulce.
    private void sortInTable() {
        view.getSorter().setComparator(2, Comparator.naturalOrder());
        view.getSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(2, SortOrder.ASCENDING)));
        view.getSorter().setComparator(1, Comparator.naturalOrder());
        view.getSorter().setComparator(0, Comparator.naturalOrder());
        view.getTable().setRowSorter(view.getSorter());
    }

    // Zobrazuje dialogové okno s chybovým hlášením.
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Chyba", JOptionPane.ERROR_MESSAGE);
    }
    
    // Zobrazuje dialogové okno s chybovým hlášením.
    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Informace", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public JPanel getView() {
        return view;
    }
    
}
