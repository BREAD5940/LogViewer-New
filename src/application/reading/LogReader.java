package application.reading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LogReader {

	public Set<String> text = new LinkedHashSet<String>();
	
	public LogReader() {
		
	}
	
	public ObservableList<HashMap<String, Object>> readLog(File file) throws IOException {
		String line;
		ObservableList<HashMap<String, Object>> log = FXCollections.observableArrayList();
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		line = bufferedReader.readLine().replace("\n", "");
		while(line != null) {
			log.add(readLine(line));
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		return log;
	}

	private HashMap<String,Object> readLine(String line) {
		JsonParser parser = new JsonParser();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		Set<Map.Entry<String,JsonElement>> metadata = ((JsonObject) ((JsonObject) parser.parse(line)).get("metadata")).entrySet();
		Set<Map.Entry<String,JsonElement>> data = ((JsonObject) ((JsonObject) parser.parse(line)).get("data")).entrySet();
		Iterator<Map.Entry<String, JsonElement>> dataIterator = data.iterator();
		Iterator<Map.Entry<String, JsonElement>> metadataIterator = metadata.iterator();
		
		while(metadataIterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = metadataIterator.next();
			map.put(entry.getKey(), entry.getValue());
			text.add(entry.getKey());
			
		}
		
		while(dataIterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = dataIterator.next();
			map.put(entry.getKey(), entry.getValue());
			text.add(entry.getKey());
		}
			
		return map;
	}

	
}
//package application.reading;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import com.google.gson.Gson;
//import com.google.gson.stream.JsonReader;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
//public class LogReader {
//	File file;
//	Gson gson;
//	JsonReader reader;
//	private long time;
//	private long runtime;
//	private String thread;
//	private String className;
//	private String label;
//	private String type = "message";
//	private Object data1 = null;
//	private String data2 = null;
//	private String data3 = null;
//	
//	
//	public LogReader(File file) throws FileNotFoundException {
//		this.file = file;
//		this.gson = new Gson();
//		FileReader fileReader = new FileReader(file);
//		this.reader = new JsonReader(fileReader);
//
//	}
//	
//	public LogLine readLine() throws IOException {
//		reader.beginObject();
//		String name = reader.nextName();
//		if(name.equals("metadata")) {
//			readMetadata();
//		}
//		name = reader.nextName();
//		if(name.equals("data")) {
//			readData();
//		}
//		
//		reader.endObject();
//		System.out.println(time + runtime + thread + className + label + type + data1 + data2 + data3);
//		return new LogLine(time, runtime, thread, className, label, type, data1, data2, data3);
//		
//		
//	}
//
//	private void readData() throws IOException {
//		reader.beginObject();
//		while(reader.hasNext()) {
//			String name = reader.nextName();	
//					
//			if(name.equals("value")) {
//				data1 = reader.nextDouble();
//			}
//			if(name.equals("name") || name.equals("message") || name.equals("stack_trace")) {
//				if(data1 != null) {
//					data1 = name + ": " + reader.nextString();
//				} else if(data2 != null) {
//					data2 = name + ": " + reader.nextString();
//				} else if(data3 != null) {
//					data3 = name + ": " + reader.nextString();
//				} else {
//					reader.skipValue();
//				}
//
//			}
//			System.out.println(data1);
//			
//		}
//		reader.endObject();
//		
//	}
//
//	private void readMetadata() throws IOException {
//		reader.beginObject();
//		while(reader.hasNext()) {
//			String name = reader.nextName();
//			if(name.equals("time")) {
//				time = reader.nextLong();
//				System.out.println(time);
//			} else if(name.equals("runtime")) {
//				runtime = reader.nextLong();
//				System.out.println(runtime);
//			} else if(name.equals("thread")) {
//				thread = reader.nextString();
//				System.out.println(thread);
//			} else if(name.equals("source")) {
//				reader.beginObject();
//				while(reader.hasNext()) {
//					name = reader.nextName();
//					if(name.equals("class")) {
//						className = reader.nextString();
//						System.out.println(className);
//					}
//					if(name.equals("label")) {
//						label = reader.nextString();
//						System.out.println(label);
//					}
//				}
//				reader.endObject();
//			} else if(name.equals("type")) {
//				ArrayList<String> typeArray = new ArrayList<String>();
//				reader.beginArray();
//				while(reader.hasNext()) {
//					typeArray.add(reader.nextString());
//				}
//				reader.endArray();
//				for(String typePart:typeArray) {
//					type = type + "." + typePart;
//				}
//				System.out.println(type.toString());
//				
//			} else {
//				reader.skipValue();
//			}
//			
//		}
//		reader.endObject();
//		
//	}
//	
//	public ObservableList<LogLine> readLog() throws IOException {
//		ObservableList<LogLine> log = FXCollections.observableArrayList();
//		reader.beginArray();
//		while(reader.hasNext()) {
//			LogLine line = readLine();
//			log.add(line);
//		}
//		reader.endArray();
//		return log;
//		
//	}
//}


