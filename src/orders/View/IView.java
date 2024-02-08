/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package orders.View;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author hrusk
 */
public interface IView {
    public JTable getTable();
    public DefaultTableModel getTableModel();
    public TableRowSorter<DefaultTableModel> getSorter();
}
