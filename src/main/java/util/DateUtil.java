package util;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Date;

public class DateUtil {
	
	private static DateUtil instance;
	
	private final String dataPattern;
	private final String dataHoraPattern;
	private final String dataHoraSQLPattern;
	private final String horaMinutoPattern;
	private final String horaMinutoSegundoPattern;
	
	// CONSTRUTORES PRIVADOS
	
	private DateUtil() {
		dataPattern = "dd/MM/yyyy";
		dataHoraPattern = "dd/MM/yyyy HH:mm:ss";
		dataHoraSQLPattern = "yyyy-MM-dd HH:mm:ss";
		horaMinutoPattern = "HH:mm";
		horaMinutoSegundoPattern = "HH:mm:ss";
	}
	
	// MÉTODOS PRIVADOS
	
	private long getQtdSabados(LocalDate inicio, LocalDate fim) {
		long sabados = 0;
		int semanaInicio = inicio.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		int semanaFim = fim.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		
		if (semanaFim >= semanaInicio) {
			sabados = semanaFim - semanaInicio;
			sabados += fim.getDayOfWeek() == DayOfWeek.SATURDAY ? 1 : 0;
			
		} else {
			LocalDate ultimoDia = fim.withYear(inicio.getYear());
			ultimoDia = ultimoDia.withMonth(12);
			ultimoDia = ultimoDia.withDayOfMonth(31);
			sabados = getQtdSabados(inicio, ultimoDia);
			sabados += getQtdSabados(ultimoDia.plusDays(1), fim);
		}
		
		return sabados;
	}
	
	// MÉTODOS PÚBLICOS
	
	public Date convertToDate(LocalDate localDate) {
		if (localDate == null) {
			throw new NullPointerException("O LocalDate informado está nulo");
		}
		
		return java.sql.Date.valueOf(localDate);
	}
	
	public LocalDate convertToLocalDate(Date date) {
		if (date == null) {
			throw new NullPointerException("O Date informado está nulo");
		}
		
		return new java.sql.Date(date.getTime()).toLocalDate();
	}
	
	public LocalDateTime convertToLocalDateTime(Date date) {
		if (date == null) {
			throw new NullPointerException("O Date informado está nulo");
		}
		
		return new Timestamp(date.getTime()).toLocalDateTime();
	}
	
	public LocalTime convertToLocalTime(Long segundos) {
		if (segundos == null) {
			throw new NullPointerException("O tempo informado está nulo");
		}
		
		//return LocalTime.ofSecondOfDay(segundos);
		LocalTime localTime = LocalTime.of(0, 0, 0, 0);
		return localTime.plusSeconds(segundos);
	}
	
	public String formatToHoraMinutoString(LocalTime localTime) {
		if (localTime == null) {
			throw new NullPointerException("O LocalTime informado está nulo");
		}
		
		return localTime.format(DateTimeFormatter.ofPattern(horaMinutoPattern));
	}
	
	public String formatToHoraMinutoSegundoString(Duration duration) {
		if (duration == null) {
			throw new NullPointerException("O LocalTime informado está nulo");
		}
		
		long horas = duration.toHours();
		duration = duration.minusHours(horas);
		long minutos = duration.toMinutes();
		duration = duration.minusMinutes(minutos);
		StringBuilder str = new StringBuilder();
		
		if (horas < 10) {
			str.append(0);
		}
		
		str.append(horas).append(":");
		
		if (minutos < 10) {
			str.append(0);
		}
		
		str.append(minutos).append(":");
		long segundos = duration.getSeconds();
		
		if (segundos < 10) {
			str.append(0);
		}
		
		str.append(segundos);
		return str.toString();
	}
	
	public String formatToHoraMinutoSegundoString(Long segundos) {
		return formatToHoraMinutoSegundoString(Duration.ofSeconds(segundos));
	}
	
	public String formatToString(LocalDate localDate) {
		if (localDate == null) {
			throw new NullPointerException("O LocalDate informado está nulo");
		}
		
		return localDate.format(DateTimeFormatter.ofPattern(dataPattern));
	}
	
	public String formatToString(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			throw new NullPointerException("O LocalDateTime informado está nulo");
		}
		
		return localDateTime.format(DateTimeFormatter.ofPattern(dataHoraPattern));
	}
	
	public String formatToSQLString(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			throw new NullPointerException("O LocalDateTime informado está nulo");
		}
		
		return localDateTime.format(DateTimeFormatter.ofPattern(dataHoraSQLPattern));
	}
	
	public LocalDateTime getFimDoDia(LocalDate localDate) {
		if (localDate == null) {
			throw new NullPointerException("O LocalDate informado está nulo");
		}
		
		return localDate.atTime(23, 59, 59);
	}
	
	public static DateUtil getInstance() {
		if (instance == null) {
			instance = new DateUtil();
		}
		
		return instance; 
	}
	
	public long getNumDias(LocalDate inicio, LocalDate fim) {
		return ChronoUnit.DAYS.between(inicio, fim);
	}
	
	/**
	 * Calcula a quantidade de dias úteis entre duas datas.<br/>
	 * Os feriados não são considerados no cálculo.
	 * 
	 * @param inicio
	 * @param fim
	 * @return
	 */
	public long getNumDiasUteis(LocalDate inicio, LocalDate fim) {
		if (inicio == null) {
			throw new NullPointerException("A data inicial informada está nula");
		}
		
		if (fim == null) {
			throw new NullPointerException("A data final informada está nula");
		}
		
		long diasFimSemana = getQtdSabados(inicio, fim) * 2;
		long numDias = getNumDias(inicio, fim);
		return numDias - diasFimSemana;
	}
	
	/**
	 * Converte o tempo em segundos
	 * @param tempo  deve vir no formado hh:mm
	 * @return
	 */
	public long getSegundos(String tempo) {
		if (tempo == null || tempo.isEmpty()) {
			throw new IllegalArgumentException("O tempo informado está nulo ou vazio");
		}
		
		return getSegundos(LocalTime.parse(tempo));
	}
	
	public long getSegundos(LocalTime localTime) {
		if (localTime == null) {
			throw new NullPointerException("O LocalTime informado está nulo");
		}
		
		return localTime.toSecondOfDay();
	}
	
	public int getTrimestre(LocalDate localDate) {
		if (localDate == null) {
			throw new NullPointerException("O LocalDate informado está nulo");
		}
		
		return localDate.get(IsoFields.QUARTER_OF_YEAR);
	}
	
	public boolean isMesmoTrimestre(LocalDate inicio, LocalDate termino) {
		if (inicio == null) {
			throw new NullPointerException("A data inicial informada está nula");
		}
		
		if (termino == null) {
			throw new NullPointerException("A data final informada está nula");
		}
		
		return getTrimestre(inicio) == getTrimestre(termino);
	}
	
	public LocalDate parseToDate(String data) {
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("A string informada está nula ou vazia");
		}
		
		return LocalDate.parse(data, DateTimeFormatter.ofPattern(dataPattern));
	}
	
	public LocalDateTime parseToDateTime(String dataHora){
		if (dataHora == null || dataHora.isEmpty()) {
			throw new IllegalArgumentException("A string informada está nula ou vazia");
		}
		
		return LocalDateTime.parse(dataHora, DateTimeFormatter.ofPattern(dataHoraPattern));
	}
	
	public LocalTime parseToHoraMinutoLocalTime(String horaMinuto) {
		if (horaMinuto == null || horaMinuto.isEmpty()) {
			throw new IllegalArgumentException("A string informada está nula ou vazia");
		}
		
		return LocalTime.parse(horaMinuto, DateTimeFormatter.ofPattern(horaMinutoPattern));
	}
	
	public LocalTime parseToHoraMinutoSegundoLocalTime(String horaMinutoSegundo) {
		if (horaMinutoSegundo == null || horaMinutoSegundo.isEmpty()) {
			throw new IllegalArgumentException("A string informada está nula ou vazia");
		}
		
		return LocalTime.parse(horaMinutoSegundo, DateTimeFormatter.ofPattern(horaMinutoSegundoPattern));
	}
	
}
