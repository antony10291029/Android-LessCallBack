package org.lxz.utils.json;
public abstract class Generic<T> {

	public Generic(String json) {
		// TODO Auto-generated constructor stub
		/**继承关系时注意父类传值this*/
		jsonBean = new JsonPathGeneric<T>(this, json, "$."){}.getObject();
	}
	
	public Generic(String json, String jsonPath) {
		// TODO Auto-generated constructor stub
		/**继承关系时注意父类传值this*/
		jsonBean = new JsonPathGeneric<T>(this, json, jsonPath){}.getObject();
	}

	public T jsonBean;

	public T getJsonBean() {
		return jsonBean;
	}

	public void setJsonBean(T jsonBean) {
		this.jsonBean = jsonBean;
	}

}
