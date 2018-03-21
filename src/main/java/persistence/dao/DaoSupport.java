package persistence.dao;

import persistence.factory.AppProxyFactory;


public final class DaoSupport {

	private DaoSupport() {}

	public static <T> T getDao(Class<T> clazz, Boolean injectEm) {
		try {
			 
			DaoSupportProxy invoker = new DaoSupportProxy(injectEm);
			AppProxyFactory f = new AppProxyFactory();
			f.setSuperclass(clazz);
			f.setHandler(invoker);
		
			return  (T) f.createClass().newInstance();
		}catch (InstantiationException e) {
			throw new RuntimeException("Não foi possível criar a classe DAO.: " + clazz.getSimpleName() + ". Verifique se a mesma possui um construtor padr�o");
		} catch (Exception e) {
			throw new RuntimeException("Erro ao criar o proxy para a classe DAO.: " + clazz.getSimpleName());
		}
		
	}
	
	public static <T> T getDao(Class<T> clazz) {
		return getDao(clazz, true);
	}
}