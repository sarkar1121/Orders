/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Presenter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import orders.Model.*;
import orders.View.AddOrderDialog;
import orders.View.PurchaseView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * Třída PurchaseController řídí interakci mezi modelem a pohledem pro správu
 * nákupů. Zabývá se logikou spojenou s přidáváním, zobrazováním a aktualizací
 * informací o nákupech.
 *
 * Třída obsahuje metody pro inicializaci komponent, naplnění tabulky daty,
 * přidání událostí, správu dialogů pro přidání nových nákupů, aktualizaci a
 * zobrazení detailů o nákupech a další. Třída také zahrnuje metody pro validaci
 * uživatelských vstupů a zobrazování chybových hlášení.
 *
 * @author hrusk
 */
public class PurchasePresenter implements IPresenter {

    private MasterModel model;
    private PurchaseView view;
    private ItemUsage currentlySelectedItem;
    private SimpleDateFormat dateFormat;
    private int selectedRowIndex;
    private List<ItemUsage> itemUsagesList = new ArrayList<>();

    // Konstruktor inicializuje controller s modelem a připravuje view.    
    public PurchasePresenter(MasterModel model) {
        this.model = model;
        view = new PurchaseView("Nákup");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        initialize();
    }

    // Inicializuje controller, nastavuje obsluhu událostí, řazení v tabulce a naplňuje tabulku daty.
    private void initialize() {
        addEventHandlers();
        sortInTable();
        fillTable();
    }

    //Naplní tabulku daty položek ze získaného seznamu.    
    private void fillTable() {
        model.getItemUsages().getAllUsages().forEach(this::addRowIntoTable);
    }

    //Přidá řádek s položkou do tabulky.
    private void addRowIntoTable(ItemUsage usage) {
        Object[] row = {
            model.getItems().getItemById(usage.getItemId()).getName(),
            usage.getQuantity(),
            dateFormat.format(usage.getPurchaseDate())
        };
        view.getTableModel().addRow(row);
    }

    // Nastavuje obsluhu událostí pro tlačítka a další komponenty
    private void addEventHandlers() {
        view.getNewPurchaseButt().addActionListener(e -> createNewPurchase());
        view.getSaveChangesButt().addActionListener(e -> saveChanges());
        view.getTable().getSelectionModel().addListSelectionListener(e -> showUsageInfo());
        view.getSearchButt().addActionListener(e -> reloadData());
    }

    // Vytváří dialog pro přidání nového nákupu a nastavuje obsluhu událostí pro tlačítka v dialogu
    private void createNewPurchase() {
        AddOrderDialog addOrder = prepareAddOrderDialog();
        addOrder.getUseButt().addActionListener(e -> addDate(addOrder));
        addOrder.getAddButt().addActionListener(e -> {
            addItem(addOrder);
        });
        addOrder.getCloseButton().addActionListener(e -> {
            closeDialog(addOrder);
        });
        addOrder.setVisible(true);
    }

    // Připravuje a nastavuje dialog pro přidání nového nákupu.
    private AddOrderDialog prepareAddOrderDialog() {
        AddOrderDialog addOrder = new AddOrderDialog(null, true);
        addOrder.setDate(dateFormat.format(Calendar.getInstance().getTime()));
        JComboBox<String> categoryComboBox = addOrder.getCategoryComboBox();
        JComboBox<String> itemComboBox = addOrder.getItemComboBox();

        populateCategoryComboBox(categoryComboBox);
        addCategoryComboBoxListener(categoryComboBox, itemComboBox);
        updateItemComboBox(itemComboBox, categoryComboBox);

        return addOrder;
    }

    // Naplňuje comboBox kategoriími z modelu.
    private void populateCategoryComboBox(JComboBox<String> categoryComboBox) {
        categoryComboBox.removeAllItems();
        for (Category category : model.getCategories().getAllCategories()) {
            categoryComboBox.addItem(category.getCatName());
        }
    }

    // Přidává posluchače pro změny ve výběru kategorie, aby aktualizoval položky v comboBoxu položek
    private void addCategoryComboBoxListener(JComboBox<String> categoryComboBox, JComboBox<String> itemComboBox) {
        categoryComboBox.addActionListener(e -> updateItemComboBox(itemComboBox, categoryComboBox));
    }

    // Aktualizuje comboBox položek na základě vybrané kategorie.
    private void updateItemComboBox(JComboBox<String> itemComboBox, JComboBox<String> categoryComboBox) {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        int categoryId = model.getCategories().getCategoryIdByName(selectedCategory);

        List<Item> itemsInCategory = model.getItems().getItemsByCategory(categoryId);

        itemComboBox.removeAllItems();
        for (Item item : itemsInCategory) {
            if (item.getCategoryID() == categoryId && item.isActive()) {
                itemComboBox.addItem(item.getName());
            }
        }
    }

    // Umožňuje nastavit datum pro využití položky v dialogu.
    private void addDate(AddOrderDialog addOrder) {
        addOrder.setEditableItem();
    }

    // Vytváří objekt ItemUsage na základě dat z dialogu.
    public ItemUsage createItemUsage(AddOrderDialog addOrder) throws Exception {
        validateFields(addOrder);

        String selectedItem = (String) addOrder.getItemComboBox().getSelectedItem();
        int itemId = model.getItems().getItemIdByName(selectedItem).getItemID();
        int quantity = Integer.parseInt(addOrder.getAmountFiel().getText());
        BigDecimal pricePerPiece = new BigDecimal(addOrder.getPriceField().getText());
        Date purchaseDate = null;
        try {
            purchaseDate = dateFormat.parse(addOrder.getDateField().getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ItemUsage itemUsage = new ItemUsage(itemId, quantity, pricePerPiece, purchaseDate, null);

        return itemUsage;
    }

    // Přidává novou položku do seznamu využití a aktualizuje tabulku v dialogu.    
    private void addItem(AddOrderDialog addOrder) {
        try {
            addOrder.enableButtonAdd();
            ItemUsage itemUsage = createItemUsage(addOrder);
            itemUsage.setUsageDate(Optional.ofNullable(itemUsage.getUsageDate()).orElse(null));
            itemUsagesList.add(itemUsage);
            addToOrderTableView(itemUsage, addOrder);
        } catch (Exception ex) {
            showErrorDialog("Chyba při ukládání objektů");
        }
    }

    // Přidává položku do tabulky v dialogu AddOrderDialog.
    private void addToOrderTableView(ItemUsage itemUsage, AddOrderDialog addOrder) {
        String itemName = this.model.getItems().getItemById(itemUsage.getItemId()).getName();
        Integer quantity = itemUsage.getQuantity();
        addOrder.getTableModel().addRow(new Object[]{itemName, quantity});
    }

    // Přidává položku do hlavní tabulky v aplikaci.
    private void addToTable(ItemUsage itemUsage) {
        DefaultTableModel modelt = (DefaultTableModel) view.getTable().getModel();
        Object[] row = {model.getItems().getItemById(itemUsage.getItemId()).getName(),
            itemUsage.getQuantity(),
            dateFormat.format(itemUsage.getPurchaseDate())};
        modelt.addRow(row);
    }

    // Uzavírá dialog AddOrderDialog a ukládá všechny změny.
    private void closeDialog(AddOrderDialog addOrder) {
        Iterator<ItemUsage> iterator = itemUsagesList.iterator();
        while (iterator.hasNext()) {
            ItemUsage itemUsage = iterator.next();
            try {
                model.getItemUsages().addItemUsage(itemUsage);
                addToTable(itemUsage);
                iterator.remove();
                addOrder.setVisible(false);
                showDialog("Položky byly úspěšně uloženy.");
            } catch (SQLException ex) {
                showErrorDialog("Uložení položek nebylo možné");
            }
        }
    }

    // Ukládá změny pro vybranou položku.
    private void saveChanges() {
        if (currentlySelectedItem != null) {

            System.out.println(currentlySelectedItem.getPurchaseDate());

            SwingUtilities.invokeLater(() -> {
                try {
                    Date date = dateFormat.parse(view.getConsumedDateField().getText());
                    updateItem(currentlySelectedItem, date);
                } catch (ParseException ex) {
                    showErrorDialog("Nesprávný formát data");
                }
            });
        } else {
            showErrorDialog("Nebyla vybrána žádná položka");
        }
    }

    // Aktualizuje informace o využití položky v modelu.
    private void updateItem(ItemUsage itemUsage, Date date) {
        try {
            itemUsage.setUsageDate(date);
            model.getItemUsages().updateItemUsage(itemUsage);
            showDialog("Změna byla úspěšně uložena.");
        } catch (SQLException ex) {
            showErrorDialog("Došlo k problému při ukládání ");
        }
    }

    // Zobrazuje informace o vybrané položce v postranním panelu.
    private void showUsageInfo() {
        try {
            setPanelForShowItemInfo(model.getItemUsages().getItemUsageBySomeInfo(selectItemFromTable(), dateFormat));
        } catch (ParseException ex) {
            showErrorDialog("Nebylo možné zobrazit informace");
        }
    }

    // Vybere data o položce z vybraného řádku v tabulce.
    private Object[] selectItemFromTable() {
        selectedRowIndex = view.getTable().getSelectedRow();
        if (selectedRowIndex != -1) {

            System.out.println("provedeno");

            int modelIndex = view.getTable().convertRowIndexToModel(selectedRowIndex);
            int columnCount = view.getTableModel().getColumnCount();
            Object[] rowData = new Object[columnCount];
            rowData[0] = Integer.toString(model.getItems().getItemIdByName(view.getTableModel().getValueAt(modelIndex, 0).toString()).getItemID());
            rowData[1] = view.getTableModel().getValueAt(modelIndex, 1).toString();
            rowData[2] = view.getTableModel().getValueAt(modelIndex, 2).toString();

            return rowData;
        }
        return null;
    }

    // Nastavuje panel pro zobrazení informací o vybrané položce.
    private void setPanelForShowItemInfo(ItemUsage itemUsage) {
        //System.out.println(itemUsage.getPurchaseDate());

        if (itemUsage != null) {
            view.getItemField().setText(model.getItems().getItemById(itemUsage.getItemId()).getName());
            view.getAmountField().setText(Integer.toString(itemUsage.getQuantity()));
            view.getPriceField().setText(itemUsage.getSum().toString());
            view.getOrderDateField().setText(dateFormat.format(itemUsage.getPurchaseDate()));
            if (itemUsage.getUsageDate() != null) {
                view.getConsumedDateField().setText(dateFormat.format(itemUsage.getUsageDate()));
            } else {
                view.getConsumedDateField().setText("Zadejte");
            }

            currentlySelectedItem = itemUsage;
        }
    }

    // Nastavuje pravidla pro řazení položek v tabulce.
    private void sortInTable() {
        view.getSorter().setComparator(0, Comparator.naturalOrder());
        view.getSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        view.getSorter().setComparator(1, Comparator.naturalOrder());
        view.getSorter().setComparator(2, Comparator.naturalOrder());
        view.getTable().setRowSorter(view.getSorter());
    }

    // Vrací panel s view pro tento controller.
    @Override
    public JPanel getView() {
        return view;
    }

    // Načítá data znovu na základě zadaného roku.
    private void reloadData() {
        try {
            String year = validate();
            model.getItemUsages().loadUsagesFromDatabase(Integer.parseInt(year));
            SwingUtilities.invokeLater(() -> {
                clearTable();
                model.getItemUsages().getAllUsages().forEach(this::addRowIntoTable);
                view.getTableModel().fireTableDataChanged();
            });
        } catch (IllegalArgumentException | SQLException ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    // Ověřuje, zda je vstupní rok platný, a vyvolává výjimku v případě neplatného formátu.
    private String validate() throws IllegalArgumentException {
        String textForSearch = view.getTextForSearchField().getText().trim();
        if (!textForSearch.matches("\\d{4}")) {
            throw new IllegalArgumentException("Zadejte platný rok ve formátu RRRR.");
        }
        return textForSearch;
    }

    // Čistí data v tabulce, odstraňuje všechny řádky.
    private void clearTable() {
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        model.setRowCount(0);
    }

    // Provádí validaci polí ve formuláři AddOrderDialog a zobrazuje chybové hlášení v případě problémů.
    private boolean validateFields(AddOrderDialog addOrder) {
        if (addOrder.getCategoryComboBox().getSelectedItem() == null) {
            showErrorDialog("Vyberte kategorii");
            return false;
        }

        if (addOrder.getItemComboBox().getSelectedItem() == null) {
            showErrorDialog("Vyberte položku");
            return false;
        }

        if (addOrder.getAmountFiel().getText().isEmpty()) {
            showErrorDialog("Zadejte množství");
            return false;
        }

        if (addOrder.getPriceField().getText().isEmpty()) {
            showErrorDialog("Zadejte cenu");
            return false;
        }

        String dateString = addOrder.getDateField().getText();
        try {
            dateFormat.parse(dateString);
        } catch (ParseException e) {
            showErrorDialog("Nesprávný formát data, použijte yyyy-MM-dd");
            return false;
        }
        return true;
    }

    // Zobrazuje dialogové okno s chybovým hlášením.
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Chyba", JOptionPane.ERROR_MESSAGE);
    }

    // Zobrazuje dialogové okno s chybovým hlášením.
    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Informace", JOptionPane.INFORMATION_MESSAGE);
    }
}
