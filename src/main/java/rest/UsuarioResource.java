package rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import infra.Fill;
import model.Usuario;
import persistence.service.UsuarioService;

@Path("/usuario")
public class UsuarioResource extends AppResource {

	@Fill
	private UsuarioService service;

	@POST
	@Path("/salvar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response salvar(Usuario usuario) {
		if (usuario.getId() == null) {
			try {
				if (service.salvar(usuario) == true) {
					return super.handleResponse("Cadastro realizado com sucesso!!!");
				} else {
					return super.handleError("Usuário já cadastrado!");
				}
			} catch (Exception e) {
				System.out.println(e);
				return super.handleError(e.getMessage());
			}
		} else {
			try {
				if (service.atualizar(usuario) == true) {
					return super.handleResponse("Edição realizada com sucesso!!!");
				} else {
					return super.handleError("Usuário já cadastrado!");
				}
			} catch (Exception e) {
				System.out.println(e);
				return super.handleError(e.getMessage());
			}
		}
	}

	@PUT
	@Path("/editar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(Usuario usuario) {
		try {
			service.atualizar(usuario);
			return super.handleResponse("Edição realizada com sucesso!!!");
		} catch (Exception e) {
			System.out.println(e);
			return super.handleError(e.getMessage());
		}
	}

	@DELETE
	@Path("/excluir/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response apagar(@PathParam("id") Long id) {
		try {
			service.excluir(service.getById(id));
			return super.handleResponse("Usuário excluido com sucesso!");
		} catch (Exception e) {
			System.out.println(e);
			return super.handleError(e.getMessage());
		}

	}
	
	@GET
	@Path("/buscar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {		
		try {
			return super.handleResponse(service.getById(id));
		}catch (Exception e) {
			System.out.println(e);
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
			System.out.println(e);
			return super.handleError(e.getMessage());
		}
		
	}
}
