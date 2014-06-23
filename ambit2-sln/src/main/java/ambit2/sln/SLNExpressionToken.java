package ambit2.sln;


public class SLNExpressionToken 
{
	public int type;
	public String attrName = null;  //This is used for user defined attributes
	public int param = 0;
	public double doubleParam = 0;
	public String stringParam = null; //This is used for some types of attributes and user defined attributes 


	//Various constructors for different token types


	public SLNExpressionToken(int type)
	{
		//Logical opearion token
		this.type = type;
	}

	public SLNExpressionToken(int type, int param)
	{
		//Attribute with integer parameter
		this.type = type;
		this.param = param;
	}

	public SLNExpressionToken(int type, double doubleParam)
	{
		//Attribute with double parameter
		this.type = type;
		this.doubleParam = doubleParam;
	}

	public SLNExpressionToken(int type, String stringParam)
	{
		//Attribute with String parameter
		this.type = type;
		this.stringParam = stringParam;
	}

	public SLNExpressionToken(String attrName, String stringParam)
	{
		//User defined attribute
		this.type = SLNConst.A_ATTR_USER_DEFINED;;
		this.attrName = attrName;
		this.stringParam = stringParam;
	}

	public boolean isLogicalOperation()
	{
		if(type >= SLNConst.LO)
			return(true);
		return(false);
	}

	public int getLogOperation()
	{
		return(type - SLNConst.LO);
	}

	private String logOperationToString()
	{
		int lo = getLogOperation();
		switch (lo)
		{
		case SLNConst.LO_NOT:
			return "!";			
		case SLNConst.LO_AND:
			return "&";
		case SLNConst.LO_OR:
			return "|";
		case SLNConst.LO_ANDLO:
			return ";";
		}
		return "*";
	}

	private String attributeToString()
	{

		switch (type)
		{
		case SLNConst.A_ATTR_charge:			
			return SLNConst.atomAttributeToSLNString(type)  + "=" + param;

		case SLNConst.A_ATTR_I:			
			return SLNConst.atomAttributeToSLNString(type)  + "=" + param;	

		case SLNConst.A_ATTR_fcharge:			
			return SLNConst.atomAttributeToSLNString(type) + "=" + doubleParam;

		case SLNConst.A_ATTR_s:			
			return SLNConst.atomAttributeToSLNString(type) + "=" + SLNConst.atomStereoChemAttrToSLNString(param);

		case SLNConst.A_ATTR_spin:			
			return SLNConst.atomAttributeToSLNString(type) + "=" + SLNConst.atomSpinAttrToSLNString(param);	

		case SLNConst.A_ATTR_USER_DEFINED:			
			return "(" +SLNConst.atomAttributeToSLNString(type) + ")" +attrName + "=" + stringParam;
			
		case SLNConst.B_ATTR_type:
			return  SLNConst.bondAttributeToSLNString(type) + "=" + SLNConst.bondTypeAttributeToSLNString(param);
			
	//	case SLNConst.B_ATTR_s:
	//		return SLNConst.bondAttributeToSLNString(type) + "=" + SLNConst.bondStereoChemistryAttributeToSLNString(param);
		}
		return "a=b";
	}

	public String toString()
	{
		if (isLogicalOperation())
			return logOperationToString();
		else
			return attributeToString();
	}

}
