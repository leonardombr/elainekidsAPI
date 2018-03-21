package persistence.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import infra.UseTransaction;
import model.AppEntity;
import rest.exceptions.AppError;

public class AppService {
	
	private EntityManager em;

	private Query getQuery(String namedQuery, Boolean isNative){
		Query q = null;
		
		if(isNative) {
			q = getEm().createNativeQuery(namedQuery);
		}else {
			q = getEm().createNamedQuery(namedQuery);
		}
		return q; 
	}
	
	@SuppressWarnings("rawtypes")
	public Object findOne(String namedQuery, Map<String, Object> map, Boolean isNative){
		List lista = new ArrayList<>();
		
		try {
			Query q = getQuery(namedQuery, isNative);

			map.forEach( (k,v) -> {
				q.setParameter(k, v);
				
			});
			
			lista = q.getResultList();
			if (lista.size() > 0){
				return lista.get(0);
			}
			return null;
		} catch (Exception e) {
			throw new AppError("Erro ao executar operação @FindOne. Tente novamente mais tarde.", e);
		}
	}
	
//	public String gerarPdf(Map<String, Object> params, Collection<?> l, String nomeArquivo, String nomeRelatorio) {
//		String pdf = System.getProperty("java.io.tmpdir") + nomeArquivo + "_" + System.currentTimeMillis() + ".pdf";
//
//		try {
//
//			java.io.InputStream in = getClass().getResourceAsStream("/com/estudanteservice/relatorios/" + nomeRelatorio);
//			JRBeanCollectionDataSource dataSouce = new JRBeanCollectionDataSource(l);
//			JasperPrint print = JasperFillManager.fillReport(in, params, dataSouce);
//			JasperExportManager.exportReportToPdfFile(print, pdf);
//			print = null;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return pdf;
//	}
	
	@SuppressWarnings("rawtypes")
	public List findMany(String namedQuery, Map<String, Object> map, Boolean isNative){
		List lista = new ArrayList<>();
		
		try {
			Query q = getQuery(namedQuery, isNative);

			map.forEach( (k,v) -> {
				q.setParameter(k, v);
				
			} );
			lista = q.getResultList(); 
		
		} catch (Exception e) {
			throw new AppError("Erro ao executar operação @FindMany. Tente novamente mais tarde.", e);
		}

		return lista;
	}
	
	@UseTransaction
	public Boolean remove(AppEntity e) {
		
		try {
			e = em.find(e.getClass(), e.getId());
			em.remove(e);
			 
			return true;
			
		} catch (Exception e1) {
			throw new AppError("Não foi possível remover esse item. Tente novamente mais tarde.", e1);
		}
	}
	@UseTransaction	
	public <T> T merge(AppEntity e) {		
		this.em.merge(e);
		return (T) e;
	}
	
	public <T> T find(Class<T> clazz, Long id) {
		return (T) this.em.find(clazz, id);
	}
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	 
	
}
