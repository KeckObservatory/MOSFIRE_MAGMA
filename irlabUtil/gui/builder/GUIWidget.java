package edu.ucla.astro.irlab.util.gui.builder;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;

import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;
import edu.ucla.astro.irlab.util.PropertySetter;
import edu.ucla.astro.irlab.util.ValueFormatter;
import edu.ucla.astro.irlab.util.ValueValidator;
import edu.ucla.astro.irlab.util.gui.StatusLightPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.lang.reflect.Method;
import java.util.Hashtable;

public class GUIWidget extends GUIComponent {
	private String propertyName="";
	private String widgetType="";
	protected PropertySetter propertySetter;
  protected ValueValidator valueValidator;
  private ValueFormatter valueFormatter;
  private String[] propertyNames = {""};
  private String[] propertySetValues = {""};
  private String propertySetValue;
  private boolean valueSetByAction=true;
  private Hashtable<String, DecimalFormat> formatters = new Hashtable<String, DecimalFormat>();

	public static String WIDGET_TYPE_STATUS = "status";
	public static String WIDGET_TYPE_CONTROL = "control";
	public static String WIDGET_TYPE_SET_CONTROL = "setcontrol";
	public static String PROPERTY_NAME_DELIMITER = ",";
	public GUIWidget(String name, JComponent component) {
		super(name, component);
		if (component instanceof JButton) {
			((JButton)component).setText(name);
		}
	}
	public void setValue(Object value) {
		setValue(propertyNames[0], value);
	}
	public void setValue(String propName, Object value) {
		JComponent myComponent = super.getComponent();
		String valueString = value.toString();
		//. use ValueFormatter is specified
		if (valueFormatter != null) {
			//. TODO do we need an exception?
			valueString = valueFormatter.getFormattedString(value);
		} else {
			DecimalFormat formatter = formatters.get(propName);
			//. otherwise, use property formatter, if specified
			if (formatter != null) {
				if (value instanceof Double) {
					valueString = formatter.format(((Double)value).doubleValue());
				} else if (value instanceof Integer) {
					valueString = formatter.format(((Integer)value).longValue());
				}
			}
		}
		
		if (myComponent instanceof JLabel) {
			((JLabel)myComponent).setText(valueString);
		} else if (myComponent instanceof JTextField) {
			((JTextField)myComponent).setText(valueString);
		} else if (myComponent instanceof StatusLightPanel) {
			valueSetByAction=false;
			((StatusLightPanel)myComponent).setStatus(((Boolean)value).booleanValue());
			valueSetByAction=true;
		} else if (myComponent instanceof JProgressBar) {
			((JProgressBar)myComponent).setValue(((Integer)value).intValue());
			//. todo: change below to include units
			((JProgressBar)myComponent).setString(((Integer)value).toString()+"%");
		} else if (myComponent instanceof JComboBox) {
			valueSetByAction=false;
			((JComboBox)myComponent).setSelectedItem(value);
			valueSetByAction=true;
		} else {
			try {
			  //. try to see if the widget has a set value(String, Object) method
				Method setValueMethod = myComponent.getClass().getMethod("setValue", new Class[] {String.class, Object.class});
				setValueMethod.invoke(myComponent, propName, value);
			} catch (NoSuchMethodException nsmEx) {
			  System.out.println("Cannot set value <"+value.toString()+"> for <"+super.getComponentName()+">:component does not have a setValue(String, Object) method.");
				//nsmEx.printStackTrace();
			} catch (IllegalArgumentException iaEx) {
			  System.out.println("Cannot set value <"+value.toString()+"> for <"+super.getComponentName()+">: illegal value <"+value.toString()+">");
				iaEx.printStackTrace();
			} catch (Exception ex) {
			  System.out.println("Cannot set value <"+value.toString()+"> for <"+super.getComponentName()+">:unknown component type <"+myComponent.toString()+">.");
			}
		}
	}
	public void implementControl() {
		//. todo: make consistent with SetterGUIWidget
		JComponent comp = super.getComponent();
		if (comp instanceof JComboBox) {
			((JComboBox)comp).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					componentActionPerformed();
				}
			});
		} else if (comp instanceof StatusLightPanel) {
			((StatusLightPanel)comp).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					componentActionPerformed();
				}
			});
		} else if (comp instanceof JButton) {
			((JButton)comp).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					componentActionPerformed();
				}
			});
		}
	}
	public void componentActionPerformed() {
		if (propertySetter != null) {
			JComponent comp = super.getComponent();

			//. do value validation?
			//if (valueValidator != null) {
			//	if (!valueValidator.isValueValid(value)) {
			//		System.out.println(valueValidator.getCriteria());
			//		return;
			//	}
			//}

			for (int ii=0; ii<propertyNames.length; ii++) {
				try {
					if (comp instanceof JComboBox) {
						if (valueSetByAction) {
							propertySetter.setNewPropertyValue(propertyNames[ii], ((JComboBox)comp).getSelectedItem().toString());
						}
					}	else if (comp instanceof StatusLightPanel) {
						if (valueSetByAction) {
							propertySetter.setNewPropertyValue(propertyNames[ii], Boolean.toString(!((StatusLightPanel)comp).getStatus()));
						}
					}	else if (comp instanceof JButton) {
						String value = "true";
						if (ii<propertySetValues.length) {
							if (!propertySetValues[ii].isEmpty()) {
								value = propertySetValues[ii];
							}
						}
						propertySetter.setNewPropertyValue(propertyNames[ii], value);
					}
				} catch (NoSuchPropertyException nspEx) {
					JOptionPane.showMessageDialog(super.getComponent(), "Error setting property <"+propertyNames[ii]+">: "+nspEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
				} catch (InvalidValueException ivEx) {
					JOptionPane.showMessageDialog(super.getComponent(), "Error setting property <"+propertyNames[ii]+">: "+ivEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	public String getPropertyName() {
		return propertyName;
	}
	public String[] getPropertyNames() {
		return propertyNames;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		//. parse propertyName for multiple properties
		propertyNames = propertyName.split(PROPERTY_NAME_DELIMITER);
	}
	public boolean containsProperty(String propertyName) {
		for (int ii=0; ii<propertyNames.length; ii++) {
			if (propertyNames[ii].trim().compareTo(propertyName) == 0) {
				return true;
			}
		}
		return false;
	}
	public String getPropertySetValue() {
		return propertySetValue;
	}
	public String[] getPropertySetValues() {
		return propertySetValues;
	}
	public void setPropertySetValue(String propertySetValue) {
		this.propertySetValue = propertySetValue;
		//. parse propertySetValue for multiple properties
		propertySetValues = propertySetValue.split(PROPERTY_NAME_DELIMITER);
	}
	public String getWidgetType() {
		return widgetType;
	}
	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}
	public void setPropertySetter(PropertySetter propSetter) {
		propertySetter = propSetter;
		if (super.getComponent() instanceof GUIBuilderCustomControl) {
			((GUIBuilderCustomControl)super.getComponent()).setPropertySetter(propSetter);
		}
	}
	public void setValueValidator(ValueValidator newValueValidator) {
		valueValidator = newValueValidator;
	}
	public void setValueFormatter(ValueFormatter newValueFormatter) {
		valueFormatter = newValueFormatter;
	}
	public void setFormat(String propertyName, String format) {
		DecimalFormat formatter = formatters.get(propertyName);
		if (formatter == null) {
			formatter = new DecimalFormat(format);
			formatters.put(propertyName, formatter);
		} else {
			formatter.applyPattern(format);
		}
	}
}
