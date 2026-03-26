package chc.framework.util.parsing.input;

import java.util.LinkedList;
import java.util.List;

import com.cbi.frw.common.exception.ChcException;

public abstract class FlowioBinder<D, T extends FlowioProperty> {

	protected LinkedList<T> properties = new LinkedList<>();
	protected Class<D> beanClass;
	
	public FlowioBinder(Class<D> beanClass) {
		this.beanClass = beanClass;
	}


	public void addProperty(T prop) {
		properties.add(prop);
	}
	
	
	public abstract D getObjectFromRow(String row) throws ChcException;
	
	public abstract String toStringRow(D doc) throws ChcException, NoSuchFieldException;


	public abstract D getObjectFromByteArray(List<Byte> bytes);


	protected abstract Byte[] toByteRow(D doc) throws NoSuchFieldException, SecurityException;


}
