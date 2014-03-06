import java.awt.Color;
import java.lang.reflect.Field;

import org.dom4j.Element;


public class OntologyItem {
private String value;
private Color color;
private String colorName;
private String name;
private boolean annotation=false;
private Element element;
	public OntologyItem(Element _element) {
		// TODO Auto-generated constructor stub
		element=_element;
		value=element.attributeValue("value");
		name=element.attributeValue("name");
		colorName=element.attributeValue("color");	
		if(element.attributeValue("annotation")!=null){
			if(element.attributeValue("annotation").equals("true")){
				annotation=true;
			}
			else{
				annotation=false;
			}
		}
		try {
		    Field field = Class.forName("java.awt.Color").getField(colorName);
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = Color.white; // Not defined
		}
	}
public Element getElement(){
	return element;
}
	public String getValue(){
		return value;
	}
	public Color getColor(){
		return color;
	}
	public String getColorName(){
		return colorName;
	}
	public String getName(){
		return name;
	}
	public String toString(){
		return name;
	}
	public boolean isAnnotation() {
		// TODO Auto-generated method stub
		return annotation;
	}
	
}
