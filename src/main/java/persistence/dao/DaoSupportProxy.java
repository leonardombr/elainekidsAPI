package persistence.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.annotations.common.reflection.ReflectionUtil;

import com.ctc.wstx.util.StringUtil;

import infra.FindMany;
import infra.FindOne;
import infra.UseTransaction;
import javassist.util.proxy.MethodHandler;

public final class DaoSupportProxy implements MethodHandler, Serializable {

	private static final long serialVersionUID = 1L;
	private Boolean injectEm;
	
	public DaoSupportProxy(Boolean injectEm){
		this.injectEm = injectEm; 
	}
	
	public DaoSupportProxy() {}
	
	public Object invoke(Object self, Method m, Method proceed, Object[] params) throws Exception { 
	
		Object resultado = null;
		EntityManager em = null;
		String erro = null;
		EntityTransaction tx = null;
		try {
			
			Field f = self.getClass().getSuperclass().getSuperclass().getDeclaredField("em");
			f.setAccessible(true);
			Object value = f.get(self);
			
			if (injectEm && value == null ) {  
				
				em = persistence.util.PersistenceUtil.createEntityManager();
				if (m.getAnnotation(UseTransaction.class) != null) {
				    tx = em.getTransaction();
					tx.begin();
				}
				
				f.set(self, em);
				
				FindMany findMany = m.getAnnotation(FindMany.class);
				if (findMany != null) {
					erro = findMany.erro();
					boolean isNative = findMany.isNative();
					processaFindMany(self, m, params, findMany, isNative);
				}
				
				FindOne findOne = m.getAnnotation(FindOne.class);
				if (findOne != null) {
					erro = findOne.erro();
					boolean isNative = findOne.isNative();
					processaFindOne(self, m, params, findOne, isNative);
				}
				
				resultado = proceed.invoke(self, params);
				
				if (m.getAnnotation(UseTransaction.class) != null) {
					if (tx != null){
						tx.commit();
					}
				}
				
				em.close();
				em = null;
				
				f.set(self, em);
			}else{
				resultado = proceed.invoke(self, params);
			}
			
		} catch (Exception e) {
			
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			
			if (em != null && em.isOpen()) {
				em.close();
			}
			
			if (e.getCause() instanceof Exception){
				Exception c =  null;
				if (erro == null) {
					c = (Exception)e.getCause();
				}else{
					c = new Exception(erro,(Exception)e.getCause());
				}
				throw c;
			}
		}
		return resultado; 
	}

	private void processaFindOne(Object self, Method m, Object[] params, FindOne findOne, Boolean isNative) throws Exception {
		String target = findOne.target();
		
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		List<String> parameterNames = persistence.util.ReflectionUtil.getParameterNames(m);
		
		for (int i = 0; i < params.length; i++) {
			mapa.put(parameterNames.get(i), params[i]);
		}
		
		Method methodfindOne = self.getClass().getSuperclass().getSuperclass().getMethod("findOne", String.class, Map.class, Boolean.class);
		
		Object result = null; 
		
		try {
			result =  methodfindOne.invoke(self, findOne.query(), mapa, isNative);
			
		} catch (Exception e) {
			throw e;
		}
		
		String atributo = m.getName().replace("buscar", "lista");
		
		if (persistence.util.StringUtil.isNotEmpty(target)) {
			atributo = target;
		}
		
		Field fieldRetorno = self.getClass().getSuperclass().getDeclaredField(atributo);
		
		fieldRetorno.setAccessible(true);
		fieldRetorno.set(self, result);
	}
	
	private void processaFindMany(Object self, Method m, Object[] params, FindMany findMany, Boolean isNative) throws Exception {
		String target = findMany.target();
		
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		List<String> parameterNames = persistence.util.ReflectionUtil.getParameterNames(m);
		
		for (int i = 0; i < params.length; i++) {
			mapa.put(parameterNames.get(i), params[i]);
		}
		
		Method methodfindMany = self.getClass().getSuperclass().getSuperclass().getMethod("findMany", String.class, Map.class, Boolean.class);
		
		List result = null; 
		
		try {
			result = (List) methodfindMany.invoke(self, findMany.query(), mapa, isNative);
			
		} catch (Exception e) {
			throw e;
		}
		
		String atributo = m.getName().replace("buscar", "lista");
		
		if (persistence.util.StringUtil.isNotEmpty(target)) {
			atributo = target;
		}
		
		Field fieldRetorno = null;
		
		try {
			fieldRetorno = self.getClass().getSuperclass().getDeclaredField(atributo);
		} catch (Exception e) {
			System.out.println("ATRIBUTO TARGET NAO DEFINIDO NA CONSULTA.: METODO.: " + m.getName() + "TARGET.: " + atributo + " CLASSE.: " + self.getClass().getSuperclass().getSimpleName());
			e.printStackTrace();
		}
		
		fieldRetorno.setAccessible(true);
		fieldRetorno.set(self, result);
	}
}