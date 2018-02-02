package edu.ucla.astro.irlab.util;

public class DitherPatternParameter {
	private String name;
	private String units;
	private Object value;
	private ValueValidator validator;
	private String type;
	
	public DitherPatternParameter(String name, String units, Object value, ValueValidator validator) throws InvalidValueException {
		this.name = name;
		this.units = units;
		this.validator = validator;
		type = value.getClass().toString();
		setValue(value);
/*		System.out.println("Param: name=<"+name+">, type=<"+type+">"); */
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) throws InvalidValueException {
		if (validator != null) {
			if (!validator.isValueValid(value)) {
				throw new InvalidValueException(validator.getCriteria(), name, value.getClass().getName(), value.toString());
			}
		}
		try {
		if (type.equals("class java.lang.Byte")) {
			this.value = new Byte(value.toString());
		} else if (type.equals("class java.lang.Short")) {
			this.value = new Short(value.toString());
		} else if (type.equals("class java.lang.Integer")) {
			this.value = new Integer(value.toString());
		} else if (type.equals("class java.lang.Long")) {
			this.value = new Long(value.toString());
		} else if (type.equals("class java.lang.Float")) {
			this.value = new Float(value.toString());
		} else if (type.equals("class java.lang.Double")) {
			this.value = new Double(value.toString());
		} else {
			this.value = value.toString();			
		}
		} catch (NumberFormatException ex) {
			throw new InvalidValueException ("Value <"+value+"> could not be parsed to a <"+type+">.");
		}
	}
}
