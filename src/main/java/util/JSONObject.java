package util;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;



@SuppressWarnings("unchecked")
public class JSONObject {
	private static final class Null {

		protected final Object clone() {
			return this;
		}
	
	    
		public boolean equals(Object object) {
		    return object == null || object == this;
		}
	
	
		public String toString() {
		    return "null";
		}
    }

    private Map map;
    public static final Object NULL = new Null();

    public JSONObject() {
        this.map = new HashMap();
    }
    
    public JSONObject(List<Object[]> res, String[] nomes, Object tipoIsNull) throws JSONException{
    	this();
    	if (tipoIsNull == null)
    		tipoIsNull = JSONObject.NULL;
    	if (res == null || res.size() == 0){
    		res = new ArrayList<Object[]>();
    		res.add(new Object[nomes.length]);
    	}
    	for (Object[] este : res)
    		for (int i=0;i<este.length;i++){
	    		Object o = este[i];
	    		if (o == null)
	    			o = tipoIsNull;
	    		this.put(nomes[i], o);
    		}
    }
    
    public JSONObject(Object[] este, String[] nomes, Object tipoIsNull, Integer[] indicesNumericos) throws JSONException{
    	this();
    	if (tipoIsNull == null)
    		tipoIsNull = JSONObject.NULL;
    	if (este == null)
    		este = new Object[nomes.length];
    	for (int i=0;i<este.length;i++){
    		Object o = este[i];
    		if (o ==null){
    			if (this.existeIndiceEmArray(i, indicesNumericos))
    				o = 0;
    			else
    				o = tipoIsNull;
    		}
    		this.put(nomes[i], o);
		}
    }
    
    public JSONObject(List<Object[]> res, String[] nomes, Object tipoIsNull, Integer[] indicesNumericos) throws JSONException{
    	this();
    	if (tipoIsNull == null)
    		tipoIsNull = JSONObject.NULL;
    	if (res== null || res.size() == 0){
    		res = new ArrayList<Object[]>();
    		res.add(new Object[nomes.length]);
    	}
    	for (Object[] este : res){
    		for (int i=0;i<este.length;i++){
	    		Object o = este[i];
	    		if (o ==null){
	    			if (this.existeIndiceEmArray(i, indicesNumericos))
	    				o = 0;
	    			else
	    				o = tipoIsNull;
	    		}
	    		this.put(nomes[i], o);
    		}
    	}
    }

    public JSONObject(JSONObject jo, String[] names) {
        this();
        for (int i = 0; i < names.length; i += 1) {
            try {
                putOnce(names[i], jo.opt(names[i]));
            } catch (Exception ignore) {
            }
        }
    }


    public JSONObject(JSONTokener x) throws JSONException {
        this();
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A JSONObject text must end with '}'");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }


            c = x.nextClean();
            if (c == '=') {
                if (x.next() != '>') {
                    x.back();
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }
            putOnce(key, x.nextValue());


            switch (x.nextClean()) {
            case ';':
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }

    public JSONObject(Map map) {
        this.map = new HashMap();
        if (map != null) {
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry)i.next();
                Object value = e.getValue();
                if (value != null) {
                    this.map.put(e.getKey(), wrap(value));
                }
            }
        }
    }

    public JSONObject(Object bean) {
        this();
        populateMap(bean);
    }
    
    public JSONObject(Object[] objetos, String[] nomes){
    	this();
    	for (int i=0;i<nomes.length;i++){
    		try {
				putOpt(nomes[i], objetos[i]);
			} catch (JSONException e) {
			}
    	}
    }
    
    public JSONObject(Object object, String names[]) {
        this();
        Class c = object.getClass();
        for (int i = 0; i < names.length; i += 1) {
            String name = names[i];
            try {
                putOpt(name, c.getField(name).get(object));
            } catch (Exception ignore) {
            }
        }
    }

    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public JSONObject(String baseName, Locale locale) throws JSONException {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
        Enumeration keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof String) {
                String[] path = ((String)key).split("\\.");
                int last = path.length - 1;
                JSONObject target = this;
                for (int i = 0; i < last; i += 1) {
                    String segment = path[i];
                    JSONObject nextTarget = target.optJSONObject(segment);
                    if (nextTarget == null) {
                        nextTarget = new JSONObject();
                        target.put(segment, nextTarget);
                    }
                    target = nextTarget;
                }
                target.put(path[last], bundle.getString((String)key));
            }
        }
    }

    public JSONObject accumulate(String key, Object value)
            throws JSONException {
        testValidity(value);
        Object object = opt(key);
        if (object == null) {
            put(key, value instanceof JSONArray ?
                    new JSONArray().put(value) : value);
        } else if (object instanceof JSONArray) {
            ((JSONArray)object).put(value);
        } else {
            put(key, new JSONArray().put(object).put(value));
        }
        return this;
    }

    public JSONObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = opt(key);
        if (object == null) {
            put(key, new JSONArray().put(value));
        } else if (object instanceof JSONArray) {
            put(key, ((JSONArray)object).put(value));
        } else {
            throw new JSONException("JSONObject[" + key +
                    "] is not a JSONArray.");
        }
        return this;
    }

    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }
        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && 
        		string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object get(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        Object object = opt(key);
        if (object == null) {
            throw new JSONException("JSONObject[" + quote(key) +
                    "] not found.");
        }
        return object;
    }

    public boolean getBoolean(String key) throws JSONException {
        Object object = get(key);
        if (object.equals(Boolean.FALSE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONObject[" + quote(key) +
                "] is not a Boolean.");
    }


    public double getDouble(String key) throws JSONException {
        Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).doubleValue() :
                Double.parseDouble((String)object);
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) +
                "] is not a number.");
        }
    }

    public int getInt(String key) throws JSONException {
        Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).intValue() :
                Integer.parseInt((String)object);
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) +
                "] is not an int.");
        }
    }

    public JSONArray getJSONArray(String key) throws JSONException {
        Object object = get(key);
        if (object instanceof JSONArray) {
            return (JSONArray)object;
        }
        throw new JSONException("JSONObject[" + quote(key) +
                "] is not a JSONArray.");
    }

    public JSONObject getJSONObject(String key) throws JSONException {
        Object object = get(key);
        if (object instanceof JSONObject) {
            return (JSONObject)object;
        }
        throw new JSONException("JSONObject[" + quote(key) +
                "] is not a JSONObject.");
    }

    public long getLong(String key) throws JSONException {
        Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).longValue() :
                Long.parseLong((String)object);
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) +
                "] is not a long.");
        }
    }

    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext()) {
            names[i] = (String)iterator.next();
            i += 1;
        }
        return names;
    }

    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; i += 1) {
            names[i] = fields[i].getName();
        }
        return names;
    }

    public String getValue(String key) throws JSONException {
        Object object = get(key);
        String dado = object == NULL ? "" : object.toString();
        dado = dado.replace("[\"", "").replace("\"]", "");
		return dado;
    }
     
    /**
     * Dado uma string separado por , retorna uma List<Long> com os valores
     * 
     * @param valor String contendo os valores. Necessarimente precisa estar no formato 1,2,3,4,5
     * @return
     * @throws JSONException
     */
    public List<Long> getListOfLong(String valor) throws JSONException {
    	List<Long> listOfLong = new ArrayList<>(); 
    	
        Object object = getValue(valor).replace("[", "").replace("]", "");
        if (object  == null ) {
        	return listOfLong;
        }else{
        	if (!StringUtils.isEmpty(object.toString())){
	        	String[] values = object.toString().replace("{", "").replace("}", "").split(",");
	        	for (String v : values) {  
					listOfLong.add(new Long(v));
				}
        	}
        }
        return listOfLong;
    }
    
    /**
     * Converte uma String em um mapa de Map<String,String>
     * 
     * @param valor necessariamente precisa estar no seguinte formato {"chave1":"valor1", "chave2":"valor2"} Onde o primeiro valor ser� considerado chave e o segundo o valor 
     * do mapa
     * @return
     * @throws JSONException
     */
    public Map<String, String> getMap(String valor) throws JSONException {
    	Map<String, String> mapa = new HashMap<>();
    	
        Object object = getValue(valor).replace("[", "").replace("]", "");
        if (object  == null ) {
        	return mapa;
        }else{
        	if (!StringUtils.isEmpty(object.toString())){
	        	String[] values = object.toString().replace("{", "").replace("}", "").split(",");
	        	for (String v : values) {
					String[] pares = v.split(":");
					String key = pares[0].replace("\"", "").replace("\"", "");
					String value = pares[1].replace("\"", "").replace("\"", "");
					mapa.put(key, value);
	        	}
        	}
        }
        return mapa;
    }
    
    public String getString(String key) throws JSONException {
        Object object = get(key);
        return object == NULL ? null : object.toString();
    }

    public boolean has(String key) {
        return this.map.containsKey(key);
    }
    
    public JSONObject increment(String key) throws JSONException {
        Object value = opt(key);
        if (value == null) {
            put(key, 1);
        } else if (value instanceof Integer) {
            put(key, ((Integer)value).intValue() + 1);
        } else if (value instanceof Long) {
            put(key, ((Long)value).longValue() + 1);                
        } else if (value instanceof Double) {
            put(key, ((Double)value).doubleValue() + 1);                
        } else if (value instanceof Float) {
            put(key, ((Float)value).floatValue() + 1);                
        } else {
            throw new JSONException("Unable to increment [" + quote(key) + "].");
        }
        return this;
    }

    public boolean isNull(String key) {
        return JSONObject.NULL.equals(opt(key));
    }

    public Iterator keys() {
        return this.map.keySet().iterator();
    }

    public int length() {
        return this.map.size();
    }

    public JSONArray names() {
        JSONArray ja = new JSONArray();
        Iterator  keys = this.keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return ja.length() == 0 ? null : ja;
    }

    public static String numberToString(Number number)
            throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(number);
        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && 
        		string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double optDouble(String key) {
        return optDouble(key, Double.NaN);
    }

    public double optDouble(String key, double defaultValue) {
        try {
            return getDouble(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int optInt(String key) {
        return optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public JSONArray optJSONArray(String key) {
        Object o = opt(key);
        return o instanceof JSONArray ? (JSONArray)o : null;
    }

    public JSONObject optJSONObject(String key) {
        Object object = opt(key);
        return object instanceof JSONObject ? (JSONObject)object : null;
    }

    public long optLong(String key) {
        return optLong(key, 0);
    }

    public long optLong(String key, long defaultValue) {
        try {
            return getLong(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String optString(String key) {
        return optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        Object object = opt(key);
        return NULL.equals(object) ? defaultValue : object.toString();        
    }

    private void populateMap(Object bean) {
        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = (includeSuperClass) ? klass.getMethods() : klass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i += 1) {
            try {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if (name.equals("getClass") || 
                                name.equals("getDeclaringClass")) {
                            key = "";
                        } else {
                            key = name.substring(3);
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }
                    if (key.length() > 0 &&
                            Character.isUpperCase(key.charAt(0)) &&
                            method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() +
                                key.substring(1);
                        }

                        Object result = method.invoke(bean, (Object[])null);
                        if (result != null) {
                            map.put(key, wrap(result));
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    public JSONObject put(String key, boolean value) throws JSONException {
        put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONObject put(String key, Collection value) throws JSONException {
        put(key, new JSONArray(value));
        return this;
    }

    public JSONObject put(String key, double value) throws JSONException {
        put(key, new Double(value));
        return this;
    }

    public JSONObject put(String key, int value) throws JSONException {
        put(key, new Integer(value));
        return this;
    }

    public JSONObject put(String key, long value) throws JSONException {
        put(key, new Long(value));
        return this;
    }

    public JSONObject put(String key, Map value) throws JSONException {
        put(key, new JSONObject(value));
        return this;
    }
    
    private boolean existeIndiceEmArray(int indice, Integer[] indicesNumericos){
    	for (Integer este : indicesNumericos){
    		if (este.intValue() == indice)
    			return true;
    	}
    	return false;
    }
    
    public void addProperties(Object[] objetos, String[] nomes, boolean isNullAsZero, Integer[] indicesNumericos) throws JSONException {
    	for (int i=0;i<nomes.length;i++){
    		Object o = objetos[i];
    		if (o ==null){
    			if (isNullAsZero && this.existeIndiceEmArray(i, indicesNumericos))
    				o = 0;
    			else
    				o = JSONObject.NULL;
    		}
    		this.put(nomes[i], o);
    	}
    }
    
    
    public JSONObject unsafePut(String key, Object value){
    	try{
    		return this.put(key, value);
    	}catch(Throwable t){
    		return null;
    	}
    }

    public JSONObject put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            remove(key);
        }
        return this;
    }

    public JSONObject putOnce(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            if (opt(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            put(key, value);
        }
        return this;
    }

    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            put(key, value);
        }
        return this;
    }

    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        String       hhhh;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    hhhh = "000" + Integer.toHexString(c);
                    sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }

    public static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return JSONObject.NULL;
        }

        /*
         * If it might be a number, try converting it. 
         * We support the non-standard 0x- convention. 
         * If a number cannot be produced, then the value will just
         * be a string. Note that the 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser may accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0' && string.length() > 2 &&
                        (string.charAt(1) == 'x' || string.charAt(1) == 'X')) {
                try {
                    return new Integer(Integer.parseInt(string.substring(2), 16));
                } catch (Exception ignore) {
                }
            }
            try {
                if (string.indexOf('.') > -1 || 
                        string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                    return Double.valueOf(string);
                } else {
                    Long myLong = new Long(string);
                    if (myLong.longValue() == myLong.intValue()) {
                        return new Integer(myLong.intValue());
                    } else {
                        return myLong;
                    }
                }
            }  catch (Exception ignore) {
            }
        }
        return string;
    }

    public static void testValidity(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException(
                        "JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float)o).isInfinite() || ((Float)o).isNaN()) {
                    throw new JSONException(
                        "JSON does not allow non-finite numbers.");
                }
            }
        }
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    public String toString() {
        try {
            Iterator     keys = this.keys();
            StringBuffer sb = new StringBuffer("{");

            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(this.map.get(o)));
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    public String toStringUTF8() {
        try {
            Iterator     keys = this.keys();
            StringBuffer sb = new StringBuffer("{");

            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(this.map.get(o)));
            }
            sb.append('}');
            return new String(sb.toString().getBytes(), Charset.forName("utf-8"));
        } catch (Exception e) {
            return null;
        }
    }

    public String toString(int indentFactor) throws JSONException {
        return toString(indentFactor, 0);
    }

    String toString(int indentFactor, int indent) throws JSONException {
        int i;
        int length = this.length();
        if (length == 0) {
            return "{}";
        }
        Iterator     keys = this.keys();
        int          newindent = indent + indentFactor;
        Object       object;
        StringBuffer sb = new StringBuffer("{");
        if (length == 1) {
            object = keys.next();
            sb.append(quote(object.toString()));
            sb.append(": ");
            sb.append(valueToString(this.map.get(object), indentFactor,
                    indent));
        } else {
            while (keys.hasNext()) {
                object = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                } else {
                    sb.append('\n');
                }
                for (i = 0; i < newindent; i += 1) {
                    sb.append(' ');
                }
                sb.append(quote(object.toString()));
                sb.append(": ");
                sb.append(valueToString(this.map.get(object), indentFactor,
                        newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (i = 0; i < indent; i += 1) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public static String valueToString(Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object object;
            try {
                object = ((JSONString)value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
            if (object instanceof String) {
                return (String)object;
            }
            throw new JSONException("Bad value from toJSONString: " + object);
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof JSONObject ||
                value instanceof JSONArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            return new JSONObject((Map)value).toString();
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection)value).toString();
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString();
        }
        return quote(value.toString());
    }

     static String valueToString(Object value, int indentFactor, int indent)
            throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                Object o = ((JSONString)value).toJSONString();
                if (o instanceof String) {
                    return (String)o;
                }
            }
        } catch (Exception ignore) {
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONObject) {
            return ((JSONObject)value).toString(indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return ((JSONArray)value).toString(indentFactor, indent);
        }
        if (value instanceof Map) {
            return new JSONObject((Map)value).toString(indentFactor, indent);
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection)value).toString(indentFactor, indent);
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }

     public static Object wrap(Object object) {
         try {
             if (object == null) {
                 return NULL;
             }
             if (object instanceof JSONObject || object instanceof JSONArray  || 
                     NULL.equals(object)      || object instanceof JSONString || 
                     object instanceof Byte   || object instanceof Character  ||
                     object instanceof Short  || object instanceof Integer    ||
                     object instanceof Long   || object instanceof Boolean    || 
                     object instanceof Float  || object instanceof Double     ||
                     object instanceof String) {
                 return object;
             }
             
             if (object instanceof Collection) {
                 return new JSONArray((Collection)object);
             }
             if (object.getClass().isArray()) {
                 return new JSONArray(object);
             }
             if (object instanceof Map) {
                 return new JSONObject((Map)object);
             }
             Package objectPackage = object.getClass().getPackage();
             String objectPackageName = ( objectPackage != null ? objectPackage.getName() : "" );
             if (objectPackageName.startsWith("java.") ||
                     objectPackageName.startsWith("javax.") ||
                     object.getClass().getClassLoader() == null) {
                 return object.toString();
             }
             return new JSONObject(object);
         } catch(Exception exception) {
             return null;
         }
     }

     public Writer write(Writer writer) throws JSONException {
        try {
            boolean  commanate = false;
            Iterator keys = this.keys();
            writer.write('{');

            while (keys.hasNext()) {
                if (commanate) {
                    writer.write(',');
                }
                Object key = keys.next();
                writer.write(quote(key.toString()));
                writer.write(':');
                Object value = this.map.get(key);
                if (value instanceof JSONObject) {
                    ((JSONObject)value).write(writer);
                } else if (value instanceof JSONArray) {
                    ((JSONArray)value).write(writer);
                } else {
                    writer.write(valueToString(value));
                }
                commanate = true;
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
     }
}