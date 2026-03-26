package chc.framework.util.parsing.test.model;

public class Transaction {

	private String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Transaction [number=" + number + "]";
	}

}
