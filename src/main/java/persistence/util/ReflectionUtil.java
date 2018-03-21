package persistence.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LazyInitializationException;
import org.omg.CORBA.portable.ApplicationException;

import rest.exceptions.AppError;

/**
 * 
 * @author  Franklin S Souza
 * @version 1.0
 * @since   06/02/2012
 */
public class ReflectionUtil  implements Serializable{

	private static final long serialVersionUID = 1L;

	public static enum MetodoAcesso {
		GET, SET
	}

	public static List<String> getParameterNames(Method m) {
		List<String> listaParametros = new ArrayList<>();
		
		Parameter[] parameters = m.getParameters();
		for (Parameter parameter : parameters) {		     
		     listaParametros.add(parameter.getName());
		}
		return listaParametros;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassFromGeneric(final Object obj, final int index) {
		Type type = null;
		try {
			// a superclass possui o tipo generico
			type = ((ParameterizedType) obj.getClass().getGenericSuperclass())
					.getActualTypeArguments()[index];
		} catch (ClassCastException e) {
			Class<T> clazz = (Class<T>) obj.getClass().getGenericSuperclass();
			try {
				return getClassFromGeneric(clazz.newInstance(), index);
			} catch (InstantiationException e1) {
				throw new RuntimeException("Erro ao instanciar objeto.", e1);
			} catch (IllegalAccessException e1) {
				throw new RuntimeException("Erro ao instanciar objeto.", e1);
			}
		}
		if (type instanceof ParameterizedType) {
			return (Class<T>) ((ParameterizedType) type).getRawType();
		}
		return (Class<T>) type;
	}

	public static <ID extends Serializable, T> Map<String, Object> getPropertysEntity(final T entity)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> map = new HashMap<String, Object>();
		PropertyDescriptor[] descriptors = BeanUtilsBean.getInstance().getPropertyUtils()
				.getPropertyDescriptors(entity);
		for (int i = 0; i < descriptors.length; i++) {
			PropertyDescriptor property = descriptors[i];
			String name = property.getName();
			if (name.equalsIgnoreCase("class")) {
				continue;
			}
			if (BeanUtilsBean.getInstance().getPropertyUtils().isReadable(entity, name)) {
				Object value = BeanUtilsBean.getInstance().getPropertyUtils()
						.getSimpleProperty(entity, name);
				map.put(name, value);
			}
		}
		return map;
	}

	public static <ID extends Serializable, T> String[] getAllFieldsName(final T entity) {
		PropertyDescriptor[] descriptors = BeanUtilsBean.getInstance().getPropertyUtils()
				.getPropertyDescriptors(entity);
		String[] map = new String[descriptors.length];
		for (int i = 0; i < descriptors.length; i++) {
			PropertyDescriptor property = descriptors[i];
			String name = property.getName();
			if (name.equalsIgnoreCase("class")) {
				continue;
			}
			if (BeanUtilsBean.getInstance().getPropertyUtils().isReadable(entity, name)) {
				map[i] = name;
			}
		}
		return map;
	}

	public static Object getValor(final Object o, final Field field) {
		try {
			return PropertyUtils.getSimpleProperty(o, field.getName());
		} catch (Exception e) {
		}
		return null;
	}

	public static List<CampoAlterado> compararBeans(final Object anterior, final Object atual) {
		List<CampoAlterado> alterados = new ArrayList<CampoAlterado>();
		if ((anterior != null) && (atual != null) && anterior.getClass().equals(atual.getClass())) {
			// verifica nos metodos
			Field[] fields = ReflectionUtil.obtemTodosAtributosDaHierarquia(anterior.getClass());
			for (Field f: fields) {
				Object valorAnt = getValor(anterior, f);
				Object valorAtual = getValor(atual, f);
				try {
					// se o valor anterior for null ou diferente
					if (((valorAnt != null) && (valorAtual != null) && !valorAnt.equals(valorAtual))) {
						alterados.add(new ReflectionUtil().new CampoAlterado(f.getName(), valorAnt,
								valorAtual));
					} else if (((valorAnt == null) && (valorAtual != null))) {
						alterados.add(new ReflectionUtil().new CampoAlterado(f.getName(), valorAnt,
								valorAtual));
					}
				} catch (LazyInitializationException e) {
					continue;
				}
			}
		}

		return alterados;
	}

	public static void getCallers() {
		Throwable t = new Throwable();
		StackTraceElement[] pilha = t.getStackTrace();
		for (int i = 0; i < pilha.length; i++) {
			StackTraceElement caller = pilha[i];
			System.out.println("Classe:" + caller.getClassName());
			System.out.println("metodo:" + caller.getMethodName());
		}
	}

	/**
	 * @return StackTraceElement[] - A pilha (StackTraceElement) desde a execuera��o deste metodo.
	 */
	public static StackTraceElement[] getStackTraceElement() {
		return new Throwable().getStackTrace();
	}

	/**
	 * @param <E>
	 * @return E - retorna a pilha de classes executadas (StackTraceElement) desde a execuera��o deste
	 *         metodo.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Object> Set<Class<E>> getStackTraceClass() {
		StackTraceElement[] pilha = getStackTraceElement();
		Set<Class<E>> classes = new HashSet<Class<E>>();
		for (StackTraceElement e: pilha) {
			try {
				classes.add((Class<E>) Class.forName(e.getClassName()));
			} catch (ClassNotFoundException e1) {
			}
		}

		return classes;
	}

	/**
	 */
	public static Map<String, Integer> getStackTraceMethod() {
		StackTraceElement[] pilha = getStackTraceElement();
		Map<String, Integer> methods = new HashMap<String, Integer>();
		for (int i = 0; i < pilha.length; i++) {
			methods.put(pilha[i].getMethodName(), i);
		}
		return methods;
	}

	public static <T extends Annotation> Method findLastCallerMethod(final Class<T> annotationClass) {
		Set<Class<Object>> stackTraceClass = getStackTraceClass();
		Map<String, Integer> stackTraceMethod = getStackTraceMethod();
		Map<Integer, Method> m = new HashMap<Integer, Method>();
		Integer ultimoExecutado = 0;
		// percorre todas as classes da pilha
		for (Class<Object> clazz: stackTraceClass) {
			// recuperar todos atributos de cada classe da pilha
			Method[] methods = obtemTodosMetodosDaHierarquia(clazz);
			for (Method method: methods) {
				// verificar se esta anotado e se o metodo passou pela pilha de execuera��o
				if (method.isAnnotationPresent(annotationClass)
						&& stackTraceMethod.containsKey(method.getName())) {
					Integer ordemExecucao = stackTraceMethod.get(method.getName());
					// pega o metodo e a ordem de execucao
					m.put(stackTraceMethod.get(method.getName()), method);
					if (ultimoExecutado < ordemExecucao) {
						ultimoExecutado = ordemExecucao;
					}
				}
			}
		}
		return m.get(ultimoExecutado);
	}

	public static <T extends Annotation> Class<Object> findLastCallerClass(
			final Class<T> annotationClass) {
		Set<Class<Object>> stackTraceClass = getStackTraceClass();
		Class<Object> c = null;
		// percorre todas as classes da pilha
		for (Class<Object> clazz: stackTraceClass) {
			if (clazz.isAnnotationPresent(annotationClass)) {
				c = clazz;
			}
		}
		return c;
	}

	/**
	 * informada.
	 * 
	 * @param classeBase
	 * @param nomeDoAtributo
	 *            O nome do atributo a ser encontrado.
	 * @throws SecurityException
	 *             No caso de haver um <code>SecurityManager</code> instalado, e ele negar acesso ao
	 *             metodo <code>java.lang.Class.getDeclaredField(java.lang.String)</code>.
	 * @throws NoSuchFieldException
	 *             Caso o atributo solicitado Não seja encontrado na hierarquia da classe base.
	 * @see Class#getDeclaredField(String)
	 */
	public static Field getField(final Class<?> classeBase, final String nomeDoAtributo) {

		for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass()) {

			try {

				Field f = classe.getDeclaredField(nomeDoAtributo);

				if (f != null) {
					return f;
				}

			} catch (NoSuchFieldException fieldException) {

			}
		}

		return null;
	}

	/**
	 * Não-ofuscados contidos na hierarquia da classe base informada.
	 * 
	 * @param classeBase
	 * @see #obtemTodosAtributosDaHierarquia(Class, boolean)
	 * @see Class#getDeclaredFields()
	 */
	public static Field[] obtemTodosAtributosDaHierarquia(final Class<?> classeBase) {
		final Map<String, Field> mapaDeAtributos = new HashMap<String, Field>();
		for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass()) {
			for (Field atributo: classe.getDeclaredFields()) {
				String nomeDoAtributo = atributo.getName();
				mapaDeAtributos.put(nomeDoAtributo, atributo);
			}
		}
		final Collection<Field> atributos = mapaDeAtributos.values();
		return atributos.toArray(new Field[atributos.size()]);
	}

	public static Field[] obtemTodosAtributosDaHierarquiaSemAnotacao(final Class<?> classeBase,
			final Class<? extends Annotation> anotacao) {
		final Map<String, Field> mapaDeAtributos = new HashMap<String, Field>();
		for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass()) {
			for (Field atributo: classe.getDeclaredFields()) {
				if (!atributo.isAnnotationPresent(anotacao)) {
					String nomeDoAtributo = atributo.getName();
					mapaDeAtributos.put(nomeDoAtributo, atributo);
				}
			}
		}
		final Collection<Field> atributos = mapaDeAtributos.values();
		return atributos.toArray(new Field[atributos.size()]);
	}

	/**
	 * 
	 * @param classeBase
	 * @param nomeDoMetodo
	 *            O nome do metodo a ser encontrado.
	 * @param listaDeArgumentos
	 *            A lista dos tipos dos argumentos.
	 * @throws SecurityException
	 *             No caso de haver um <code>SecurityManager</code> instalado, e ele negar acesso ao
	 *             metodo <code>java.lang.Class.getDeclaredField(java.lang.String)</code>.
	 * @throws NoSuchMethodException
	 *             Caso o metodo solicitado Não seja encontrado na hierarquia da classe base.
	 * @see Class#getDeclaredMethod(String, Class...)
	 */
	public static Method procuraMetodoNaHierarquia(final Class<?> classeBase,
			final String nomeDoMetodo, final Class<?>... listaDeArgumentos)
			throws SecurityException, NoSuchMethodException {
		for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass())
			try {
				return classe.getDeclaredMethod(nomeDoMetodo, listaDeArgumentos);
			} catch (NoSuchMethodException methodException) {
			}
		throw new NoSuchMethodException(nomeDoMetodo);
	}

	/**
	 * sobrescritos e Não-ofuscados contidos na hierarquia da classe base informada.
	 * 
	 * @param classeBase
	 * @see #obtemTodosMetodosDaHierarquia(Class, boolean)
	 * @see Class#getDeclaredMethods()
	 */
	public static Method[] obtemTodosMetodosDaHierarquia(final Class<?> classeBase) {
		final Map<String, Method> mapaDeMetodos = new HashMap<String, Method>();
		for (Method metodo: classeBase.getDeclaredMethods()) {
			String nomeDoMetodo = metodo.getName();
			mapaDeMetodos.put(nomeDoMetodo, metodo);
		}
		final Collection<Method> atributos = mapaDeMetodos.values();
		return atributos.toArray(new Method[atributos.size()]);
	}

	public static Method[] obtemTodosMetodosComNome(final Class<?> classeBase,
			final String methodName) {
		final Map<String, Method> mapaDeMetodos = new HashMap<String, Method>();
		for (Method metodo: classeBase.getDeclaredMethods()) {
			String nomeDoMetodo = metodo.getName();
			if (nomeDoMetodo.equals(methodName) && !mapaDeMetodos.containsKey(nomeDoMetodo))
				mapaDeMetodos.put(nomeDoMetodo, metodo);
		}
		final Collection<Method> atributos = mapaDeMetodos.values();
		return atributos.toArray(new Method[atributos.size()]);
	}

	public static Method obtemMetodoComNome(final Class<?> classeBase, final String methodName) {
		for (Method metodo: classeBase.getDeclaredMethods()) {
			String nomeDoMetodo = metodo.getName();
			if (nomeDoMetodo.equals(methodName))
				return metodo;
		}
		return null;
	}

	public static Method[] obtemTodosMetodosDaHierarquiaComNome(final Class<?> classeBase,
			final String methodName) {
		final Map<String, Method> mapaDeMetodos = new HashMap<String, Method>();
		for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass())
			for (Method metodo: classe.getDeclaredMethods()) {
				String nomeDoMetodo = metodo.getName();
				if (nomeDoMetodo.equals(methodName) && !mapaDeMetodos.containsKey(nomeDoMetodo))
					mapaDeMetodos.put(nomeDoMetodo, metodo);
			}
		final Collection<Method> atributos = mapaDeMetodos.values();
		return atributos.toArray(new Method[atributos.size()]);
	}

	public static Class<?> getClassFromFieldValue(final Object fieldValue, final Class<?> classe) {
		return fieldValue.getClass();
	}

	/**
	 * 
	 * 
	 * @param classeBase
	 * @param nomeDoAtributo
	 * @return
	 */
	public static boolean temAtributoNaHierarquia(final Class<?> classeBase,
			final String nomeDoAtributo) {

		if (StringUtils.isBlank(nomeDoAtributo)) {
			return false;
		}

		if (!(nomeDoAtributo.indexOf(".") > -1)) {
			for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass())
				try {
					Field f = classe.getDeclaredField(nomeDoAtributo);
					return f != null;
				} catch (NoSuchFieldException fieldException) {
				}

		} else {
			String primeiroAtr = nomeDoAtributo.substring(0, nomeDoAtributo.indexOf("."));
			Field declaredField = null;
			for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass()) {
				try {
					declaredField = classe.getDeclaredField(primeiroAtr);
					if (declaredField != null)
						break;
				} catch (NoSuchFieldException fieldException) {
				}
			}

			Field f2 = null;
			try {
				f2 = getField(declaredField.getType(),
						nomeDoAtributo.substring(nomeDoAtributo.indexOf(".") + 1));
			} catch (SecurityException e) {
				e.printStackTrace();
			}

			return f2 != null;
		}
		return false;
	}

	public static <T> T invocarSetterNumerico(final T obj, final Method method,
			final Object valorProp) {

		Class<?>[] classesType = method.getParameterTypes();

		for (Class<?> type: classesType) {

			try {

				if (Short.class.isAssignableFrom(type)) {
					return extrairValorNumerico(obj, method, valorProp);
				}

				if (Long.class.isAssignableFrom(type)) {
					return extrairValorNumerico(obj, method, valorProp);
				}

				if (BigDecimal.class.isAssignableFrom(type)) {
					return extrairValorNumerico(obj, method, valorProp);
				}

				if (Float.class.isAssignableFrom(type)) {
					return extrairValorNumerico(obj, method, valorProp);
				}

				if (Double.class.isAssignableFrom(type)) {
					return extrairValorNumerico(obj, method, valorProp);
				}

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T extrairValorNumerico(final T obj, final Method method,
			final Object valorProp) throws IllegalAccessException, InvocationTargetException {
		return (T) method.invoke(obj, new Object[] {new Short(valorProp.toString())});
	}

	/**
	 * 
	 * 
	 * @param classeBase
	 * @param fieldName
	 * @return
	 */
	public static Field obtemAtributoDaHierarquiaComNome(final Class<?> classeBase,
			final String fieldName) {

		if (StringUtils.isBlank(fieldName)) {
			return null;
		}

		for (Class<?> classe = classeBase; classe != null; classe = classe.getSuperclass()) {
			for (Field field: classe.getDeclaredFields()) {
				String nomeDoCampo = field.getName();
				if (nomeDoCampo.equals(fieldName)) {
					return field;
				}
			}
		}
		return null;
	}

	// utilizado em comparar beans
	public class CampoAlterado {

		private String nome;

		private Object valorAnterior;

		private Object valorAtual;

		public CampoAlterado(final String nome, final Object valorAnterior, final Object valorAtual) {
			this.nome = nome;
			this.valorAnterior = valorAnterior;
			this.valorAtual = valorAtual;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(final String nome) {
			this.nome = nome;
		}

		public Object getValorAnterior() {
			return valorAnterior;
		}

		public void setValorAnterior(final Object valorAnterior) {
			this.valorAnterior = valorAnterior;
		}

		public Object getValorAtual() {
			return valorAtual;
		}

		public void setValorAtual(final Object valorAtual) {
			this.valorAtual = valorAtual;
		}

		@Override
		public String toString() {
			if ((valorAnterior != null) || (valorAtual != null)) {
				if (valorAnterior == null) {
					return nome + ": '" + valorAtual + "'";
				}
				return nome + ": de '" + valorAnterior + "' para '" + valorAtual + "'";
			} else
				return null;
		}
	}

	/**
	 * Recebe o nome de uma propriedade e chama dinamicamente o metodo getter correpondente,
	 * colocando o resultado em Object. Se a propriedade for uma classe agregada, varre todos os
	 * niveis de getters.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T chamaGetter(final Object obj, final String nomeAtributo) {
		try {
			List<String> l = Arrays.asList(StringUtils.split(nomeAtributo, ".")); // StringUtil.separaListaTermos(nomeAtributo,
																					// ".");
			Object objAux = obj;
			Iterator<String> i = l.iterator();
			while (i.hasNext()) {
				String nomeAtributoAux = i.next();
				Class<?> type = PropertyUtils.getPropertyType(objAux, nomeAtributoAux);
				if ((type != null) && Map.class.isAssignableFrom(type)) {
					objAux = PropertyUtils.getMappedProperty(objAux, nomeAtributoAux);
				} else {
					objAux = PropertyUtils.getSimpleProperty(objAux, nomeAtributoAux);
				}
				if (objAux == null) {
					return (T) objAux;
				}
			}
			return (T) objAux;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(final Object obj, final String nomeAtributo) {
		List<String> l = Arrays.asList(StringUtils.split(nomeAtributo, ".")); // StringUtil.separaListaTermos(nomeAtributo,
																				// ".");aluno.sexo.id
		Object objAux = obj;
		Iterator<String> i = l.iterator();
		try {
			while (i.hasNext()) {
				String nomeAtributoAux = i.next();
				Class<?> type = PropertyUtils.getPropertyType(objAux, nomeAtributoAux);
				if (type == null) { // Não existe a proprieade
					Method method = objAux.getClass().getMethod(nomeAtributoAux);
					if (method != null) {
						objAux = method.invoke(objAux);

					}
				} else {
					if (Map.class.isAssignableFrom(type)) {
						objAux = PropertyUtils.getMappedProperty(objAux, nomeAtributoAux);
					} else {
						objAux = PropertyUtils.getSimpleProperty(objAux, nomeAtributoAux);
					}
				}
				if (objAux == null) {
					return (T) objAux;
				}
			}
			return (T) objAux;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retorna todos os fields da super classe de um objeto
	 * 
	 * @param o 		Objeto a ser inspecionado
	 * @return 			Retorna todos os fields da super classe de um objeto
	 */
	public static List<Field> getSuperClassFields(Object o) {
		List<Field> listaMetodos = new ArrayList<Field>();
		
		for (Field m : o.getClass().getDeclaredFields()) {		
			listaMetodos.add(m);
		}
		return listaMetodos;
	}
	
	/**
	 * Retorna todos os methods da super classe de um objeto
	 * 
	 * @param o 		Objeto a ser inspecionado
	 * @return 			Retorna todos os methods da super classe de um objeto
	 */
	public static List<Method> getSuperClassMethods(Object o) {
		List<Method> listaMetodos = new ArrayList<Method>();
		
		for (Method m : o.getClass().getDeclaredMethods()) {		
			listaMetodos.add(m);
		}
		return listaMetodos;
	}
	
	/**
	 * Retorna todos os fields de um objeto
	 * 
	 * @param o 		Objeto a ser inspecionado
	 * @return 			 Retorna todos os fields de um objeto
	 */
	public static List<Field> getFields(Object o) {
		List<Field> listaMetodos = new ArrayList<Field>();
		
		for (Field m : o.getClass().getDeclaredFields()) {		
			listaMetodos.add(m);
		}
		return listaMetodos;
	}
	
	/**
	 * Retorna todos os methods de um objeto
	 * 
	 * @param o 		Objeto a ser inspecionado
	 * @return 			Retorna todos os methos de um objeto
	 */
	public static List<Method> getMethods(Object o) {
		List<Method> listaMetodos = new ArrayList<Method>();
		
		for (Method m : o.getClass().getDeclaredMethods()) {		
			listaMetodos.add(m);
		}
		return listaMetodos;
	}
	
	/**
	 * Retorna um metodo dado seu objeto e seu metodo
	 * 
	 * @param o 		Objeto a ser inspecionado
	 * @param metodo 	Nome do metodo
	 * @return 			Retorna um metodo dado seu objeto e seu metodo
	 */
	public static Method getMethodByName(Object o, String metodo) {
		for (Method m : o.getClass().getDeclaredMethods()) {		
			if (m.getName().equals(metodo)) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * metodo que invoca um metodo dado seu objeto, nome e argumentos.
	 * 
	 * 
	 * @param metodo			Nome do metodo a ser invocado.
	 * @param params			par�metros do metodo.
	 * @return {@link Object}	Retorna o objeto de retorno do metodo.
	 */
	public static Object invokeClassMethod(Object o, String metodo, Object... params)throws ApplicationException, ApplicationException{
		Boolean invocationComplete = false;
		Object resultado = null;
		Class clazz = getRealClass(o);
		
		try {
			for (Method m : clazz.getDeclaredMethods()) {		
				if (m.getName().equalsIgnoreCase(metodo)) {
					
					if (m.getReturnType().equals(void.class)) {
						throw new AppError("metodo.: " + metodo + " da classe.: " + clazz.getSimpleName() +" Não pode ser void.");			
					}
					
					resultado = m.invoke(o, params);
					invocationComplete = true;
					break;
				}
			}
		}catch (InvocationTargetException e) {
			if (e.getCause() instanceof ApplicationException){
				invocationComplete= true;
				throw new AppError(e.getCause().getMessage(),e);
			} else if (e.getCause() instanceof ApplicationException){
				invocationComplete = true;
				throw (ApplicationException)e.getCause();
			} 
 
			throw new AppError(e.getMessage(),e);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppError("metodo.: " + metodo + " Não encontrado na classe.: " + clazz.getSimpleName());
		}finally{
			if (invocationComplete == false){
				throw new AppError("metodo.: " + metodo + " Não encontrado na classe.: " + clazz.getSimpleName());
			}
		}
		return resultado;
	}
	
	/**
	 * metodo que invoca um metodo dado seu objeto, nome e argumentos.
	 * 
	 * 
	 * @param metodo			Nome do metodo a ser invocado.
	 * @param params			par�metros do metodo.
	 */
	public static void invokeVoidClassMethod(Object o, String metodo, Object... params)throws ApplicationException{
		Boolean invocationComplete = false;
		try {
			Class clazz = getRealClass(o);
			for (Method m : clazz.getDeclaredMethods()) {		
				if (m.getName().equalsIgnoreCase(metodo)) {
					m.invoke(o, params);
					invocationComplete = true;
				}
			}
		}catch (InvocationTargetException e) {
			if (e.getCause() instanceof ApplicationException){
				invocationComplete= true;
				throw new AppError(e.getCause().getMessage(),e);
			} 
			throw new AppError(e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppError("metodo.: " + metodo + " Não encontrado na classe.: " + o.getClass().getSimpleName());
		}finally{
			if (invocationComplete == false){
				throw new AppError("metodo.: " + metodo + " Não encontrado na classe.: " + o.getClass().getSimpleName());
			}
		}
	}
	
	public static Object invokeSuperClassMethod(Object o, String metodo){
		
		try {
			Class<?> c = getRealClass(o);
			for (Method m : c.getDeclaredMethods()) {		
				if (m.getName().equalsIgnoreCase(metodo)) {
					return m.invoke(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * metodo que invoca um metodo da super classe dado seu objeto filho e nome do metodo.
	 * O metodo Não pode conter argumentos.
s	 * @param metodo	nome do metodo a ser invocado.
	 */
	public static void invokeVoidSuperClassMethod(Object o, String metodo){
		
		try {
			Class<?> c = getRealClass(o);
			for (Method m : c.getDeclaredMethods()) {		
				if (m.getName().equalsIgnoreCase(metodo)) {
					m.invoke(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Verifica se a entidade esta em modo merge ou persist.
	 * Para isso, o metodo inspeciona o valor da propriedade id do objeto.
	 * 
	 */
	public static Boolean isSelected(Object o){
		Boolean selected = (Boolean) getSuperClassFieldValue(o, "selecionado");
		if (selected != null && selected == true) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se a entidade esta em modo merge ou persist.
	 * Para isso, o metodo inspeciona o valor da propriedade id do objeto.
	 * 
	 */
	public static Boolean isToSave(Object o){
		Object id = getFieldValue(o, "id");
		if (id == null || id.toString().equals("0")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retorna o metodo set do atributo na super classe tendo como par�metro o nome do atributo.
	 * 
	 * @param o				Objeto contendo o metodo
	 * @param nomeMetodo	Nome do metodo		
	 * @return				metodo encontrado ou null
	 */
	public static Method getSuperClassSetterMethod(Object o, String nomeMetodo) {
		Class clazz = getRealClass(o);
		
		for (Method m : clazz.getDeclaredMethods()) {			
			
			if (m.getName().equalsIgnoreCase(nomeMetodo)) {
				return m;
			}
		}
		
		return null;
	}
	
	/**
	 * Invoca um metodo dado seu objeto, seu nome de atriuto e seus argumentos
	 * 
	 * @param attributeName		metodo a ser invocado
	 * @param params			par�metros a serem passados para o metodo
	 */
	public static void invokeSuperClassSetterMethodByFieldName(Object o, String attributeName, Object... params) throws Exception{
		Method m = null;
		Class clazz = getRealClass(o);
		String nomeClasse = clazz.getSimpleName() + ".java"; 
		try {
			String setter = "set" + attributeName;
			
			m = getSuperClassSetterMethod(o, setter);
			
			if (m == null) {
				throw new AppError("metodos get/set do atributo.: " + attributeName.toLowerCase() + " Não encontrados na classe.: " + nomeClasse+ " \n" +
										   "Crie os metodos get/set para esse atributo na classe.: \n" +
										   nomeClasse);
			}
			
			m.invoke(o, params);
			
		} catch (IllegalArgumentException e) {
			throw new AppError("Argumentos inv�lidos foram passados para o metodo " +
					   m.getName() + " da classe " + nomeClasse);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Retorna um field da super classe dado seu objeto e seu nome
	 * 
	 * @param o				Nome do objeto contendo o field
	 * @param nomeField		Nome do field
	 * @return				Objeto {@link Field} correspondente ou null caso Não o encontre.
	 */
	public static Field getSuperClassField(Object o, String nomeField){
		try {
			Class clazz = getRealClass(o);
			for (Field f: clazz.getDeclaredFields()) {
				if (f.getName().equalsIgnoreCase(nomeField)){
					return f;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retorna o valor de um field
	 * 
	 * @param o				Nome do objeto contendo o field
	 * @param nomeField		Nome do field
	 * @return				Valor do field
	 */
	public static Object getSuperClassFieldValue(Object o, String nomeField){
		try {
			Class c = getRealClass(o);
			for (Field f: c.getDeclaredFields()) {
				if (f.getName().equalsIgnoreCase(nomeField)){
					f.setAccessible(true);
					return f.get(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retorna o tipo de atributo 
	 * @param o
	 * @param nomeField
	 */
	public static Class<?> getAttributeType(Object o, String nomeField){
		Field f = getField(o, nomeField);
		return f.getType();
	}
	
	
	/**
	 * Retorna o valor de um field
	 * 
	 * @param o				Nome do objeto contendo o field
	 * @param nomeField		Nome do field
	 * @return				Valor do field
	 */
	public static Object getFieldValue(Object o, String nomeField){
		try {
			for (Field f: o.getClass().getDeclaredFields()) {
				if (f.getName().equalsIgnoreCase(nomeField)){
					f.setAccessible(true);
					return f.get(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retorna um field dado seu objeto e seu nome
	 * 
	 * @param o				Nome do objeto contendo o field
	 * @param nomeField		Nome do field
	 * @return				Objeto {@link Field} correspondente ou null caso Não o encontre.
	 */
	public static Field getField(Object o, String nomeField){
		try {
			for (Field f: o.getClass().getDeclaredFields()) {
				if (f.getName().equalsIgnoreCase(nomeField)){
					return f;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Seta um valor a um determinado field da super classe tendo como par�metro seu objeto, 
	 * nome de field e valor
	 * 
	 * @param o				Objeto contendo o field
	 * @param nomeField		Nome do field
	 * @param value			Valor a ser setado no field.
	 */
	public static void setSuperClassFieldValue(Object o, String nomeField, Object value){
		Field f = getSuperClassField(o, nomeField);
		
		try {
			if (f != null) {
				f.setAccessible(true);
				f.set(o, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Seta um valor a um determinado field tendo como par�metro seu objeto, nome de field e valor
	 * 
	 * @param o				Objeto contendo o field
	 * @param nomeField		Nome do field
	 * @param value			Valor a ser setado no field.
	 */
	public static void setFieldValue(Object o, String nomeField, Object value){
		Field f = getField(o, nomeField);
		
		try {
			if (f != null) {
				f.setAccessible(true);
				f.set(o, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Recupera a Super Classe do objeto passado como parametro
	 * 
	 * @param o     Class a ser inspecionado
	 * 
	 * @return 		Super Classe do objeto passado como parametro ou null caso Não encontre
	 */
	public static Class<?> getSuperClass(Class<?> o){
		if (o.getClass() != null) {
			return o.getClass().getSuperclass();
		} else {
			return null;
		}
	}
	
	/**
	 * Retorn um objeto Method dado seu objeto, seu nome e seus par�metros.
	 * 
	 * @param methodName		Nome do metodo a ser procurado
	 * @param parameterTypes	Lista de par�metros do metodo
	 * 
	 */
	public static Method getMethod(Class<?> o, String methodName, Class<?>[] parameterTypes){
		
		try {
			if (methodName.equalsIgnoreCase("finalize")) {
				return null;
			}
			return o.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retorn um objeto Method dado seu objeto, seu nome e seus par�metros.
	 * 
	 * @param methodName		Nome do metodo a ser procurado
	 * @param parameterTypes	Lista de par�metros do metodo
	 * 
	 */
	public static Method getMethod(Object o, String methodName, Class<?>[] parameterTypes){
		
		try {
			return o.getClass().getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Recupera a classe real do objeto
	 * 
	 * @param o   Objeto a ser inspecionado
	 * 
	 * @return 		Super Classe do objeto passado como parametro ou null caso Não encontre
	 */
	public static Class<?> getRealClass(Object o){
		if (!o.getClass().getSuperclass().equals(Object.class)) {
			return o.getClass().getSuperclass();
		} else {
			return o.getClass();
		}
	}

	public static String getEntityTableName(Class clazz) {
		Table t = (Table) clazz.getAnnotation(javax.persistence.Table.class);
		return t.name();
	}
}
