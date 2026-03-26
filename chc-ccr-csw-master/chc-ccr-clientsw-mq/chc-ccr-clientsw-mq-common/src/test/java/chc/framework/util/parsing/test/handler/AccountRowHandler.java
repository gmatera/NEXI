//package chc.framework.util.parsing.test.handler;
//
//import org.junit.Assert;
//
//import chc.framework.util.parsing.test.model.Account;
//import npl.flowio.input.handler.RowHandler;
//
//public class AccountRowHandler implements RowHandler<Account>{
//
//	public void onRow(Account acc) {
//		
//		System.out.println("get "+acc);
//		if(acc.getNumber().equals("1111111111")) {
//			Assert.assertEquals(acc.getCustomer().getName().trim(), "zulu1");
//		}
////		if(acc.getNumber().equals("2222222222")) {
////			Assert.assertEquals(acc.getCustomer().getName().trim(), "zulu2");
////		}
//	}
//
//	@Override
//	public void endOfFile() {
//		
//	}
//
//}
