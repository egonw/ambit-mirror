package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.transformations.IHasValueTransformation;

public interface ICorrectionFactor extends IHasValueTransformation
{
	public enum Type
	{
		PREDEFINED, ATOM_PAIR, SMARTS;
		
		public static Type fromString(String s)
		{
			try
			{
				Type type  = Type.valueOf(s);
				return (type);
			}
			catch (Exception e)
			{
				return null;
			}
		}
	}
	   
	public double getContribution();
	public void setContribution(double contribution);
	public String getDesignation();	
	public double calculateFor(IAtomContainer mol);
	public Type getType();
}
