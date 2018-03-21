package rest;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.core.Response;

import infra.Fill;
import persistence.factory.ServiceFactory;
import persistence.util.ReflectionUtil;

public class AppResource {

	public AppResource() {
			
		fillService();			
	}

	public Response handleError(Object value){
		RetornoResourceTO to = new RetornoResourceTO();
		to.setErro(true);	
		to.setMensagem(value.toString());
		releaseService();
		
		return Response.status(200).entity(to).header("Content-Type", "application/json;charset=UTF-8").build();
	}
	
	public Response handleResponse(Object value){
		RetornoResourceTO to = new RetornoResourceTO();
		to.setErro(false);
		to.setValue(value);
		
		
		releaseService();
		
		return Response.status(200).entity(to).header("Content-Type", "application/json;charset=UTF-8").build();
	}
	
	private void releaseService(){
		try {
			List<Field> fields = ReflectionUtil.getFields(this);
			
			for (Field serviceField : fields) {
				if (serviceField.getAnnotation(Fill.class) != null){
					serviceField.setAccessible(true);
					
					Object service = serviceField.get(this);
					
					// seta o em para null
					Field emField = service.getClass().getSuperclass().getSuperclass().getDeclaredField("em");
					emField.setAccessible(true);
					emField.set(service, null);
					
					// seta o objeto para null
					serviceField.set(this, null);
				}
			}
		} catch (Exception e) {
			System.out.println("ERRO AO FINALIZAR @FILL NA CAMADA REST");
			e.printStackTrace();
		}
	}
	
	private void fillService() {
		try {
			List<Field> fields = ReflectionUtil.getFields(this);
			
			for (Field f : fields) {
				if (f.getAnnotation(Fill.class) != null){
					f.setAccessible(true);							
					Object servico = ServiceFactory.get(f.getType());
					f.set(this, servico);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}