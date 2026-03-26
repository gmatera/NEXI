package chc.framework.util.parsing.input;

import java.util.List;

import org.springframework.util.StringUtils;

import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;

import lombok.Getter;

public class FlowioInputMapper<D> {

	@Getter
	private FlowioBinder<D, FlowioPropertyFixed> flowioBinder;

	public FlowioInputMapper(FlowioBinder<D, FlowioPropertyFixed> flowioBinder) {
		super();
		this.flowioBinder = flowioBinder;
	}

//	@Deprecated
//	public D read(String line) throws ChcException {
//		if (StringUtils.hasText(line)) {
//			return flowioBinder.getObjectFromRow(line);
//		} else
//			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "message is empty");
//	}
//	
	public D read(List<Byte> bytes) throws ChcException {
		if (!bytes.isEmpty()) {
			return flowioBinder.getObjectFromByteArray(bytes);
		} else
			throw new ChcException(I18nCommon.ERR_NO_DATA, "message is empty");
	}
}
