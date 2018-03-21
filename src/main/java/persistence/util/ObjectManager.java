package persistence.util;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import persistence.factory.ServiceFactory;
import rest.exceptions.AppError;

public class ObjectManager {

	public static Long getFieldAsLong(Class clazz, String field, Serializable id) throws AppError{
		Object v = getField(clazz, field, id);
		if (v != null){
			return Long.parseLong(v.toString());
		}
		return null;
	}
	
	public static Object getField(Class clazz, String field, Serializable id) throws AppError{
		EntityManager em = ServiceFactory.createEntityManager();
		try {
			String tableName = persistence.util.ReflectionUtil.getEntityTableName(clazz);
			Query q = em.createNativeQuery("select " + field + " from " + tableName + " where id = " + id);
			try {
				return q.getSingleResult();
				
			} catch (NoResultException e) {
				return null;
			}
			
		} catch (Exception e) {
			throw new AppError("Não foi possível buscar o field. Tente novamente mais tarde.", e );
		}finally{
			em.close();
		}
	}
	
	public static void execute(String sql) throws AppError{
		EntityManager em = ServiceFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			Query q = em.createNativeQuery(sql);
			q.executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			throw new AppError("Não foi possível executar o comando. Tente novamente mais tarde.", e );
		}finally{
			em.close();
		}
	}
	
	public static void remover(Object o) throws AppError{
		EntityManager em = ServiceFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			Field f = o.getClass().getSuperclass().getDeclaredField("id");
			f.setAccessible(true);
			Object value = f.get(o);
			if (value != null) {
				o = em.find(o.getClass(), value);
				em.remove(o);
			}
			em.flush();
			em.getTransaction().commit();
		} catch (Exception e) {
			throw new AppError("Não foi possível remover o item. Tente novamente mais tarde.", e );
		}finally{
			em.close();
		}
	}
	
	public static <T> T save(T o) throws AppError{
		EntityManager em = ServiceFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			Field f = o.getClass().getSuperclass().getDeclaredField("id");
			f.setAccessible(true);
			Object value = f.get(o);
			if (value == null) {
				em.persist(o);
			} else {
				em.merge(o);
			}
			 
			em.getTransaction().commit();
		} catch (Exception e) {
			throw new AppError("Não foi possível salvar ou atualizar o item. Tente novamente mais tarde.", e);
		}finally{
			em.close();
		}
		return o;
	}
	
	public static <T> T find(Class<T> c, Serializable id){
		EntityManager em = ServiceFactory.createEntityManager();
		T t = null;
		try {
			
			t = em.find(c, id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			em.close();
		}
		return t;
	}
}
