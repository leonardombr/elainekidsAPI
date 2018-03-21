package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="crianca")
public class Crianca  extends AppEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name="NM_CRIANCA")
	private String nome;
	
	@Column(name="NM_MAE")
	private String nomeMae;
	
	@Column(name="NM_PAI")
	private String nomePai;
	
	@Column(name="IDADE")
	private String idade;
	
	@Column(name="ENDERECO")
	private String endereco;
	
	@Column(name="SEXO")
	private String sexo;
	
	@Column(name="DT_CRIACAO")
	private Date DtCriacao;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Date getDtCriacao() {
		return DtCriacao;
	}

	public void setDtCriacao(Date string) {
		DtCriacao = string;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
