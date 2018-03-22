package rest;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import infra.Fill;
import model.Crianca;
import model.PesquisaFilter;
import persistence.service.CriancaService;

@Path("/crianca")
public class CriancaResource extends AppResource {
	
	private Calendar d = Calendar.getInstance();
	private Date data = d.getTime();
	
	
	
	@Fill
	private CriancaService service;
	
	
	@POST
	@Path("/salvar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response salvar(Crianca crianca) {
		if (crianca.getId() == null) {
			try {
				crianca.setDtCriacao(data);
				if (service.salvar(crianca) == true) {
					return super.handleResponse("Cadastro realizado com sucesso!!!");
				} else {
					return super.handleError("Crianca já cadastrada!");
				}
			} catch (Exception e) {
				return super.handleError(e.getMessage());
			}
		} else {
			try {
				if (service.atualizar(crianca) == true) {
					return super.handleResponse("Edição realizada com sucesso!!!");
				} else {
					return super.handleError("Crianca já cadastrada!");
				}
			} catch (Exception e) {
				return super.handleError(e.getMessage());
			}
		}

	}

	@POST
	@Path("/editar")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response editar(Crianca crianca) {
		
		try {
			service.atualizar(crianca);
			return super.handleResponse("Edição realizada com sucesso!!!");
		}catch (Exception e) {
			return super.handleError(e.getMessage());
		}
		
	}
	
	@GET
	@Path("/excluir/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response apagar(@PathParam("id") Long id) {		
		try {
			service.excluir(service.getById(id));
			return super.handleResponse("Criança excluida com sucesso!");
		}catch (Exception e) {
			return super.handleError(e.getMessage());
		}
		
	}
	
	@GET
	@Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
	public Response listar() {		
		try {
			return super.handleResponse(service.getAll());
		}catch (Exception e) {
			return super.handleError(e.getMessage());
		}
		
	}
	
	@POST
	@Path("/pesquisar")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response search(PesquisaFilter parametro) {
		if(parametro.getNome() == null || parametro.getNome().equals("")){
			return listar();
		}else {
			try {
				return super.handleResponse(service.search(parametro));
			}catch (Exception e) {
				return super.handleError(e.getMessage());
			}
		}		
	}
	
	@GET
	@Path("/buscar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {		
		try {
			return super.handleResponse(service.getById(id));
		}catch (Exception e) {
			return super.handleError(e.getMessage());
		}
		
	}
	
}
