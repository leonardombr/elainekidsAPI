package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class StringUtils {
	
	public static Boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
	
	public static double getSimilaridade(String str1, String str2){
		str1 = StringUtils.normalizeString(str1.toLowerCase());
		str2 = StringUtils.normalizeString(str2.toLowerCase());
		Set<Character> h1 = new HashSet<Character>();//para n�o deixar itens duplicados
		Set<Character> h2 = new HashSet<Character>();

		for(char c: str1.toCharArray())
			h1.add(c);		
		
		
		for(char c : str2.toCharArray())
			h2.add(c);		

		// deixando apenas a interse��o entre h1 e h2 em h1
		h1.retainAll(h2);

		// deixando apenas os elementos �nicos em h2
		h2.removeAll(h1);
		int uniao = h1.size() + h2.size();
		int intersecao = h1.size();

		return (double)intersecao/uniao;
	}
	
	public static String exceptionToString(Throwable e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));		
		return sw.toString();
	}
	
	

	
	/**
	 * de utf-8 para formato nativo do java
	 * @param s
	 * @return
	 */
	public static String convertFromUTF8(String s) {
		String retorno = null;
		try {
			retorno = new String(s.getBytes("ISO-8859-1"), "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return retorno;
	}

	/**
	 * String nativa para utf-8
	 * @param s
	 * @return
	 */
	public static String convertToUTF8(String s){
		String retorno = null;
		try {
			retorno = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return retorno;
	}
	
	public static void main(String[] args) {
		
		
		System.out.println(capitalize("jose de arimateira"));
	}
	public static String capitalize(String value){
		String result = "";
		String[] nomes = value.toLowerCase().split(" ");
		for(String palavra : nomes){			
			result = result + " " + palavra.replaceFirst(palavra.substring(0, 1), palavra.substring(0, 1).toUpperCase());
		}
		return result.trim();
	}
	
	public static String normalizeString(String s){
		try{
			String r = Normalizer.normalize(s, Normalizer.Form.NFD);
			r = r.replaceAll("[^\\x00-\\x7F]", "");
			return r;
		}catch(Throwable t){
			return s;
		}
	}
	public static String retirarMascara(String campo) {
		return campo.replaceAll("\\.", "").replaceAll("-", "").replaceAll("/", "").replace("(", "").replace(")", "").replace(" ", "");
	}
	/**
	 * Substitui os caracteres inv�idos para nome de arquivo
	 * 
	 * @param valor
	 * @return
	 */
	public static String substituirCaracteresNomeArquivo(String valor){
		if (valor == null || valor.trim().length() == 0){
			return valor;
		}
		return valor.replaceAll("[:]|[*]|[|]|[<]|[>]|[\"]|[?]|[\\/]|["+Pattern.quote("\\")+"] |[%]", "_");
	
	}
	
	public static String getMD5(String value) throws NoSuchAlgorithmException{
		MessageDigest d = MessageDigest.getInstance("MD5");
		d.update(value.getBytes());
		return new BigInteger(1,d.digest()).toString(16);
	}
	
	public static Boolean asBoolean(Object o){
		if (o instanceof Boolean){
			return (Boolean)o;
		}else if (o instanceof Byte){
		    return Byte.valueOf("1").equals(o);
		}else if (o instanceof Integer){
		    return Integer.valueOf(1).equals(o);
		}else if (o instanceof BigDecimal){
		    return BigDecimal.valueOf(1).equals(o);
		}else if (o instanceof String){
		    return "on".equals(o) || "yes".equals(o);
		}
		return "1".equals(o.toString());
	}

}
