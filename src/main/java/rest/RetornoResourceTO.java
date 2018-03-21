package rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RetornoResourceTO implements Serializable{

	private static final long serialVersionUID = 1L;

	@XmlElement
	private Boolean erro;
	
	@XmlElement
	private String mensagem;
	
	@XmlElement
	private Object value;
	
	public Boolean getErro() {
		return erro;
	}
	public void setErro(Boolean verificado) {
		this.erro = verificado;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
