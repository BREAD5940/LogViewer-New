package application.reading;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LogLine {
	
	private long time;
	private long runtime;
	private String thread;
	private String clazz;
	private String label;
	private String type;
	private Object data1;
	private Object data2;
	private Object data3;
	
	public LogLine(JsonObject jsonObject) {
		try{
			this.time = ((JsonObject) jsonObject.get("metadata")).get("time").getAsLong();
			this.runtime = ((JsonObject) jsonObject.get("metadata")).get("runtime").getAsLong();
			this.thread = ((JsonObject) jsonObject.get("metadata")).get("thread").getAsString();
			this.clazz = ((JsonObject) ((JsonObject) jsonObject.get("metadata")).get("source")).get("class").getAsString();
			if(((JsonObject) ((JsonObject) jsonObject.get("metadata")).get("source")).has("label")) {
				this.label = ((JsonObject) ((JsonObject) jsonObject.get("metadata")).get("source")).get("label").getAsString();
			}
			if(((JsonObject) jsonObject.get("metadata")).has("type")) {
				JsonArray typeArray = ((JsonObject) jsonObject.get("metadata")).get("type").getAsJsonArray();
				this.type = typeArray.toString();
				for(JsonElement e:typeArray) {
					if(e.toString().contains("value")) {
						if(((JsonObject) jsonObject.get("data")).has("value")) {
							this.data1 = ((JsonObject) jsonObject.get("data")).get("value");
						}
						if(((JsonObject) jsonObject.get("data")).has("unit")) {
							this.data2 = ((JsonObject) jsonObject.get("data")).get("unit").getAsString();
						}
					}
					if(e.toString().contains("event")) {
						if(((JsonObject) jsonObject.get("data")).has("name")) {
							this.data1 = ((JsonObject) jsonObject.get("data")).get("name").getAsString();
							
						}
						if(((JsonObject) jsonObject.get("data")).has("message")) {
							if(data2 == null) {
								this.data2 = ((JsonObject) jsonObject.get("data")).get("message").getAsString();
							} else if(data3 == null) {
								this.data3 = ((JsonObject) jsonObject.get("data")).get("message").getAsString();
							}
						}
						if(((JsonObject) jsonObject.get("data")).has("args")) {
							if(data2 == null) {
								this.data2 = ((JsonObject) jsonObject.get("data")).get("args");
							} else if(data3 == null) {
								this.data3 = ((JsonObject) jsonObject.get("data")).get("args");
							}
						}
						if(((JsonObject) jsonObject.get("data")).has("stack_trace")) {
							if(data2 == null) {
								this.data2 = ((JsonObject) jsonObject.get("data")).get("stack_trace");
							} else if(data3 == null) {
								this.data3 = ((JsonObject) jsonObject.get("data")).get("stack_trace");
							}
						}
					}
				}
			}
			
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
		
	}

	public long getTime() {
		return time;
	}

	public long getRuntime() {
		return runtime;
	}

	public String getThread() {
		return thread;
	}

	public String getClazz() {
		return clazz;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}

	public Object getData1() {
		return data1;
	}

	public Object getData2() {
		return data2;
	}

	public Object getData3() {
		return data3;
	}
	
}
