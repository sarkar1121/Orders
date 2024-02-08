/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package orders;

import orders.Presenter.MasterPresenter;
import java.sql.SQLException;
import javax.swing.SwingUtilities;
import orders.Model.*;
import orders.View.*;

/**
 *
 * @author hrusk
 */
public class Orders {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainView view = new MainView();
                    MasterModel model= new MasterModel();
                    MasterPresenter presenter=new MasterPresenter(model, view);
                    presenter.start();
                } catch (SQLException e) {
                    System.out.println("Nastala chyba při práci s databází");
                }
            }
        });
    }
}
