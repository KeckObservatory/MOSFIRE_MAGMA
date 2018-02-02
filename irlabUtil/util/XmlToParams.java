package edu.ucla.astro.irlab.util;

import org.jdom.*;
import java.util.*;
import java.io.File;
import java.io.IOException;


/**
 * <p>Title: OSIRIS</p>
 * <p>Description: Utilities Package for OSIRIS GUIs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class XmlToParams {

  /**
   *  Constructor
   */
  public XmlToParams() {
  }

  /**
   *  Extracts settings defined in an XML file and sets them in a Java Object.
   *  Each XML tag must have the datatype as the name of the tag, and have the
   *  attribute <code>"paramName"</code> which identifies the field in the Java Object
   *  to receive the value of the parameter.  Accepted datatypes are: primatives, except char
   *  (<code>boolean</code>, <code>int</code>, <code>long</code>, <code>float</code>,
   *  <code>double</code>), <code>String</code>, <code>java.awt.Color</code>,
   *  <code>java.awt.Dimension</code>, <code>java.io.File</code>, <code>java.awt.Font</code>,
   *  and <code>java.awt.Point</code>.  <code>String[]</code> arrays are also allowed,
   *  using <code>StringArray</code> as the name of the tag.
   *  The java object passed in is used by other objects in the package to get the
   *  values defined in the XML file.
   *  <p>
   *  XML tags must be defined as follows:
   *  <ul>
   *  <li>All tags must have a <code>paramName</code> attribute, with the value mapped
   *  to the name of the corresponding Parameter object field name.
   *  <li><code>Color</code> tags must have <code>"red"</code>, <code>"green"</code>,
   *  and <code>"blue"</code> attributes, each set to integer values between 0 and 255.
   *  <li><code>Dimension</code> tags must have <code>"xs"</code> and <code>"ys"</code> attributes,
   *  each set to positive integer values.
   *  <li><code>File</code> tags must have a <code>"value"</code> attribute, setto
   *  the path of the file or directory.
   *  <li><code>Font</code> tags must have a <code>"name"</code> attribute, set to
   *  the name of the font; a <code>"style"</code> attribute, describing the face of the font,
   *  set to one of "plain", "bold", "italics", or "bold+italics"; and a
   *  <code>"size"</code> attribute, set to an integer value dictating the size in points of the font.
   *  <li><code>Point</code> tags must have <code>"x"</code> and <code>"y"</code> attributes,
   *  each set to positive integer values.
   *  <li> <code>Strings</code> and primatives must have a single <code>"value"</code>
   *  attribute giving the value of the paramater.
   *  </ul>
   *  <p>
   *  Example:
   *  <ul>
   *    <li>in XML File ("example.xml"):<br><br>
   *      <code>
   *      ...<br>
   *      &lt;String paramName="TITLE_DIALOG" value="XML Example Dialog" /&gt;<br>
   *      &lt;Dimension paramName="DIM_DIALOG" xs="400" ys="300" /&gt;<br>
   *      ...<br><br>
   *    </code>
   *    <li>in Java Param Class (ParamClass):<br><br>
   *    <code>
   *      public class ParamClass {<br>
   *      &nbsp;&nbsp;...<br>
   *	  &nbsp;&nbsp;public static String TITLE_DIALOG;<br>
   *	  &nbsp;&nbsp;public static java.awt.Dimension DIM_DIALOG;<br>
   *      &nbsp;&nbsp;<br>
   *      &nbsp;&nbsp;public ParamClass() {}<br>
   *      &nbsp;&nbsp;...<br>
   *      }<br><br>
   *    </code>
   *    <li>in calling Java program:<br><br>
   *      <code>
   *      ...<br>
   *      ParamClass myParams = new ParamClass();<br>
   *      JDialog myDialog;<br>
   *      <br>
   *	  try {<br>
   *      &nbsp;&nbsp;XmltoParams.extractParams(new File("example.xml"), myParams);<br>
   *      <br>
   *      &nbsp;&nbsp;myDialog = new JDialog(null, myParams.TITLE_DIALOG, false);<br>
   *      &nbsp;&nbsp;myDialog.setSize(myParams.DIM_DIALOG);<br>
   *      } catch (JDOMException e) {<br>
   *      ...<br>
   *    </code>
   *  </ul>
   *  @param  xmlFile        XML File from which values are read
   *  @param  paramObj       object whose fields are set to values defined in XML file
   *  @throws IOException    if XML file cannot be opened
   *  @throws JDOMException  if XML or <code>paramObj</code> is improperly formatted
   *  @throws InvalidEnvironmentVariableException if invalid environment variable used in File property
   *  @see    Color
   *  @see    Dimension
   *  @see    File
   *  @see    Font
   *  @see    Point
   */
  public static void extractParams(File xmlFile, Object paramObj) throws IOException, JDOMException, InvalidEnvironmentVariableException{
    //. open file and build local document model.  throws IOException or JDOMException on errors.
    org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
    org.jdom.Document myDoc = builder.build(xmlFile);

    //. get root element.  can be anything.
    Element root=myDoc.getRootElement();
    //. get children elements
    List elements=root.getChildren();
    //. loop through them
    for (Iterator ii = elements.iterator(); ii.hasNext(); ) {
      //. get current element
      Element current=(Element)ii.next();
      //. get name of element, which is datatype
      String paramType=current.getName();
      //. get name of field in param object
      String paramName=current.getAttribute("paramName").getValue();
      try {
	//. get a refenece to param object field
        java.lang.reflect.Field paramField=paramObj.getClass().getField(paramName);
	//. if it is a dimension
	if ("Dimension".equals(paramType)) {
	  //. get xs attribute
	  Attribute xsAtt=current.getAttribute("xs");
	  if (xsAtt == null)
	    throw new JDOMException("Invalid XML: Dimension tag must have xs attribute.");
	  //. get ys attribute
	  Attribute ysAtt=current.getAttribute("ys");
	  if (ysAtt == null)
	    throw new JDOMException("Invalid XML: Dimension tag must have ys attribute.");
	  //. set field in param object with xs and ys attribute values
	  paramField.set(paramObj, new java.awt.Dimension(xsAtt.getIntValue(),
	    ysAtt.getIntValue()));

	//. if it is a point
	} else if ("Point".equals(paramType)) {
	  //. get xs attribute
	  Attribute xAtt=current.getAttribute("x");
	  if (xAtt == null)
	    throw new JDOMException("Invalid XML: Point tag must have x attribute.");
	  //. get ys attribute
	  Attribute yAtt=current.getAttribute("y");
	  if (yAtt == null)
	    throw new JDOMException("Invalid XML: Point tag must have y attribute.");
	  //. set field in param object with xs and ys attribute values
	  paramField.set(paramObj, new java.awt.Point(xAtt.getIntValue(),
	    yAtt.getIntValue()));

	//. if it is a font
        } else if ("Font".equals(paramType)) {
	  int style;
	  //. get name attribute
	  Attribute nameAtt=current.getAttribute("name");
	  if (nameAtt == null)
	    throw new JDOMException("Invalid XML: Font tag must have name attribute.");
	  //. get style attribute
	  Attribute styleAtt=current.getAttribute("style");
	  if (styleAtt == null)
	    throw new JDOMException("Invalid XML: Font tag must have style attribute.");
	  //. get size attribute
	  Attribute sizeAtt=current.getAttribute("size");
	  if (sizeAtt == null)
	    throw new JDOMException("Invalid XML: Font tag must have size attribute.");

	  //. convert style string to java.awt.Font style integer constants
	  String styleString=styleAtt.getValue();
	  if (styleString.compareToIgnoreCase("plain")==0) {
	    style=java.awt.Font.PLAIN;
	  } else if (styleString.compareToIgnoreCase("bold")==0) {
	    style=java.awt.Font.BOLD;
	  } else if (styleString.compareToIgnoreCase("italic")==0) {
	    style=java.awt.Font.ITALIC;
	  } else if ((styleString.compareToIgnoreCase("bold+italic")==0) ||
            (styleString.compareToIgnoreCase("italic+bold")==0) ) {
	    style=java.awt.Font.BOLD+java.awt.Font.ITALIC;
	  } else {
	    throw new JDOMException("Invalid font style ("+styleString+
	      "). Must be one of: PLAIN, BOLD, ITALIC, BOLD+ITALIC.");
	  }
	  //. set field in param object with name, style, and size attribute values
	  paramField.set(paramObj, new java.awt.Font(nameAtt.getValue(), style,
	    sizeAtt.getIntValue()));

	//. if it is a color
        } else if ("Color".equals(paramType)) {
	  //. get red attribute
	  Attribute redAtt=current.getAttribute("red");
	  if (redAtt == null)
	    throw new JDOMException("Invalid XML: Color tag must have red attribute.");
	  //. get green attribute
	  Attribute greenAtt=current.getAttribute("green");
	  if (greenAtt == null)
	    throw new JDOMException("Invalid XML: Color tag must have green attribute.");
	  //. get blue attribute
	  Attribute blueAtt=current.getAttribute("blue");
	  if (blueAtt == null)
	    throw new JDOMException("Invalid XML: Color tag must have blue attribute.");
	  //. set field in param object with red, green, and blue attribute values
	  paramField.set(paramObj, new java.awt.Color(redAtt.getIntValue(), greenAtt.getIntValue(), blueAtt.getIntValue()));

	//. if it is a File
	} else if ("File".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: File tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.set(paramObj, new File(FileUtilities.replaceEnvironmentVariables(valueAtt.getValue())));

	//. if it is a string
	} else if ("String".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: String tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.set(paramObj, new String(valueAtt.getValue()));
	//. if it is a boolean
        } else if ("boolean".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: boolean tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.setBoolean(paramObj, valueAtt.getBooleanValue());
	//. if it is a int
        } else if ("int".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: int tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.setInt(paramObj, valueAtt.getIntValue());
	//. if it is a long
        } else if ("long".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: long tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.setLong(paramObj, valueAtt.getLongValue());
	//. if it is a float
        } else if ("float".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: float tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.setFloat(paramObj, valueAtt.getFloatValue());
	//. if it is a double
        } else if ("double".equals(paramType)) {
	  //. get value attribute
	  Attribute valueAtt=current.getAttribute("value");
	  if (valueAtt == null)
	    throw new JDOMException("Invalid XML: double tag must have a value attribute.");
	  //. set field in param object with value attribute value
	  paramField.setDouble(paramObj, valueAtt.getDoubleValue());
        } else if ("StringArray".equals(paramType)) {
	  //. get children
	  List stringArrayChildren = current.getChildren();
	  int jjI=0;
	  String[] stringArray = new String[stringArrayChildren.size()];
	  for (Iterator jj = stringArrayChildren.iterator(); jj.hasNext();) {
	    Element stringArrayChild = (Element)jj.next();
	    if (!stringArrayChild.getName().equals("String"))
	      throw new JDOMException("Invalid XML: StringArray children must be String types.");

	    Attribute valueAtt=stringArrayChild.getAttribute("value");
	    if (valueAtt == null)
	      throw new JDOMException("Invalid XML: String tag in StringArray must have a value attribute.");

	    stringArray[jjI] = valueAtt.getValue();
	    jjI++;
	  }
	  //. set field in param object with value attribute value
	  paramField.set(paramObj, stringArray);
        } else {
	  //. if it isn't one of the accepted datatypes, throw exception
	  throw new JDOMException("Illegal datatype for parameter ("+paramType
	    +"). Must be one of: boolean, int, long, float, double, String, Color, Dimension, File, Font, Point");
        }
      } catch (NoSuchFieldException nsfE) {
	//. if field doesn't exist in param object, throw exception
	throw new JDOMException("Error setting attribute "+nsfE.getMessage()+" in parameter object. "
	  +"Make sure paramName fields in XML file match variable names in the Parameter class. ");
      } catch (IllegalAccessException iaE) {
	//. if error accessing field in object, throw exception
	throw new JDOMException("Error accessing field in parameter object for "+paramName+". "
	  +"Make sure field exists and is declared public. IllegalAccessException message:"
	  +iaE.getMessage());
      } catch (IllegalArgumentException iarE) {
	//. if error setting value of field in object, throw exception
	throw new JDOMException("Error setting value for "+paramName+" in parameter object.  Make sure datatypes are correct.");
      }
    }
  }

}
