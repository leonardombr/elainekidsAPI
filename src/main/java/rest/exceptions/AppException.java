package rest.exceptions;

import javax.ws.rs.WebApplicationException;

public class AppException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	public AppException(String mensagem) {
		super(mensagem);
	}
}
