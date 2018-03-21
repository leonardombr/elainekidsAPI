package persistence.factory;

import java.io.Serializable;

import javax.persistence.EntityManager;

import persistence.dao.DaoSupport;
import persistence.util.PersistenceUtil;

public class ServiceFactory implements Serializable{
 
	private static final long serialVersionUID = 1L;

	public static <T> T getService(Class<T> clazz, Boolean injectEm) {
		return (T)DaoSupport.getDao(clazz, injectEm);
	}
	
	public static <T> T get(Class<T> clazz) {
		return (T)DaoSupport.getDao(clazz);
	}
	 
//	public static Connection getConnection(){
//		try {
//			
//			Session session = (Session) createEntityManager().getDelegate();
//			SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
//			ConnectionProvider cp = sfi.getConnectionProvider();
//			return cp.getConnection();
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public static  EntityManager  createEntityManager() {
		return PersistenceUtil.createEntityManager();
	}
}