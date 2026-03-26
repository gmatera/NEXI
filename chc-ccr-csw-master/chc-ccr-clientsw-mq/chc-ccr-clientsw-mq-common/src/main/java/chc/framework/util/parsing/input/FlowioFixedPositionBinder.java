package chc.framework.util.parsing.input;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.util.DateUtils;
import com.cbi.frw.common.util.DynamicFieldUtils;

import chc.framework.util.parsing.input.annotaion.FlowioProperty;
import lombok.NonNull;

public class FlowioFixedPositionBinder<D> extends FlowioBinder<D, FlowioPropertyFixed> {
	protected static Logger log = LoggerFactory.getLogger(FlowioFixedPositionBinder.class);
	
	public static final String ISO_DATE = "yyyy-MM-dd";
	public static final String ISO_DATE_TIME = "yyMMddHHmmss";
	public static final String ISO_DATE_TIME_ZONE_MICROSEC_FORMAT = "yyyyMMddHHmmssSSSSSSZ";
	public static final String ISO_DATE_TIME_ZONE_MICROSEC_PARSE = "yyyyMMddHHmmssnnnnnnZ";

	public FlowioFixedPositionBinder(Class<D> clazz) {
		super(clazz);
	}
	
	public int getSize() {
		FlowioPropertyFixed last = properties.getLast();
		if(last != null) {
			return last.getColumnEnd();
		}
		return 0;
	}

	@Override
	public D getObjectFromRow(String row) throws ChcException {
		String val = null;
		String property = null;

		D doc;
		try {
			doc = beanClass.getDeclaredConstructor().newInstance();
			if (row == null || row.trim().length() == 0)
				throw new ChcException(I18nCommon.ERR_GENERIC,false);

			for (FlowioPropertyFixed prop : properties) {
				// Handling Exception if the string is < of the end
				if (row.length() < prop.getColumnEnd()) {
					val = row.substring(prop.getColumnStart(), row.length());
				} else {
					val = row.substring(prop.getColumnStart(), prop.getColumnEnd());
				}

				
					property = prop.getName();
 					try {
						setValue(doc, property, val);
					} catch (Exception e) {
						log.debug("Unable to setValue for :"+doc.getClass().getSimpleName()+"- property:"+property+" - val:"+val);
					}
				
			}
			return doc;
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			throw new ChcException(I18nCommon.ERR_GENERIC);
					//String.format("Unable to instanziate class %S",beanClass.getSimpleName()));
		}
		

		

	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setValue(Object target, String property, String value)
			throws ChcException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (!PropertyUtils.getPropertyType(target, property).isEnum() && (value == null || value.trim().isEmpty()))
			return;

		Object val = value;
		
		try {
			if (String.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				val = value.trim();
			}
			if (LocalDateTime.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				val = DateUtils.parseDateTime(value);
			} else if (LocalDate.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				val = LocalDate.parse(value);
			} else if (Double.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))
					|| Float.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(value.indexOf(',') != -1)
					value = value.replace(",", ".");
				val = Double.parseDouble(value);
			} else if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(value.indexOf(',') != -1)
					value = value.replace(",", ".");
				val = new BigDecimal(value);
			} else if (Long.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				val = Long.parseLong(value);
			} else if (Integer.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				val = Integer.parseInt(value);
			} else if (PropertyUtils.getPropertyType(target, property).isEnum()) {
				if(StringUtils.isEmpty(value.trim())) {
					val = "I_BLANK";
				}else {
					val = "I_"+value.trim();
				}
				val = Enum.valueOf((Class<Enum>) PropertyUtils.getPropertyType(target, property), val.toString());
			} 
			DynamicFieldUtils.setValue(target, property, val);
		} catch (Exception e) {
			throw new ChcException(I18nCommon.ERR_GENERIC);
					//String.format("Error setting property %s to class %s value %s", property, beanClass, val));
		}
		
	}
	
	private int checkSign(Number n, StringBuilder num, FlowioProperty ann) {
		if(ann == null || ann.unsigned()) {
			return 0;
		}
		
		if(n.intValue() >= 0 ) {
			num.append("+");
		}else {
			num.append("-");
		}
		return 1;
	}
	
	private void appendStringValue(Object val, FlowioProperty ann, int dynLenght, StringBuilder sb) {
		if (val instanceof String) {
			sb.append(StringUtils.leftPad((String) val, dynLenght, ' '));
			return;
		}
		
		if (val instanceof Long || val instanceof Integer) {
			dynLenght -= checkSign((Number) val, sb, ann);
			sb.append(StringUtils.rightPad(val.toString(), dynLenght, '0'));
			return;
		}
		
		if (val instanceof Double || val instanceof Float) {
			int fraction = ann != null ? ann.decimanDigits() : 0;
			DecimalFormat formatter = new DecimalFormat();
			formatter.setMaximumFractionDigits(fraction);
			formatter.setMinimumFractionDigits(fraction);
			dynLenght -= checkSign((Number) val, sb, ann);
			sb.append( StringUtils.leftPad( formatter.format( Math.abs(((Number) val).doubleValue())).replace(".", ","), dynLenght, '0'));
			return;
		}
		
		if (val instanceof BigDecimal) {
			int fraction = ann != null ? ann.decimanDigits() : 0;
			dynLenght -= checkSign((Number) val, sb, ann);
			BigDecimal bd = (BigDecimal) val;
			bd = bd.setScale(fraction, RoundingMode.HALF_UP);
			sb.append( StringUtils.leftPad(bd.abs().toPlainString().replace(".", ","), dynLenght, '0'));
			return;
		}
		
		if(val instanceof LocalDateTime) {
			// 2003-01-27T00:00:00.000000000
			sb.append(DateUtils.format((LocalDateTime) val, FlowioFixedPositionBinder.ISO_DATE_TIME));
			return;
		}
		if(val instanceof LocalDate) {
			// 2003-01-27
			sb.append(DateUtils.format((LocalDate) val, FlowioFixedPositionBinder.ISO_DATE));
		}
		if(val instanceof ZonedDateTime) {
			// 2003-01-27T00:00:00.000000000
			sb.append(DateUtils.format((ZonedDateTime) val, FlowioFixedPositionBinder.ISO_DATE_TIME));
			return;
		}
	}
	
	@Override
	public String toStringRow(D doc) throws NoSuchFieldException {
		StringBuilder sb = new StringBuilder();
		Object val;
		FlowioProperty ann;
		int dynLenght;
		for (FlowioPropertyFixed prop : properties) {
			
			val = DynamicFieldUtils.getValue(doc, prop.getName());
			ann = doc.getClass().getDeclaredField(prop.getName()).getAnnotation(FlowioProperty.class);
			dynLenght = prop.getColumnEnd() - prop.getColumnStart();

			if(val == null) {
				return StringUtils.leftPad("", dynLenght, " ");
			}
			
			appendStringValue(val, ann, dynLenght, sb);
		}

		return sb.toString();
	}
	@Override
	public Byte[] toByteRow(D doc) throws NoSuchFieldException, SecurityException {
		StringBuilder sb = new StringBuilder();
	    Stream<Byte> stream = Stream.of();

		Object val;
		FlowioProperty ann;
		Object type;
		int dynLenght;
		for (FlowioPropertyFixed prop : properties) {
			
			val = DynamicFieldUtils.getValue(doc, prop.getName());
			ann = doc.getClass().getDeclaredField(prop.getName()).getAnnotation(FlowioProperty.class);
			type = 	doc.getClass().getDeclaredField(prop.getName()).getType();
			dynLenght = prop.getColumnEnd() - prop.getColumnStart();

			Byte[] byteObject = ArrayUtils.toObject(converToByteValue(val, ann, dynLenght, sb,type));

			
			
	        stream = Stream.concat(stream, Arrays.stream(byteObject));		
		}
		return stream.toArray(Byte[]::new);
	}
	
	
	private byte[] converToByteValue(Object val, FlowioProperty ann, int dynLenght, StringBuilder sb, Object type) {
		if (val == null) {
			if (type.equals(String.class)) {
				return createByteArray((byte) 0x20, dynLenght - 1);
			} else if (type.equals(Integer.class) || type.equals(Long.class) || type.equals(Double.class)
					|| type.equals(Float.class) || type.equals(BigDecimal.class)) {
				return createByteArray((byte) 0x30, dynLenght - 1);
			} else if (type.equals(byte[].class)) {
				return createByteArray((byte) 0x00, dynLenght - 1);
			} else {
				return createByteArray((byte) 0x20, dynLenght - 1);
			}
		}
				
		if (val instanceof byte[]) {
			byte[] byteVal = (byte[]) val;
			if (byteVal.length != dynLenght) {
				byteVal = Arrays.copyOf(byteVal, dynLenght);
			}
			return byteVal;
		}
	if (val instanceof String) {
		return StringUtils.rightPad((String) val, dynLenght, ' ').getBytes();
	}
	
	if (val instanceof Long || val instanceof Integer) {
		dynLenght -= checkSign((Number) val, sb, ann);
		return StringUtils.leftPad(val.toString(), dynLenght, '0').getBytes();
	}
	
	if (val instanceof Double || val instanceof Float) {
		int fraction = ann != null ? ann.decimanDigits() : 0;
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(fraction);
		formatter.setMinimumFractionDigits(fraction);
		dynLenght -= checkSign((Number) val, sb, ann);
		sb.append( StringUtils.leftPad( formatter.format( Math.abs(((Number) val).doubleValue())).replace(".", ","), dynLenght, '0'));
		return StringUtils.leftPad( formatter.format( Math.abs(((Number) val).doubleValue())).replace(".", ","), dynLenght, '0').getBytes();
	}
	
	if (val instanceof BigDecimal) {
		int fraction = ann != null ? ann.decimanDigits() : 0;
		dynLenght -= checkSign((Number) val, sb, ann);
		BigDecimal bd = (BigDecimal) val;
		bd = bd.setScale(fraction, RoundingMode.HALF_UP);
		sb.append( StringUtils.leftPad(bd.abs().toPlainString().replace(".", ","), dynLenght, '0'));
		return StringUtils.leftPad(bd.abs().toPlainString().replace(".", ","), dynLenght, '0').getBytes();
	}
	
	if(val instanceof LocalDateTime) {
		// 2003-01-27T00:00:00.000000000
		sb.append(DateUtils.format((LocalDateTime) val, FlowioFixedPositionBinder.ISO_DATE_TIME));
		return DateUtils.format((LocalDateTime) val, FlowioFixedPositionBinder.ISO_DATE_TIME).getBytes();
	}
	if(val instanceof LocalDate) {
		// 2003-01-27
		sb.append(DateUtils.format((LocalDate) val, FlowioFixedPositionBinder.ISO_DATE));
		return DateUtils.format((LocalDate) val, FlowioFixedPositionBinder.ISO_DATE).getBytes();

	}
	if(val instanceof ZonedDateTime) {
		// 2003-01-27T00:00:00.000000000
		sb.append(DateUtils.format((ZonedDateTime) val, FlowioFixedPositionBinder.ISO_DATE_TIME_ZONE_MICROSEC_FORMAT));
		return DateUtils.format((ZonedDateTime) val, FlowioFixedPositionBinder.ISO_DATE_TIME_ZONE_MICROSEC_FORMAT).getBytes();
	}
	return null;
}
		
	
	@Override
	public D getObjectFromByteArray(@NonNull List<Byte> row) {
		List<Byte> val = null;
		String property = null;

		D doc;
		try {
			doc = beanClass.getDeclaredConstructor().newInstance();

			for (FlowioPropertyFixed prop : properties) {
				// Handling Exception if the string is < of the end
				if (row.size() < prop.getColumnEnd()) {
					val = row.subList(prop.getColumnStart(), row.size());
				} else {
					val = row.subList(prop.getColumnStart(), prop.getColumnEnd());
				}
				property = prop.getName();
				setByteValue(doc, property, val);

			}
			return doc;

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException 
				| IndexOutOfBoundsException | IllegalArgumentException 
				| InstantiationException | SecurityException e1) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e1, e1.toString());
		}

	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setByteValue(Object target, String property, List<Byte>  value){

		Byte[] bytes = value.toArray(new Byte[value.size()]);
		String stringValue = new String(ArrayUtils.toPrimitive(bytes));
		
		try {
			if (!PropertyUtils.getPropertyType(target, property).isEnum() && (value == null || value.isEmpty()))
				return;
			
			
			Object val = value;

			if (String.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				val = stringValue.trim();
			}else if(ZonedDateTime.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(!StringUtils.isEmpty(stringValue.trim()))
					val = DateUtils.parseZonedDateTime(stringValue, FlowioFixedPositionBinder.ISO_DATE_TIME_ZONE_MICROSEC_PARSE);
				else
					val = null;
			}
			else if (LocalDateTime.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(!StringUtils.isEmpty(stringValue.trim())) {
					val = parseLctDate(stringValue);
				} else {
					val = null;
				}
			} 
			else if (LocalDate.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(!StringUtils.isEmpty(stringValue.trim())) {
					val = LocalDate.parse(stringValue);
				}else {
					val = null;
				}
			} else if (Double.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))
					|| Float.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(stringValue.indexOf(',') != -1)
					stringValue = stringValue.replace(",", ".");
				if (StringUtils.isEmpty(stringValue.trim())) {
					val = 0;
				} else {
					val = Double.parseDouble(stringValue);
				}
			} else if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if(stringValue.indexOf(',') != -1)
					stringValue = stringValue.replace(",", ".");				
				if (StringUtils.isEmpty(stringValue.trim())) {
					val = 0;
				} else {
					val = new BigDecimal(stringValue);
				}		
			} else if (Long.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if (StringUtils.isEmpty(stringValue.trim())) {
					val = 0L;
				} else {
					val = Long.parseLong(stringValue);
				}
			} else if (Integer.class.isAssignableFrom(PropertyUtils.getPropertyType(target, property))) {
				if (StringUtils.isEmpty(stringValue.trim())) {
					val = 0;
				} else {
					val = Integer.parseInt(stringValue);
				}
			} else if (PropertyUtils.getPropertyType(target, property).isEnum()) {
				if(StringUtils.isEmpty(stringValue.trim())) {
					val = "I_BLANK";
				}else {
					val = "I_"+stringValue.trim();
				}
				val = Enum.valueOf((Class<Enum>) PropertyUtils.getPropertyType(target, property), val.toString());
			} else {
				val =  ArrayUtils.toPrimitive(bytes);

			}
			
			DynamicFieldUtils.setValue(target, property, val);
		} catch (Exception e) {
			log.error("##### Unable to setValue for : {} - property:{} - val:{}", target.getClass().getSimpleName(), property, stringValue);
			// non lanciare eccezioni, in modo da creare comunque kìl'oggetto e passarlo al validator
			//throw new ChcException(I18nCommon.ERR_GENERIC, e);
		}
		
	}
	
	private byte[] createByteArray(byte byteChar, int size) {
		byte[] bytes = new byte[size + 1];
		for (int i = 0; i <= size; i++) {
			bytes[i] = byteChar;
		}
		return bytes;
	}

	public static LocalDateTime parseLctDate(String date) throws ParseException {
		if(!org.springframework.util.StringUtils.hasLength(date))
			return null;
		SimpleDateFormat lctFormat = new SimpleDateFormat(ISO_DATE_TIME);
		return DateUtils.fromDateToLocalDateTime(lctFormat.parse(date));
	}
	
}
