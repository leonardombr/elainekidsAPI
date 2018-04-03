package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="usuario")
public class Usuario extends AppEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name="NM_USER")
	private String nome;
	
	@Column(name="LOGIN")
	private String login;
	
	@Column(name="SENHA")
	private String senha;
	
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
}
