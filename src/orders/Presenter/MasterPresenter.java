/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Presenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.SwingWorker;
import orders.Model.MasterModel;
import orders.View.*;

/**
 *
 * @author hrusk
 */
public class MasterPresenter {

    private final MainView view;
    private final MasterModel model;

    public MasterPresenter(MasterModel model, MainView view) {
        this.model = model;
        this.view = view;       
    }
    
    public void start(){
       loadDataAtStart();
        initControllers();
    }

    private void loadDataAtStart() {
        view.showLoadingIndicator();
        SwingWorker<Void, Void> dataLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                model.getCategories().loadCategoriesFromDatabase();
                model.getItems().loadItemsFromDatabase();
                model.getItemUsages().loadUsagesFromDatabase(Calendar.getInstance().get(Calendar.YEAR));
                return null;
            }

            @Override
            protected void done() {
                view.hideLoadingIndicator();
            }
        };
        dataLoader.execute();
    }

    private void initControllers(){
        List<IPresenter> controllers = new ArrayList<>();
        controllers.add(new ItemPresenter(model));        
        controllers.add(new PurchasePresenter(model));
        addViewsToMainView(controllers);
    }

    private void addViewsToMainView(List<IPresenter> controllers) {
        for (IPresenter controller : controllers) {
            view.getjTabbedPane().addTab(controller.getView().getName(), controller.getView());
        }
    }

}
