package persistence.service;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import infra.UseTransaction;
import model.Usuario;
import rest.exceptions.AppError;
import rest.exceptions.AppException;

public class UsuarioService extends AppService {

	private static EntityTransaction tx = null;

	@UseTransaction
	public boolean salvar(Usuario usuario) throws AppException {

		try {
			if (validaNome(usuario) == true) {
				tx = getEm().getTransaction();
				getEm().persist(usuario);
				tx.commit();
				return true;
			} else {
				return false;
			}
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppException("Erro ao cadastrar usuario!");
		}
	}

	@UseTransaction
	public boolean atualizar(Usuario usuario) throws AppException {

		try {
			if (validaNome(usuario) == true) {
				tx = getEm().getTransaction();
				getEm().persist(getEm().merge(usuario));
				tx.commit();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new AppException("Erro ao editar usuário!");
		}
	}

	@UseTransaction
	public boolean excluir(Usuario usuario) throws AppException {

		try {
			tx = getEm().getTransaction();
			getEm().remove(getEm().merge(usuario));
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new AppException("Erro ao apagar usuário!");
		}
	}

	public List<Usuario> getAll() {
		try {
			TypedQuery<Usuario> q = getEm().createQuery("select u from Usuario u", Usuario.class);
			return q.getResultList();
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppError("Ocorreu um erro ao recuperar lista de usuários!");
		}
	}

	public Usuario getById(Long id) {
		try {
			Query q = getEm().createQuery("select u from Usuario u where u.id = :id", Usuario.class);
			q.setParameter("id", id);
			return (Usuario) q.getSingleResult();
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppError("Ocorreu um erro ao pesquisar usuário!", e);
		}
	}

	public boolean validaNome(Usuario usuario) {
		try {
			Query q = getEm().createQuery("select u from Usuario u where u.nome = :nome", Usuario.class);
			q.setParameter("nome", usuario.getNome());
			Usuario usuarioValidate = (Usuario) q.getSingleResult();
			if (usuarioValidate.getId() == usuario.getId()) {
				return true;
			} else {
				return false;
			}
		} catch (AppException e) {
			throw e;
		} catch (NoResultException e) {
			return true;
		} catch (Exception e) {
			throw new AppError("Erro ao validar nome de cadastro!");
		}
	}

}
