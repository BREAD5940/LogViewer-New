package application;

import java.io.File;
import java.io.IOException;

import org.controlsfx.control.table.TableFilter;

import application.reading.LogLine;
import application.reading.LogReader;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainWindowControl {
	
	@FXML
	public TableView<LogLine> table;
	@FXML
	private TableColumn<LogLine, Long> timeCol;
	@FXML
	private TableColumn<LogLine, Long> runtimeCol;
	@FXML
	private TableColumn<LogLine, String> threadCol;
	@FXML
	private TableColumn<LogLine, String> clazzCol;
	@FXML
	private TableColumn<LogLine, String> labelCol;
	@FXML
	private TableColumn<LogLine, String> typeCol;
	@FXML
	private TableColumn<LogLine, Object> data1Col;
	@FXML
	private TableColumn<LogLine, Object> data2Col;
	@FXML
	private TableColumn<LogLine, Object> data3Col;
	@FXML
	private NumberAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private LineChart<Number, Number> lineChart;
	
	ObservableList<LogLine> log;
	
	public void chooseFile() throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Log File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files (*.json)", "*.json"));
		File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);
		LogReader reader = new LogReader();
		log = reader.readLog(selectedFile);
		updateLogTable();
	}
	
	public void updateLogTable() {
		timeCol.setCellValueFactory(new PropertyValueFactory<LogLine, Long>("time"));
		runtimeCol.setCellValueFactory(new PropertyValueFactory<LogLine, Long>("runtime"));
		threadCol.setCellValueFactory(new PropertyValueFactory<LogLine, String>("thread"));
		clazzCol.setCellValueFactory(new PropertyValueFactory<LogLine, String>("clazz"));
		labelCol.setCellValueFactory(new PropertyValueFactory<LogLine, String>("label"));
		typeCol.setCellValueFactory(new PropertyValueFactory<LogLine, String>("type"));
		data1Col.setCellValueFactory(new PropertyValueFactory<LogLine, Object>("data1"));
		data2Col.setCellValueFactory(new PropertyValueFactory<LogLine, Object>("data2"));
		data3Col.setCellValueFactory(new PropertyValueFactory<LogLine, Object>("data3"));
		table.setItems(log);
		
        TableFilter<LogLine> tableFilter = TableFilter.forTableView(table).lazy(false).apply();

        tableFilter.setSearchStrategy((input,target) -> {
            try {
                return target.matches(input);
            } catch (Exception e) {
                return false;
            }
        });
        

		
		
	}
//
//	public static void initializeTableSize() {
//		table.prefHeightProperty().bind(Main.primaryStage.heightProperty());
//		table.prefWidthProperty().bind(Main.primaryStage.widthProperty());
//
//	}
}
