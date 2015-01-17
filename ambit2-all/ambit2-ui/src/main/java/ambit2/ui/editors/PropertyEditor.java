package ambit2.ui.editors;

import ambit2.base.data.Property;

public class PropertyEditor extends BeanEditor<Property> {

	public PropertyEditor() {
		
		super(null, new String[] {"name","label","units","title","url"},
				new String[] {"Name","Alias","units","url","WWW"},"");
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1857739692763387703L;

}