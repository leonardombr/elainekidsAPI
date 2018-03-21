package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="foto")
public class Foto extends AppEntity{
	
	private static final long serialVersionUID = 1L;
	
	@OneToOne
	@JoinColumn(name="ID_CRIANCA")
	private Crianca crianca;
	
	@Column(name="PATH_FOTO")
	private String pathFoto;
	
	@Column(name="DT_CRIACAO")
	private String dataCriacao;
}
