package persistence.service;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import infra.UseTransaction;
import model.Crianca;
import model.PesquisaFilter;
import rest.exceptions.AppError;
import rest.exceptions.AppException;

public class CriancaService extends AppService {
	private static EntityTransaction tx = null;

	@UseTransaction
	public boolean salvar(Crianca crianca) throws AppException {

		try {
			if (validaNome(crianca) == true) {
				tx = getEm().getTransaction();
				getEm().persist(crianca);
				tx.commit();
				return true;
			} else {
				return false;
			}
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppException("Erro ao cadastrar criança!");
		}
	}
	
	@UseTransaction
	public boolean atualizar(Crianca crianca) throws AppException {

		try {
			if (validaNome(crianca) == true) {
				tx = getEm().getTransaction();
				getEm().persist(getEm().merge(crianca));
				tx.commit();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new AppException("Erro ao editar criança!");
		}
	}

	public List<Crianca> getAll() {
		try {
			TypedQuery<Crianca> q = getEm().createQuery("select c from Crianca c", Crianca.class);
			return q.getResultList();
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppError("Ocorreu um erro ao recuperar lista de crianças!");
		}
	}

	public Crianca getById(Long id) {
		try {
			Query q = getEm().createQuery("select c from Crianca c where c.id = :id", Crianca.class);
			q.setParameter("id", id);
			return (Crianca) q.getSingleResult();
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppError("Ocorreu um erro ao pesquisar criança!", e);
		}
	}

	public List<Crianca> search(PesquisaFilter parametro) {
		try {
			TypedQuery<Crianca> q = getEm().createQuery("select c from Crianca c where c.nome LIKE :name", Crianca.class);
			q.setParameter("name", "%" + parametro.getNome() + "%");
			return q.getResultList();
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppError("Ocorreu um erro ao pesquisar criança!", e);
		}

	}
	
	public boolean validaNome(Crianca crianca) {		
		try {
			TypedQuery<Crianca> q = getEm().createQuery("select c from Crianca c where c.nome = :name", Crianca.class);
			q.setParameter("name", crianca.getNome());
			Crianca criancaValidate = new Crianca();
			criancaValidate = q.getSingleResult();
			
			if(criancaValidate.getNome().equals(crianca.getNome())){
				if(criancaValidate.getId() == crianca.getId()){
					return false;
				}
				return true;
			}else{
				return true;
			}
		} catch (AppException e) {
			throw e;
		} catch(Exception e) {
			throw new AppError("Erro ao validar nome de cadastro!");
		}
	}

	@UseTransaction
	public boolean excluir(Crianca crianca) throws AppException {

		try {
			tx = getEm().getTransaction();
			getEm().remove(getEm().merge(crianca));
			tx.commit();
			return true;
		} catch (Exception e) {
			throw new AppException("Erro ao apagar criança!");
		}
	}

}
