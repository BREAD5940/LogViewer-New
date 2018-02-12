package application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.controlsfx.control.table.TableFilter;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.sun.javafx.scene.control.skin.TableViewSkin;

import application.reading.LogReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
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
	@FXML
	private ChoiceBox<String> choiceBox;
	@FXML
	private AnchorPane anchorPane;

	
	ObservableList<HashMap<String, Object>> log = FXCollections.observableArrayList();
	
	ObservableList<HashMap<String, Object>> data = FXCollections.observableArrayList();
	
	Set<String> oldText = null;
	
	LogReader reader;
	
	public MainWindowControl() {
		reader = new LogReader();
	}
    
	public void chooseFile() throws IOException, NullPointerException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Log File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files (*.json)", "*.json"));
		File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);		
		log.addAll(reader.readLog(selectedFile));
		updateLogTable();
		updateChoiceBox();
		oldText = reader.text;
	}
	
	private void updateGraph(String label) {
		XYChart.Series series = new XYChart.Series<>();
		lineChart.getData().clear();
		lineChart.setTitle(label);
		for(HashMap<String, Object> point:data) {
			if (point.containsKey("label")) {
				if (point.get("label").equals(label)) {
					final XYChart.Data<Double, Double> dataPoint = new XYChart.Data<>(((Number) point.get("time")).doubleValue(), ((Number) point.get("value")).doubleValue());
					series.getData().add(dataPoint);
					dataPoint.setNode(new HoveredThresholdNode(((Number) point.get("time")).doubleValue(), ((Number) point.get("value")).doubleValue()));
				} 
			}
		}
		lineChart.getData().add(series);
	}
	
	private void updateChoiceBox() {
		Set<String> choiceSet = new LinkedHashSet<String>();
		for(HashMap<String, Object> map:log) {
			HashMap<String,Object> point = new HashMap<String,Object>();
			if(((JsonArray) map.get("type")).size() > 1 && ((JsonArray) map.get("type")).get(0).getAsString().equals("value")) {
				
				Object value = (JsonPrimitive) map.get("value");
				if(((JsonPrimitive) value).isBoolean()) {  
					if(((JsonPrimitive) value).getAsBoolean() == true) {
						value = 1;
					} else {
						value = 0;
					}
					
				} else if(((JsonPrimitive) value).isNumber()) {
					value = ((JsonPrimitive) value).getAsDouble();
					
				}
				double time =  ((JsonPrimitive) map.get("runtime")).getAsDouble()/1000000;
	
				if(map.containsKey("label")) {
					String topLabel = ((JsonArray)map.get("label")).get(0).getAsString();
					choiceSet.add(topLabel);
					point.put("label", topLabel);
				}
				
				if(map.containsKey("unit")) {
					point.put("unit", map.get("unit"));
				}
				
				point.put("value", value);
				point.put("time", time);
			}
			data.add(point);
		}
		choiceBox.getItems().addAll(choiceSet);
		
		choiceBox.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue ov, String value, String new_value) {
						updateGraph(new_value);
					}
		});
		
	}
	

	public void updateLogTable() {

		constructNewColumns();
		
		table.setItems(log);
		
        setupFilter();
		
        sizeFit();
        
        
	}

	private void constructNewColumns() {
		Set<String> newText = reader.text;
		
		if(oldText != null) {
			newText.removeAll(oldText);
		}
		
		
		for(String name:newText) {
			
			TableColumn<HashMap<String, Object>, Object> column = new TableColumn<HashMap<String, Object>, Object>(name);
			column.setCellValueFactory(new MapValueFactory(name));
			table.getColumns().add(column);
			
		}
	}

	private void sizeFit() {
		TableViewSkin<?> skin = (TableViewSkin<?>) table.getSkin();
        
        for(TableColumn column:table.getColumns()) {
//        	column.minWidthProperty().bind(table.minWidthProperty().divide(table.getColumns().size()));	
        	try {	
	        	if (column != null) {
	        		Method method;
					
					method = skin.getClass().getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
					method.setAccessible(true);
					method.invoke(skin, column, 30);
	        	}
	        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

	private void setupFilter() {
		TableFilter<HashMap<String, Object>> tableFilter = TableFilter.forTableView(table).lazy(false).apply();

        tableFilter.setSearchStrategy((input,target) -> {
            try {
                return target.matches(input);
            } catch (Exception e) {
                return false;
            }
        
        });
	}
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		table.minHeightProperty().bind(Main.primaryStage.heightProperty());
		table.minWidthProperty().bind(Main.primaryStage.widthProperty());
//		final double SCALE_DELTA = 1.1;
//		lineChart.setOnScroll(new EventHandler<ScrollEvent>() {
//		    public void handle(ScrollEvent event) {
//		        event.consume();
//
//		        if (event.getDeltaY() == 0) {
//		            return;
//		        }
//
//		        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
//
//		        lineChart.setScaleX(lineChart.getScaleX() * scaleFactor);
//		        lineChart.setScaleY(lineChart.getScaleY() * scaleFactor);
//		    }
//		});
//
//		lineChart.setOnMousePressed(new EventHandler<MouseEvent>() {
//		    public void handle(MouseEvent event) {
//		        if (event.getClickCount() == 2) {
//		            lineChart.setScaleX(1.0);
//		            lineChart.setScaleY(1.0);
//		        }
//		    }
//		});
	}

}

