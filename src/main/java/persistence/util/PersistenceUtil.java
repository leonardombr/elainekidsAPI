package persistence.util;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceUtil implements Serializable{

	private static final long serialVersionUID = 1L;
	private static EntityManagerFactory defautlEmf = null;
	private static EntityManagerFactory userEmf = null;
	
	public static void closeFactories() {
		if (defautlEmf != null && defautlEmf.isOpen()) {
			defautlEmf.close();
		}
		if (userEmf != null && userEmf.isOpen()) {
			userEmf.close();
		}
	}
	
	public static EntityManager createEntityManager() {
		if (defautlEmf == null || !defautlEmf.isOpen()) {
			defautlEmf = Persistence.createEntityManagerFactory("ELAINE_KIDS");
		}
		return defautlEmf.createEntityManager();
	}
}
