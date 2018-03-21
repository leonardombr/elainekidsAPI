package persistence.util;

import java.io.Serializable;
import java.text.Normalizer;

public class StringUtil extends org.apache.commons.lang3.StringUtils implements Serializable{

	private static final long serialVersionUID = 1L;
	 
	public static void main(String[] args) {
		System.out.println(quebrarString("franklin souza", 3)); 
	}
	
	public static Boolean isExtensaoVideoValida(String nome){
		if (nome == null){
			return false;
		}
		if (!nome.endsWith(".mp4") && !nome.endsWith(".mpeg") && !nome.endsWith(".mov")){
			return false;
		}
		return true;
	}
	public static String quebrarString(String value, int tamanho){
		if (value.length() == tamanho){
			return value;
		}else if (value.length() > tamanho){
			return value.substring(0, tamanho) + "...";
		}
		return value;
	}
	public static String normalizeName(String valor){
		String realName = "";
		try {
			if (StringUtil.isEmpty(valor)){
				return "";
			}
			String nomes[] = valor.toLowerCase().split(" ");
			realName = "";
			
			for (String n : nomes) {
				if (isEmpty(n)){
					continue;
				}
				realName=realName+ " " + n.substring(0,1).toUpperCase() + n.substring(1,n.length());
			}
		} catch (Exception e) {
			realName = "Não definido";
		}
		return realName;
	}
	
	public static String criarNomeArquivoValido(String valor){
		return criarNomePastaValido(valor,"?","'","`","´","|","@","#","\"","$","%","&", "%",";",",",
				"*","(",")","=","+","-","/", "=","[","]","[", "{","}","<",">","!","~","\"").replaceAll(" ", "_");
	}
	
	public static String criarNomePastaValido(String valor){
		return criarNomePastaValido(valor,"?","'","`","´","|","@","#","\"","$","%","&", "%",".",";",",",
				"*","(",")","=","+","-","/", "=","[","]","[", "{","}","<",">","!","\"");
	}
	
	public static String criarNomePastaValido(String value, String... values){
		value = Normalizer.normalize(value, Normalizer.Form.NFD);
		value = value.replaceAll("[^\\p{ASCII}]", "");
		for (String c : values) {
			value=value.replace(c, ""); 
		}
		return value;
	}
	
	public static String getNumeroTicket(Long id){
		return "#TICKET" + id;
	}
	
	public static String replaceAcentuacao(String value){
		value = Normalizer.normalize(value, Normalizer.Form.NFD);  
		return value.replaceAll("[^\\p{ASCII}]", "");		
	}
	public static String captilize(String value){
		return org.apache.commons.lang3.text.WordUtils.capitalize(value.toLowerCase());
	}
}
