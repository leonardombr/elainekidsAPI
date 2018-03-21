package rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import infra.Fill;
import model.Usuario;
import persistence.service.LoginService;

@Path("/login")
public class LoginResource extends AppResource {
    
    @Fill
    private LoginService service;
    
    @POST
    @Path("/efetuarLogin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response efetuarLogin(Usuario usuario){
        try {
        	usuario = service.efetuarLogin(usuario);        	
        	return super.handleResponse(usuario);
        } catch (Exception e) {
            return super.handleError(e.getMessage());
        }
    };
    
    @GET
    @Path("/teste")
    @Produces
    public Response testeJson() {
    	return super.handleResponse("TesteOK");
    };
    
//    @GET
//    @Path("/getDiasDesabilitados/{idCidade}/{idServico}/{idUnidade}/{idAtendente}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public void getDiasDesabilitados(@Suspended final AsyncResponse ar, @PathParam("idCidade") Long idCidade,  @PathParam("idServico") Long idServico, @PathParam("idUnidade") Long idUnidade, @PathParam("idAtendente") Long idAtendente){
//        if(idUnidade == 0) {
//            idUnidade = null;
//        }
//        if(idAtendente == 0) {
//            idAtendente = null;
//        }
//        try {
//            DiasDesabilitados diasDesabilitados = new DiasDesabilitados();
//            diasDesabilitados.setIdCidade(idCidade);
//            diasDesabilitados.setIdServico(idServico);
//            diasDesabilitados.setIdUnidade(idUnidade);
//            diasDesabilitados.setIdAtendente(idAtendente);
//            
//            ar.resume(Response.ok(consumer.getDiasDesabilitados(diasDesabilitados)).build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            ar.resume(Response.status(500).build());
//        }
//    }

}

/*package rest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import infra.Fill;
import model.Usuario;
import persistence.service.LoginService;

@Path("/login")
public class LoginResource extends AppResource {
	
	@Fill
	private LoginService service;
	
	@POST
	@Path("/efetuarLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response efetuarAgendamento(Usuario usuario){
		try {

			System.out.println(usuario.getLogin());
			System.out.println(usuario.getSenha());
			
			service.efetuarLogin(usuario);
			
			return super.handleResponse("SIM");
			
		} catch (Exception e) {
			return super.handleResponse("NAO");
		}
	}
	 
	
//	@GET
//	@Path("/getDiasDesabilitados/{idCidade}/{idServico}/{idUnidade}/{idAtendente}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public void getDiasDesabilitados(@Suspended final AsyncResponse ar, @PathParam("idCidade") Long idCidade,  @PathParam("idServico") Long idServico, @PathParam("idUnidade") Long idUnidade, @PathParam("idAtendente") Long idAtendente){
//		if(idUnidade == 0) {
//			idUnidade = null;
//		}
//		if(idAtendente == 0) {
//			idAtendente = null;
//		}
//		try {
//			DiasDesabilitados diasDesabilitados = new DiasDesabilitados();
//			diasDesabilitados.setIdCidade(idCidade);
//			diasDesabilitados.setIdServico(idServico);
//			diasDesabilitados.setIdUnidade(idUnidade);
//			diasDesabilitados.setIdAtendente(idAtendente);
//			
//			ar.resume(Response.ok(consumer.getDiasDesabilitados(diasDesabilitados)).build());
//		} catch (Exception e) {
//			e.printStackTrace();
//			ar.resume(Response.status(500).build());
//		}
//	}

}*/
