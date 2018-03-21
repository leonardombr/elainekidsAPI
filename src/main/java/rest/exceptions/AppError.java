package rest.exceptions;

import javax.ws.rs.WebApplicationException;

public class AppError extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	public AppError(String mensagem, Throwable t) {
		super(mensagem, t);
	}
	
	public AppError(String mensagem) {
		super(mensagem);
	}
}
