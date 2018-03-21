package persistence.service;

import javax.persistence.Query;

import model.Usuario;
import rest.exceptions.AppError;
import rest.exceptions.AppException;

public class LoginService extends AppService {
	
//	@UseTransaction
	public Usuario efetuarLogin(Usuario usuario) throws AppException{
		
		try {
			
			Query q = getEm().createQuery("select o from Usuario o where o.login = :login and o.senha = :senha");
			q.setParameter("login", usuario.getLogin());
			q.setParameter("senha", usuario.getSenha());
			try {
				return (Usuario) q.getSingleResult();
				
			} catch (Exception e) {
				throw new AppException("Usuario ou senha inválidos");
			}
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppError("Ocorreu um erro ao validar o login. Tente novamente mais tarde.", e);
		}
		
//		try {
//			
//			Query q = getEm().createNativeQuery("select ID, LOGIN, SENHA from usuario where LOGIN = '"+usuario.getLogin()+"' AND SENHA = '"+usuario.getSenha()+"';");
//			
//			List<Usuario> listaUsuario = new ArrayList<Usuario>();
//			listaUsuario = q.getResultList();
//			if(listaUsuario.size() > 0) {
//				return true;
//			}else {
//				return false;
//			}			
//		} catch (AppException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new AppError("Ocorreu um erro ao validar o login. Tente novamente mais tarde.", e);
//		}
		
	} 
	 
}
