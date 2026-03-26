package chc.framework.util.parsing.test.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import chc.framework.util.parsing.input.annotaion.FlowioProperty;


public class AccountSimple {

	private String number;
	private LocalDateTime orario;
	private LocalDate data;
	
	@FlowioProperty  
	private Integer iii;
	
	@FlowioProperty 
	private Long lll;
	
	@FlowioProperty(decimanDigits=6)  
	private Double ddd;

	@FlowioProperty(unsigned=true,decimanDigits=3)  
	private Float fff;
	
	@FlowioProperty(unsigned=true,decimanDigits=3)  
	private BigDecimal bbb;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public LocalDateTime getOrario() {
		return orario;
	}

	public void setOrario(LocalDateTime orario) {
		this.orario = orario;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Double getDdd() {
		return ddd;
	}

	public void setDdd(Double ddd) {
		this.ddd = ddd;
	}

	public Float getFff() {
		return fff;
	}

	public void setFff(Float fff) {
		this.fff = fff;
	}

	public BigDecimal getBbb() {
		return bbb;
	}

	public void setBbb(BigDecimal bbb) {
		this.bbb = bbb;
	}

	public Integer getIii() {
		return iii;
	}

	public void setIii(Integer iii) {
		this.iii = iii;
	}

	public Long getLll() {
		return lll;
	}

	public void setLll(Long lll) {
		this.lll = lll;
	}


	@Override
	public String toString() {
		return "Account [number=" + number + ", orario=" + orario + ", data=" + data + ", iii=" + iii
				+ ", lll=" + lll + ", ddd=" + ddd + ", fff=" + fff + ", bbb=" + bbb + "]";
	}

	
}
