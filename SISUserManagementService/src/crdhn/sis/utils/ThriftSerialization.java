/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */

package crdhn.sis.utils;


import java.lang.reflect.Method;

import java.lang.reflect.Field;

import java.lang.reflect.ParameterizedType;

import java.util.ArrayList;

import java.util.EnumSet;

import java.util.HashMap;

import java.util.HashSet;

import java.util.Iterator;

import java.util.List;

import java.util.Map;

import java.util.Set;

import org.json.JSONArray;

import org.json.JSONException;

import org.json.JSONObject;


/**

 *

 * @author cuongnc

 */

public class ThriftSerialization {


	public static JSONObject toJSON(Object thriftobject) {

		JSONObject ret = new JSONObject();

		if (thriftobject == null){

			return null;

		}

		Class clazz = thriftobject.getClass();

		try {

			for (Class subclazz : clazz.getClasses()) {

				if (subclazz.getName().contains("$_Fields")) {

					Method getFieldNameMethod = subclazz.getMethod("getFieldName");

					Method getFieldValueMethod = clazz.getMethod("getFieldValue", subclazz);

					for (Iterator it = EnumSet.allOf(subclazz).iterator(); it.hasNext();) {

						Object field = it.next();

						String key = (String) getFieldNameMethod.invoke(field);

						

						//value object

						Object object = getFieldValueMethod.invoke(thriftobject, field);

						if(object == null){

							ret.put(key, JSONObject.NULL);

						}

						else if((object instanceof Boolean )||(object instanceof Byte ) ||  

							(object instanceof Short ) ||(object instanceof Integer ) ||

							(object instanceof Long ) || (object instanceof Double ) || 

							(object instanceof String )

						){

							ret.put(key, object);

						}else if (object instanceof List <?>) {

							JSONArray jarray = toJSON((List)object);

							ret.put(key, jarray);

						}else if (object instanceof Set <?>) {

							JSONArray jarray = toJSON((Set)object);

							ret.put(key, jarray);

						}else if (object instanceof Map <?,?>) {

							JSONObject obj = toJSON((Map)object);

							ret.put(key, obj);

						}

						else{

							JSONObject obj = toJSON(object);

							ret.put(key, obj);

						}

					}

					return ret;

				}

			}

		}

		catch (Exception ex) {

			System.out.printf(ex.getMessage());

		}

		return null;

	}


	public static JSONArray toJSON(List<?> list) {

		JSONArray ret = new JSONArray();

		try {

			for (int i = 0; i < list.size(); i++) {

				Object object = list.get(i);


				if(object == null){

					ret.put(JSONObject.NULL);

				}else if((object instanceof Boolean )||(object instanceof Byte ) ||  

					(object instanceof Short ) ||(object instanceof Integer ) ||

					(object instanceof Long ) || (object instanceof Double ) || 

					(object instanceof String )

				){

					ret.put(object);

				}else if (object instanceof List <?>) {

					JSONArray _array = toJSON((List)object);

					ret.put(_array);

				}else if (object instanceof Set <?>) {

					JSONArray _array = toJSON((Set)object);

					ret.put(_array);

				}else if (object instanceof Map <?,?>) {

					JSONObject _obj = toJSON((Map)object);

					ret.put( _obj);

				}else{

					JSONObject _obj = toJSON(object);

					ret.put(_obj);

				}

			}

		}

		catch (Exception ex) {

			System.out.printf(ex.getMessage());

		}

		return ret;

	}

	

	public static JSONArray toJSON(Set<?> set) {

		JSONArray ret = new JSONArray();

		try {

			for (Object object : set) {

				if(object == null){

					ret.put(JSONObject.NULL);

				}else if((object instanceof Boolean )||(object instanceof Byte ) ||  

					(object instanceof Short ) ||(object instanceof Integer ) ||

					(object instanceof Long ) || (object instanceof Double ) || 

					(object instanceof String )

				){

					ret.put(object);

				}else if (object instanceof List <?>) {

					JSONArray _array = toJSON((List)object);

					ret.put(_array);

				}else if (object instanceof Set <?>) {

					JSONArray _array = toJSON((Set)object);

					ret.put(_array);

				}else if (object instanceof Map <?,?>) {

					JSONObject _obj = toJSON((Map)object);

					ret.put( _obj);

				}else{

					JSONObject _obj = toJSON(object);

					ret.put(_obj);

				}

			}

		}

		catch (Exception ex) {

			System.out.printf(ex.getMessage());

		}

		return ret;

	}

	public static JSONObject toJSON(Map<?,?> map) {

		JSONObject ret = new JSONObject();

		try {

			for (Map.Entry<?, ?> entry : map.entrySet()) {

				Object object = entry.getValue();

				String key = String.valueOf(entry.getKey());

				if(object == null){

					ret.put(key, JSONObject.NULL);

				}else if((object instanceof Boolean )||(object instanceof Byte ) ||  

					(object instanceof Short ) ||(object instanceof Integer ) ||

					(object instanceof Long ) || (object instanceof Double ) || 

					(object instanceof String )

				){

					ret.put(key, object);

				}else if (object instanceof List <?>) {

					JSONArray _array = toJSON((List)object);

					ret.put(key, _array);

				}else if (object instanceof Set <?>) {

					JSONArray _array = toJSON((Set)object);

					ret.put(key, _array);

				}else if (object instanceof Map <?,?>) {

					JSONObject _obj = toJSON((Map)object);

					ret.put(key, _obj);

				}else{

					JSONObject _obj = toJSON(object);

					ret.put(key, _obj);

				}

			}

		}

		catch (Exception ex) {

			System.out.printf(ex.getMessage());

		}

		return ret;

	}


	

	

	

	

	//json to thrift

	public static Object fromJSON(JSONObject jobject, Class clazz) {

		try {

			if(jobject == null){

				return null;

			}

			

			Object instance = clazz.newInstance();

			

			for (Class subclazz : clazz.getClasses()) {

				if (subclazz.getName().contains("$_Fields")) {

					Method getFieldNameMethod = subclazz.getMethod("getFieldName");

					Method setFieldValueMethod = clazz.getMethod("setFieldValue", subclazz, Object.class);


					for (Iterator it = EnumSet.allOf(subclazz).iterator(); it.hasNext();) {

						Object field = it.next();

						String fieldname = (String) getFieldNameMethod.invoke(field);

						Field clazzfield = clazz.getDeclaredField(fieldname);

						Class fieldtype = clazzfield.getType();

						

						if(fieldtype == Boolean.TYPE) {

							try{

								Object value = jobject.getBoolean(fieldname);

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}else if(fieldtype == Byte.TYPE) {

							try{

								Object value = Byte.parseByte(jobject.getString(fieldname)) ;

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

							

						}else if(fieldtype == Short.TYPE) {

							try{

								Object value = Short.parseShort(jobject.getString(fieldname)) ;

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}

						else if (fieldtype == Integer.TYPE) {

							try{

								Object value = jobject.getInt(fieldname);

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}else if(fieldtype == Long.TYPE) {

							try{

								Object value = jobject.getLong(fieldname);

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}else if(fieldtype == Double.TYPE) {

							try{

								Object value = jobject.getDouble(fieldname);

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}else if(fieldtype == String.class) {

							try{

								Object value = jobject.getString(fieldname);

								setFieldValueMethod.invoke(instance, field, value);

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}

						

						else if(List.class.isAssignableFrom(fieldtype)) {

							ParameterizedType listType= (ParameterizedType) clazzfield.getGenericType();

							Class elementClass = (Class) listType.getActualTypeArguments()[0];

							try{

								if(jobject.isNull(fieldname)){

									setFieldValueMethod.invoke(instance, field, new ArrayList());

								}else{

									JSONArray _jarray = jobject.getJSONArray(fieldname);

									List _list = fromJSONToList(_jarray, elementClass);

									setFieldValueMethod.invoke(instance, field, _list);

								}

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

							

						}

						else if(Set.class.isAssignableFrom(fieldtype)) {

							ParameterizedType listType= (ParameterizedType) clazzfield.getGenericType();

							Class elementClass = (Class) listType.getActualTypeArguments()[0];

							try{

								if(jobject.isNull(fieldname)){

									setFieldValueMethod.invoke(instance, field, new HashSet());

								}else{

									JSONArray _jarray = jobject.getJSONArray(fieldname);

									Set _set = fromJSONToSet(_jarray, elementClass);

									setFieldValueMethod.invoke(instance, field, _set);

								}

								

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}

						else if(Map.class.isAssignableFrom(fieldtype)) {

							ParameterizedType listType= (ParameterizedType) clazzfield.getGenericType();

							Class keyClass = (Class) listType.getActualTypeArguments()[0];

							Class elementClass = (Class) listType.getActualTypeArguments()[1];

							try{

								if(jobject.isNull(fieldname)){

									setFieldValueMethod.invoke(instance, field, new HashMap());

								}else{

									JSONObject _jobject = jobject.getJSONObject(fieldname);

									Map _map = fromJSONToMap(_jobject, keyClass, elementClass);

									setFieldValueMethod.invoke(instance, field, _map);

								}

							}catch (JSONException ex) {

								System.out.println("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}

						else {

							try{

								if(!jobject.isNull(fieldname)){

									JSONObject _jobject = jobject.getJSONObject(fieldname);

									Object _value = fromJSON(_jobject, fieldtype);

									setFieldValueMethod.invoke(instance, field, _value);

								}else{

									System.out.print("Can not serialize field '" +fieldname+ "' : Set Null object");

									setFieldValueMethod.invoke(instance, field, null);

								}

							}catch (Exception ex) {

								System.out.print("Can not serialize field '" +fieldname+ "' :" + ex.getMessage());

							}

						}

						

					}

					return instance;

					//break;

				}

			}

		}

		catch (Exception ex) {

			System.out.printf(ex.getMessage());

		}

		return null;

	}

	

	public static List fromJSONToList(JSONArray jarray, Class elementClass) {

		try{

			if((jarray != null)){

				List _list = new ArrayList();

				for (int i = 0; i < jarray.length(); i++){

					if(elementClass == Boolean.class) {

						try{

							Object value = jarray.getBoolean(i);

							_list.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Byte.class) {

						try{

							Object value = Byte.parseByte(jarray.getString(i)) ;

							_list.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Short.class) {

						try{

							Object value = Short.parseShort(jarray.getString(i)) ;

							_list.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

					else if (elementClass == Integer.class) {

						try{

							Object value = jarray.getInt(i);

							_list.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Long.class) {

						try{

							Object value = jarray.getLong(i);

							_list.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Double.class) {

						try{

							Object value = jarray.getDouble(i);

							_list.add(value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == String.class) {

						try{

							Object value = jarray.getString(i);

							_list.add(value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

					

					/*TODO: list, map, set*/

					else{

						try{

							Object value = fromJSON((JSONObject)jarray.get(i), elementClass);

							_list.add(value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

				}

				return _list;

			}

		}catch (Exception ex) {

			System.out.println("Can not serialize field :" + ex.getMessage());

		}

		return null;

	}

	

	

	public static Set fromJSONToSet(JSONArray jarray, Class elementClass) {

		try{

			if((jarray != null)){

				Set _set = new HashSet();

				for (int i = 0; i < jarray.length(); i++){

					if(elementClass == Boolean.class) {

						try{

							Object value = jarray.getBoolean(i);

							_set.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Byte.class) {

						try{

							Object value = Byte.parseByte(jarray.getString(i)) ;

							_set.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Short.class) {

						try{

							Object value = Short.parseShort(jarray.getString(i)) ;

							_set.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

					else if (elementClass == Integer.class) {

						try{

							Object value = jarray.getInt(i);

							_set.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Long.class) {

						try{

							Object value = jarray.getLong(i);

							_set.add(value);

						}catch (JSONException ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Double.class) {

						try{

							Object value = jarray.getDouble(i);

							_set.add(value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == String.class) {

						try{

							Object value = jarray.getString(i);

							_set.add(value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

					

					/*TODO: list, map, set*/

					else{

						try{

							Object value = fromJSON((JSONObject)jarray.get(i), elementClass);

							_set.add(value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

				}

				return _set;

			}

		}catch (Exception ex) {

			System.out.println("Can not serialize field :" + ex.getMessage());

		}

		return null;

	}

	

	

	public static Map fromJSONToMap(JSONObject jobject, Class keyClass, Class elementClass) {

		if(jobject == null){

			return null;

		}

		try{

			Map _map = new HashMap();

			Iterator<?> keys = jobject.keys();

			while( keys.hasNext() ) {

				String jkey = (String)keys.next();

				Object key = null;

				

				if(keyClass == Boolean.class) {

					try{

						key = Boolean.parseBoolean(jkey);

					}catch (Exception ex) {

						System.out.println("Can not serialize field :" + ex.getMessage());

					}

				}else if(keyClass == Byte.class) {

					try{

						key = Byte.parseByte(jkey) ;

					}catch (Exception ex) {

						System.out.println("Can not serialize field :" + ex.getMessage());

					}

				}else if(keyClass == Short.class) {

					try{

						key = Short.parseShort(jkey) ;

					}catch (Exception ex) {

						System.out.println("Can not serialize field :" + ex.getMessage());

					}

				}

				else if (keyClass == Integer.class) {

					try{

						key = Integer.parseInt(jkey) ;

					}catch (Exception ex) {

						System.out.println("Can not serialize field :" + ex.getMessage());

					}

				}else if(keyClass == Long.class) {

					try{

						key = Long.parseLong(jkey) ;

					}catch (Exception ex) {

						System.out.println("Can not serialize field :" + ex.getMessage());

					}

				}else if(keyClass == Double.class) {

					try{

						key = Double.parseDouble(jkey) ;

					}catch (Exception ex) {

						System.out.println("Can not serialize field :" + ex.getMessage());

					}

				}else if(keyClass == String.class) {

					key = jkey;

				}

				

				if(key != null){

					//value

					if(elementClass == Boolean.class) {

						try{

							Object value = jobject.getBoolean(jkey);

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Byte.class) {

						try{

							Object value = Byte.parseByte(jobject.getString(jkey)) ;

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Short.class) {

						try{

							Object value = Short.parseShort(jobject.getString(jkey)) ;

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

					else if (elementClass == Integer.class) {

						try{

							Object value = jobject.getInt(jkey);

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Long.class) {

						try{

							Object value = jobject.getLong(jkey);

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == Double.class) {

						try{

							Object value = jobject.getDouble(jkey);

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}else if(elementClass == String.class) {

						try{

							Object value = jobject.getString(jkey);

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

					

					/*TODO: list, map, set*/

					else{

						try{

							Object value = fromJSON((JSONObject)jobject.get(jkey), elementClass);

							_map.put(key, value);

						}catch (Exception ex) {

							System.out.println("Can not serialize field :" + ex.getMessage());

						}

					}

				}

			}

			return _map;

			

		}catch (Exception ex) {

			System.out.println("Can not serialize field :" + ex.getMessage());

		}

		return null;

	}

}