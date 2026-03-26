package chc.framework.util.parsing.input.annotaion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FlowioProperty {
	
	public boolean unsigned() default false;
	public int decimanDigits() default 0;
}
