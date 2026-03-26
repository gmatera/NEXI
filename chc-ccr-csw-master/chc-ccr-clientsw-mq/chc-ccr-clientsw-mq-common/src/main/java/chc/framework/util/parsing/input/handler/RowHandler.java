package chc.framework.util.parsing.input.handler;

import com.cbi.frw.common.exception.ChcException;

public interface RowHandler<D> {

	public void onRow(D doc) throws ChcException;
//	public void endOfFile() throws WrongChecksumException, ChcException;
}
