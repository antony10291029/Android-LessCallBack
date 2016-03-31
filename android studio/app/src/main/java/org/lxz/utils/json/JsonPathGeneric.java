package org.lxz.utils.json;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class JsonPathGeneric<T> {

	@SuppressWarnings("unchecked")
	public static String getGenericInString(String json, String jsonPath) {
		return new JsonPathGeneric<String>(String.class, json, jsonPath) {
		}.getObject();
	}

	public static int getGenericInInteger(String json, String jsonPath) {
		return new JsonPathGeneric<Integer>(String.class, json, jsonPath) {
		}.getObject();
	}

	public static double getGenericInDouble(String json, String jsonPath) {
		return new JsonPathGeneric<Double>(Double.class, json, jsonPath) {
		}.getObject();
	}

	public static Map<?, ?> getGenericInMap(String json, String jsonPath) {
		return new JsonPathGeneric<Map<?,?>>(Map.class, json, jsonPath) {
		}.getObject();
	}
	public static List<Map<?,?>> getGenericInListMap(String json, String jsonPath) {
		return new JsonPathGeneric<List<Map<?,?>>>(List.class, json, jsonPath) {
		}.getObject();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getGeneric(String json, String jsonPath, Class<T> clazz) {

		return new JsonPathGeneric<T>(clazz, json, jsonPath){}.getObject();
	}
	
	@SuppressWarnings("unchecked")
	public static <T>List<T> getGenericList(String json, String jsonPath,
			Type type) {
		String jsonResult = null;
		if (jsonPath == null || "".equals(jsonPath)) {
			jsonResult = json;
		} else {
			jsonResult = JsonPath.read(json, jsonPath).toString();
		}
		return (List<T>) new Gson().fromJson(jsonResult, type);
	}


	public JsonPathGeneric() {
	}

	private String json;
	private String jsonPath;
	private String jsonResult;
	private Type type;
	private T object;

	/** 继承关系时注意父类传值 */
	public JsonPathGeneric(Object obj, String json, String jsonPath) {
		if (obj instanceof Type) {
			parse((Type) obj, json, jsonPath);
		} else {
			Type type = null;
			try {
				type = ReflectionUtil.getParameterizedTypes(obj)[0];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parse(type, json, jsonPath);
		}
	}

	public JsonPathGeneric(Type type, String json, String jsonPath) {
		parse(type, json, jsonPath);
	}

	public JsonPathGeneric(String json, String jsonPath) {
		try {
			Type[] parameterizedTypes = ReflectionUtil
					.getParameterizedTypes(this);
			type = parameterizedTypes[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parse(type, json, jsonPath);
	}

	@SuppressWarnings("unchecked")
	public void parse(Type type, String json, String jsonPath) {
		this.json = json;
		this.jsonPath = jsonPath;
		this.type = type;
		if (jsonPath == null || "".equals(jsonPath)) {
			jsonResult = json;
		} else {
			this.jsonResult = JsonPath.read(json, jsonPath).toString();
		}
		Class<T> clazz = null;
		if (type != null) {
			try {
				clazz = (Class<T>) ReflectionUtil.getClass(type);
			} catch (ClassNotFoundException e) {
			}
		}
		if (clazz != null) {
			
			if (String.class.equals(clazz)) {
				object = (T) jsonResult;
			} else {
				object = new Gson().fromJson(jsonResult, clazz);
			}
		} else {
			if (type != null) {
				object = new Gson().fromJson(jsonResult, type);
			} else {
				object = (T) new Gson().fromJson(jsonResult, Object.class);
			}
		}

	}

	public String getJson() {
		return json;
	}

	public JsonPathGeneric<T> setJson(String json) {
		this.json = json;
		return this;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public JsonPathGeneric<T> setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
		return this;
	}

	public String getJsonResult() {
		return jsonResult;
	}

	public Type getType() {
		return type;
	}

	public JsonPathGeneric<T> setType(Type type) {
		this.type = type;
		return this;
	}

	public T getObject() {
		return object;
	}

	


}