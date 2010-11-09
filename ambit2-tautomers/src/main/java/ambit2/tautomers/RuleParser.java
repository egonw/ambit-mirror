package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.smarts.SmartsParser;

public class RuleParser 
{
	String errors = "";
	Rule curRule = null;
	
	public Rule parse(String ruleString)
	{	
		System.out.println("rule: " + ruleString);
		errors = "";
		Rule rule = new Rule();
		curRule = rule;
		
		int res = ruleString.indexOf(TautomerConst.KeyWordPrefix, 0);
		int curPos = res;
		
		while (res != -1)
		{	
			res = ruleString.indexOf(TautomerConst.KeyWordPrefix, curPos+TautomerConst.KeyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = ruleString.substring(curPos);
			else
			{	
				keyword = ruleString.substring(curPos,res);
				curPos = res;	
			}
			
			parseKeyWord(keyword);
		}			
		
		
		postProcessing();
		
		//System.out.println("errors: " + errors);		
		if (errors.equals(""))
			return(rule);
		else
			return(null);
	}
	
	
	void postProcessing()
	{
		if (curRule.smartsStates.length <= 1)
		{	
			errors += "Too few states. At leats two states are need! \n";
			return;
		}	
		
		if (curRule.smartsStates.length != curRule.mobileGroupPos.length)
		{
			errors += "The number of states and number of group positionas are not the same!\n";
			return;
		}
		
		curRule.statePaterns = new QueryAtomContainer[curRule.smartsStates.length];
		SmartsParser sp = new SmartsParser();
		for (int i = 0; i<curRule.smartsStates.length; i++)
		{
			QueryAtomContainer q = sp.parse(curRule.smartsStates[i]);
			sp.setNeededDataFlags();
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))			
				errors += "Incorrect state description: " + errorMsg + "\n";
			else
				curRule.statePaterns[i] = q;
		}
	}
	
	void parseKeyWord(String keyWord)
	{		
		//System.out.println("   keyword: " + keyWord);
		int sepPos = keyWord.indexOf(TautomerConst.KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors += "Incorrect key word syntax: " + keyWord + "\n";
			return;
		}
		
		String key = keyWord.substring(TautomerConst.KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		//System.out.println(">"+keyValue+"<");
		
		if (key.equals("NAME"))	
		{	
			parseName(keyValue);
			return;
		}
		
				
		if (key.equals("TYPE"))
		{	
			parseType(keyValue);
			return;
		}
		
		if (key.equals("GROUP"))	
		{	
			parseGroup(keyValue);
			return;
		}
		
		if (key.equals("STATES"))	
		{	
			parseStates(keyValue);
			return;
		}
		
		if (key.equals("GROUP_POS"))	
		{	
			parseGroup_Pos(keyValue);
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			parseInfo(keyValue);
			return;
		}
		
		errors += "Unknow key word: " + key + "\n";
		
	}
	
	void parseName(String keyValue)
	{
		curRule.name = keyValue;
	}
	
	void parseType(String keyValue)
	{
		if (keyValue.equals("MOBILE_GROUP"))
		{	
			curRule.type = TautomerConst.RT_MobileGroup;
			return;
		}	
		
		errors += "Unknow rule type: " + keyValue + "\n";
	}
	
	void parseGroup(String keyValue)
	{
		curRule.mobileGroup = keyValue;
	}
	
	
	void parseStates(String keyValue)
	{	
		//String elements [] = keyValue.split(TautomerConst.KeyWordElementSeparator);
		
		String elements [] = keyValue.split(" ");
		curRule.smartsStates = new String[elements.length]; 
		for (int i = 0; i < elements.length; i++)
		{	
			String s = elements[i].trim();
			if(!s.equals(""))
				curRule.smartsStates[i] = s;
		}
		
	}
	
	void parseGroup_Pos(String keyValue)
	{
		Vector<String> elements = getStringElements(keyValue, TautomerConst.KeyWordElementSeparator);
		curRule.mobileGroupPos = new int[elements.size()]; 
		for (int i = 0; i < elements.size(); i++)
		{	
			try {
			int pos = Integer.parseInt(elements.get(i));
			curRule.mobileGroupPos[i]  = pos;
			}
			catch (Exception e)
			{
				errors += "Incorrect group position: " + keyValue + "\n";
			}
		}	
	}
	
	void parseInfo(String keyValue)
	{
		curRule.RuleInfo = keyValue.trim();
	}
	
	
	//Helper function --------------------------------------------------
	
	Vector<String> getStringElements(String string, String separator)
	{
		Vector<String> elements = new Vector<String>();
		int curPos = 0;
		int res = string.indexOf(separator, curPos);
		
		if (res == -1)
		{	
			elements.add(string);
			return(elements);
		}
		else
		{
			String el = string.substring(curPos,res);
			elements.add(el);
			curPos = res + separator.length();
		}
			
				
		while (res != -1)
		{	
			res = string.indexOf(separator, curPos + separator.length());
						
			if (res == -1)
			{	
				String el = string.substring(curPos);
				elements.add(el);
			}	
			else
			{	
				String el = string.substring(curPos,res);
				elements.add(el);
				curPos = res + separator.length();	
			}
		}	
		return (elements);
	}
	 
	
	
}
