package com.geekcap.swingx.treetable.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Json与javaBean之间的转换工具类
 * 
 * @author 晚风工作室 www.soservers.com
 * @version 20111221
 * 
 *          {@code   现使用json-lib组件实现
 *          需要
 *              json-lib-2.4-jdk15.jar
 *              ezmorph-1.0.6.jar
 *              commons-collections-3.1.jar
 *              commons-lang-2.0.jar
 *          支持
 * }
 */
public class JsonUtil {

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 * 
	 * @param jsonString
	 * @param beanCalss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToBean(String jsonString, Class<T> beanCalss) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		T bean = (T) JSONObject.toBean(jsonObject, beanCalss);
		return bean;
	}
	
	/**
	 * 
	 * @param jsonString
	 * @param beanCalss
	 * @param classMap 特殊类型的属性类型，默认都是String类型，map中的key是json中的key，map的value是对应的数据类型（class）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToBean(String jsonString, Class<T> beanCalss, Map<String, ?> classMap) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		T bean = (T) JSONObject.toBean(jsonObject, beanCalss, classMap);
		return bean;
	}

	/**
	 * 将java对象转换成json字符串
	 *
	 * @param bean
	 * @return
	 */
	public static String beanToJson(Object bean) {
		JSONObject json = JSONObject.fromObject(bean);
		return json.toString();
	}

	/**
	 * 将java对象转换成json字符串
	 *
	 * @param bean
	 * @return
	 */
	public static String beanToJson(Object bean, String[] _nory_changes, boolean nory) {
		JSONObject json = null;
		if (nory) {// 转换_nory_changes里的属性
			Field[] fields = bean.getClass().getDeclaredFields();
			String str = "";
			for (Field field : fields) {
				// System.out.println(field.getName());
				str += (":" + field.getName());
			}
			fields = bean.getClass().getSuperclass().getDeclaredFields();
			for (Field field : fields) {
				// System.out.println(field.getName());
				str += (":" + field.getName());
			}
			str += ":";
			for (String s : _nory_changes) {
				str = str.replace(":" + s + ":", ":");
			}
			json = JSONObject.fromObject(bean, configJson(str.split(":")));
		} else {// 转换除了_nory_changes里的属性
			json = JSONObject.fromObject(bean, configJson(_nory_changes));
		}
		return json.toString();
	}

	private static JsonConfig configJson(String[] excludes) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludes);
		jsonConfig.setIgnoreDefaultExcludes(false);
		// jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(datePattern));
		return jsonConfig;
	}

	/**
	 * 将java对象List集合转换成json字符串
	 * 
	 * @param beans
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String beanListToJson(List beans) {
		StringBuffer rest = new StringBuffer();
		rest.append("[");
		int size = beans.size();
		for (int i = 0; i < size; i++) {
			rest.append(beanToJson(beans.get(i)) + ((i < size - 1) ? "," : ""));
		}
		rest.append("]");
		return rest.toString();
	}

	/**
	 * 
	 * @param beans
	 * @param _no_changes
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String beanListToJson(List beans, String[] _nory_changes, boolean nory) {
		StringBuffer rest = new StringBuffer();
		rest.append("[");
		int size = beans.size();
		for (int i = 0; i < size; i++) {
			try {
				rest.append(beanToJson(beans.get(i), _nory_changes, nory));
				if (i < size - 1) {
					rest.append(",");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		rest.append("]");
		return rest.toString();
	}

	/**
	 * 从json HASH表达式中获取一个map，改map支持嵌套功能
	 *
	 * @param jsonString
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<String, Object> jsonToMap(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Iterator keyIter = jsonObject.keys();
		String key;
		Object value;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		while (keyIter.hasNext()) {
			key = (String) keyIter.next();
			value = jsonObject.get(key);
			valueMap.put(key, value);
		}
		return valueMap;
	}

	/**
	 * map集合转换成json格式数据
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToJson(Map<String, ?> map, String[] _nory_changes, boolean nory) {
//		String s_json = "{";
//		Set<String> key = map.keySet();
//		for (Iterator<?> it = key.iterator(); it.hasNext();) {
//			String s = (String) it.next();
//			if (map.get(s) == null) {
//				s_json += (s + ":" + "");
//			} else if (map.get(s) instanceof List<?>) {
//				s_json += (s + ":" + JsonUtil.beanListToJson((List<?>) map.get(s), _nory_changes, nory));
//			} else {
//				JSONObject json = JSONObject.fromObject(map);
//				s_json += (s + ":" + json.toString());
//			}
//			if (it.hasNext()) {
//				s_json += ",";
//			}
//		}
//		s_json += "}";
//		return s_json;
		return JSONObject.fromObject(map).toString();
	}
	
	public static String mapToJson(Map<String, ?> map, String[] ignoreKeys) {
		JsonConfig config = new JsonConfig();
		config.setExcludes(ignoreKeys);
		return JSONObject.fromObject(map, config).toString();
	}

	/**
	 * 从json数组中得到相应java数组
	 *
	 * @param jsonString
	 * @return
	 */
	public static Object[] jsonToObjectArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return jsonArray.toArray();
	}

	public static String listToJson(List<?> list) {
		JSONArray jsonArray = JSONArray.fromObject(list);
		return jsonArray.toString();
	}

	/**
	 * 从json对象集合表达式中得到一个java对象列表
	 *
	 * @param jsonString
	 * @param beanClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> jsonToBeanList(String jsonString, Class<T> beanClass) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		T bean;
		int size = jsonArray.size();
		List<T> list = new ArrayList<T>(size);
		for (int i = 0; i < size; i++) {
			jsonObject = jsonArray.getJSONObject(i);
			bean = (T) JSONObject.toBean(jsonObject, beanClass);
			list.add(bean);
		}
		return list;
	}

	/**
	 * 从json数组中解析出java字符串数组
	 *
	 * @param jsonString
	 * @return
	 */
	public static String[] jsonToStringArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] stringArray = new String[jsonArray.size()];
		int size = jsonArray.size();
		for (int i = 0; i < size; i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

	/**
	 * 从json数组中解析出javaLong型对象数组
	 *
	 * @param jsonString
	 * @return
	 */
	public static Long[] jsonToLongArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		int size = jsonArray.size();
		Long[] longArray = new Long[size];
		for (int i = 0; i < size; i++) {
			longArray[i] = jsonArray.getLong(i);
		}
		return longArray;
	}

	/**
	 * 从json数组中解析出java Integer型对象数组
	 *
	 * @param jsonString
	 * @return
	 */
	public static Integer[] jsonToIntegerArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		int size = jsonArray.size();
		Integer[] integerArray = new Integer[size];
		for (int i = 0; i < size; i++) {
			integerArray[i] = jsonArray.getInt(i);
		}
		return integerArray;
	}

	/**
	 * 从json数组中解析出java Double型对象数组
	 *
	 * @param jsonString
	 * @return
	 */
	public static Double[] jsonToDoubleArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		int size = jsonArray.size();
		Double[] doubleArray = new Double[size];
		for (int i = 0; i < size; i++) {
			doubleArray[i] = jsonArray.getDouble(i);
		}
		return doubleArray;
	}

//	public static void main(String[] args) {
//		String jsonStr = "{\"reqHeader\":{\"Host\":\"172.23.29.178:9013\",\"Connection\":\"keep-alive\",\"Upgrade-Insecure-Requests\":\"1\",\"User-Agent\":\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\",\"Accept\":\"text/html,application/xhtml xml,application/xml;q=0.9,image/webp,*/*;q=0.8\",\"Referer\":\"http://172.23.29.183:9014/\",\"Accept-Encoding\":\"gzip, deflate, sdch\",\"Accept-Language\":\"zh-CN,zh;q=0.8\",\"Cookie\":\"JSESSIONID=2A780FA1722E32FFA7FC09E7B516EE69; TGC=eyJhbGciOiJIUzUxMiJ9\"},\"method\":\"GET\",\"reqParams\":{\"service\":\"http://172.23.29.183:9014\"},\"reasonPhrase\":\"Found\",\"rspHeader\":{\"Server\":\"Apache-Coyote/1.1\",\"Cache-Control\":\"no-store\",\"Set-Cookie\":\"CASPRIVACY=\\\"\\\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure\",\"Pragma\":\"no-cache\",\"Expires\":\"Thu, 01 Jan 1970 00:00:00 GMT\",\"Location\":\"http://172.23.29.183:9014\",\"Content-Length\":\"0\",\"Date\":\"Tue, 14 Mar 2017 08:58:53 GMT\"},\"rspBody\":\"\",\"url\":\"172.23.29.178:9013/logout\",\"statusCode\":300}";
//		Map<String, Object> jsonMap = jsonToMap(jsonStr);
//		System.out.println(jsonMap);
//	}
}
