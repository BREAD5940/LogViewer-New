package application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.controlsfx.control.table.TableFilter;

import com.sun.javafx.scene.control.skin.TableViewSkin;

import application.reading.LogReader;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class MainWindowControl {
	
	@FXML
	public TableView<HashMap<String, Object>> table;
	@FXML
	private NumberAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private LineChart<Number, Number> lineChart;
	
	ObservableList<HashMap<String, Object>> log;
	
	LogReader reader;
	
    private static Method columnToFitMethod;

    static {
        try {
            columnToFitMethod = TableViewSkin.class.getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
            columnToFitMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
	public void chooseFile() throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Log File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files (*.json)", "*.json"));
		File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);
		reader = new LogReader();
		log = reader.readLog(selectedFile);
		updateLogTable();
	}
	
	public void updateLogTable() {

		for(String name:reader.text) {
			TableColumn<HashMap<String, Object>, Object> column = new TableColumn<HashMap<String, Object>, Object>(name);
			column.setCellValueFactory(new MapValueFactory(name));
			table.getColumns().add(column);
		}
		table.setItems(log);
		
        TableFilter<HashMap<String, Object>> tableFilter = TableFilter.forTableView(table).lazy(false).apply();

        tableFilter.setSearchStrategy((input,target) -> {
            try {
                return target.matches(input);
            } catch (Exception e) {
                return false;
            }
        
        });
		
        for(TableColumn column:table.getColumns()) {
        	column.setMinWidth((int)((long)(Math.round(Main.primaryStage.getWidth() / table.getColumns().size()))));
        }
	}

	public void initialize() {
		table.minHeightProperty().bind(Main.primaryStage.heightProperty());
		table.minWidthProperty().bind(Main.primaryStage.widthProperty());

	}
}
