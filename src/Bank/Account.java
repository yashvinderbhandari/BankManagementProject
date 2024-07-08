package Bank;

public class Account {
	 private int accountNumber;
	    private String accountHolder;
	    private double balance;
		public Account(int accountNumber, String accountHolder, double balance) {
			super();
			this.accountNumber = accountNumber;
			this.accountHolder = accountHolder;
			this.balance = balance;
		}
		public Account(){
			 this.accountNumber=0;
		}
		public int getAccountNumber() {
			return accountNumber;
		}
		public void setAccountNumber(int accountNumber) {
			this.accountNumber = accountNumber;
		}
		public String getAccountHolder() {
			return accountHolder;
		}
		public void setAccountHolder(String accountHolder) {
			this.accountHolder = accountHolder;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
	    
}


