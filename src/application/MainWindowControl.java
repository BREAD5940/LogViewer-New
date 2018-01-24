package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.controlsfx.control.table.TableFilter;

import application.reading.LogReader;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;


public class MainWindowControl implements Initializable{
	
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		table.minHeightProperty().bind(Main.primaryStage.heightProperty());
		table.minWidthProperty().bind(Main.primaryStage.widthProperty());
	}
}
