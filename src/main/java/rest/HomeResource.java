package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import infra.Fill;
import persistence.service.CriancaService;

@Path("/home")
public class HomeResource extends AppResource{
	
	@Fill
	private CriancaService criancaService;
	
	@GET
	@Path("/inicial")
	@Produces(MediaType.APPLICATION_JSON)
	public Response home() {
		try {
			return super.handleResponse(criancaService.amountCrianca());
		} catch (Exception e) {
			return super.handleError(e.getMessage());
		}		
	}
	
}
