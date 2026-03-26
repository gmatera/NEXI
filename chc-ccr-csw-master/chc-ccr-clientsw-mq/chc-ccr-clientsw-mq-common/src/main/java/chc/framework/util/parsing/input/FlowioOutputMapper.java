package chc.framework.util.parsing.input;

import org.apache.commons.lang3.ArrayUtils;

import com.cbi.frw.common.exception.ChcException;

public class FlowioOutputMapper<DOC> {

	private FlowioBinder<DOC, FlowioPropertyFixed> flowioBinder;

	public FlowioOutputMapper(FlowioBinder<DOC, FlowioPropertyFixed> flowioBinder) {
		super();
		this.flowioBinder = flowioBinder;
	}

//	public String write(DOC doc) throws NoSuchFieldException, ChcException {
//		return flowioBinder.toStringRow(doc);
//	}
	
	public byte[] writeByte(DOC doc) throws NoSuchFieldException {
		return  ArrayUtils.toPrimitive(flowioBinder.toByteRow(doc));
	}
	
}
