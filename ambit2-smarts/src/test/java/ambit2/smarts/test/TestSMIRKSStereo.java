package ambit2.smarts.test;

import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;

public class TestSMIRKSStereo extends TestCase
{
	protected static Logger logger = Logger.getLogger(TestSMIRKSStereo.class
			.getName());
	
	boolean FlagApplyStereoTransformation = true;

	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	SmilesParser smiParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	
	
	public TestSMIRKSStereo() {
		smrkMan.setFlagApplyStereoTransformation(FlagApplyStereoTransformation);
		smrkMan.setFlagProcessResultStructures(true);
		smrkMan.setFlagClearHybridizationBeforeResultProcess(true);
		smrkMan.setFlagClearImplicitHAtomsBeforeResultProcess(true);
		smrkMan.setFlagClearAromaticityBeforeResultProcess(true);
		smrkMan.setFlagAddImplicitHAtomsOnResultProcess(true);
		smrkMan.setFlagConvertAddedImplicitHToExplicitOnResultProcess(false);
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(true);
		smrkMan.getSmartsParser().mSupportDoubleBondAromaticityNotSpecified = false;
	}
	
	public static Test suite() {
		return new TestSuite(TestSMIRKSStereo.class);
	}
	
	IAtomContainer applySMIRKSReaction(String smirks, IAtomContainer target)
			throws Exception {

		
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals("")) {
			throw (new Exception("Smirks Parser errors:\n"
					+ smrkMan.getErrors()));
		}		

		if (smrkMan.applyTransformation(target, reaction))
			return target; // all products inside the atomcontainer, could be
							// disconnected
		else
			return null;
	}
	
	IAtomContainer applySMIRKSReaction(String smirks, String targetSmiles, boolean explicitH)
			throws Exception {
		SmilesParser sp = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		IAtomContainer target = sp.parseSmiles(targetSmiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
		CDKHueckelAromaticityDetector.detectAromaticity(target);
		CDKHydrogenAdder h = CDKHydrogenAdder.getInstance(target.getBuilder());
		h.addImplicitHydrogens(target);
		if (explicitH)
			MoleculeTools.convertImplicitToExplicitHydrogens(target);
		
		return applySMIRKSReaction(smirks, target);
	}
	
	void checkReactionResult(IAtomContainer resultProduct, String expectedProductsSmiles[]) throws Exception
	{
		String expAbsSmi[] = new String[expectedProductsSmiles.length];
		for (int i = 0; i < expAbsSmi.length; i++)
			expAbsSmi[i] = getAbsoluteSmiles(expectedProductsSmiles[i]);
							
		IAtomContainerSet products = ConnectivityChecker.partitionIntoMolecules(resultProduct);
		for (int i = 0; i < products.getAtomContainerCount(); i++)
		{
			IAtomContainer product = products.getAtomContainer(i);
			String prodSmi = SmilesGenerator.absolute().create(product);
			boolean expectedProd = false;
			for (int k = 0; k < expAbsSmi.length; k++)
				if (prodSmi.equals(expAbsSmi[k]))
				{
					expectedProd = true;
					break;
				}
			
			assertEquals("Searching result product: " + prodSmi , true, expectedProd);
		}
	}
	
	String getAbsoluteSmiles(String smi) throws Exception
	{
		IAtomContainer mol = smiParser.parseSmiles(smi);
		return SmilesGenerator.absolute().create(mol);
	}
	
	//Transformations with SMIRKS containing stereo
	
	public void test01A() throws Exception 
	{
		String smirks = "[O:3][C:1]=[C:2][C:4]>>[O:3]/[C:1]=[C:2]/[C:4]";
		String target = "OC=CC";
		String expectedProducts[] = new String[] {"O/C=C/C"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test01B() throws Exception 
	{
		String smirks = "[O:3][C:1]=[C:2][C:4]>>[O:3]/[C:1]=[C:2]/[C:4]";
		String target = "O/C=C\\C";
		String expectedProducts[] = new String[] {"O/C=C/C"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test02() throws Exception 
	{
		String smirks = "[C:1][C@:2]([O:3])([N:4])[Cl:5]>>[C:1][C@@:2]([O:3])([N:4])[Cl:5]";
		String target = "C[C@](O)(N)Cl";
		String expectedProducts[] = new String[] {"C[C@@](O)(N)Cl"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test02B() throws Exception 
	{
		String smirks = "[C:1][C@:2]([O:3])([N:4])[Cl:5]>>[C:1][C@@:2]([O:3])([N:4])[Cl:5]";
		String target = "C[C@](O)(N)Cl";
		String expectedProducts[] = new String[] {"C[C@](N)(O)Cl"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test02C() throws Exception 
	{
		String smirks = "[C:1][C@:2]([O:3])([N:4])[Cl:5]>>[C:1][C@:2]([Cl:5])([N:4])[O:3]";
		String target = "C[C@](O)(N)Cl";
		String expectedProducts[] = new String[] {"C[C@](Cl)(N)O"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test03() throws Exception 
	{
		String smirks = "[C:1][C@H:2]([O:3])[N:4]>>[C:1][C@@H:2]([O:3])[N:4]";
		String target = "C[C@H](O)N";
		String expectedProducts[] = new String[] {"C[C@@H](O)N"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test03B() throws Exception 
	{
		String smirks = "[C:1][C@H:2]([O:3])[N:4]>>[C:1][C@@H:2]([O:3])[N:4]";
		String target = "C[C@H](O)N";
		String expectedProducts[] = new String[] {"C[C@H](N)O"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test03C() throws Exception 
	{
		String smirks = "[C:1][C@H:2]([O:3])[N:4]>>[C:1][C@@H:2]([O:3])[N:4]";
		String target = "C[C@@H](O)N";
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		assertEquals("Exptected reaction " + smirks + " not applicable for " + target, null, resultProduct);
	}
	
	public void test04() throws Exception 
	{
		String smirks = "[C:1][C@:2]([O:3])([N:4])Cl>>[C:1][C@@:2]([O:3])([N:4])Br";
		String target = "C[C@](O)(N)Cl";
		String expectedProducts[] = new String[] {"C[C@@](O)(N)Br"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	
	
	
	
	
	
	
	//Transformation on chemical objects with stereo elements
	
	public void test101() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "C[C@](O)(CC)Cl";
		String expectedProducts[] = new String[] {"C[C@](N)(CC)Cl"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test102() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "O/C=C/C";
		String expectedProducts[] = new String[] {"N/C=C/C"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test103() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "C[C@H](O)Cl";
		String expectedProducts[] = new String[] {"C[C@H](N)Cl"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test103B() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "C[C@H](O)Cl";
		String expectedProducts[] = new String[] {"C[C@H](N)Cl"};
		boolean FlagExplicitH = true;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test103C() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "C[C@]([H])(O)Cl";
		String expectedProducts[] = new String[] {"C[C@H](N)Cl"};
		boolean FlagExplicitH = true;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test103D() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "C[C@H](O)Cl";
		String expectedProducts[] = new String[] {"C[C@@H](Cl)N"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	/*
	 * Exception is thrown
	 * 
	 * java.lang.IllegalArgumentException: expected two ligand bonds
	at org.openscience.cdk.stereo.DoubleBondStereochemistry.<init>(DoubleBondStereochemistry.java:53)
	at ambit2.smarts.StereoChemUtils.bondChange(StereoChemUtils.java:845)
	
	public void test104() throws Exception 
	{
		//This one does not need FlagApplyStereoTransformation
		
		String smirks = "[C:1]N>>[C:1]Cl";
		String target = "O/C=C(N)/C";
		String expectedProducts[] = new String[] {"O/C=C(Cl)/C"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	*/
	
	
	//tu.testSMIRKS("[C:1]N>>[C:1]Cl", "O/C=C(\\N)C");  //This is not so clear what should happen
	
	
	public void test105() throws Exception 
	{
		//This one is quite rare.
		//And it is not so clear what should happen but it works! 
		String smirks = "O[C:1]Cl>>N[C:1]Br";
		String target = "C[C@](O)(CC)Cl";
		String expectedProducts[] = new String[] {"C[C@](N)(CC)Br"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test106() throws Exception 
	{
		//This one is quite rare.
		//And it is not so clear what should happen but it works! 
		String smirks = "[C:1][C:2]([O:3])([N:4])Cl>>[C:1][C:2]([O:3])([N:4])Br";
		String target = "C[C@](O)(N)Cl";
		String expectedProducts[] = new String[] {"C[C@](O)(N)Br"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	
}
