/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.config.AmbitCONSTANTS;

/**
 * Reads XLS files. This implementation loads the workbook in memory which is inefficient for big files.
 * 
 * TODO find how to read it without loading into memory.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class IteratingXLSReader extends IteratingFilesWithHeaderReader {
	protected HSSFWorkbook workbook;
	protected HSSFSheet sheet;
	protected Iterator iterator;
	protected  InputStream input;
	//protected HSSFFormulaEvaluator evaluator;
	
	public IteratingXLSReader(InputStream input, int sheetIndex)  throws AmbitIOException {
		super();
		try {
			this.input = input;
			workbook = new HSSFWorkbook(input);
			sheet = workbook.getSheetAt(sheetIndex);
			//evaluator = new HSSFFormulaEvaluator(sheet, workbook);
			
		} catch (Exception x) {
			throw new AmbitIOException(x);
		}
	}
	@Override
	protected LiteratureEntry getReference() {
		return LiteratureEntry.getInstance(workbook.getSheetName(workbook.getSheetIndex(sheet)),getClass().getName());
	}
	public void processHeader() {
		iterator = sheet.rowIterator();
		//process first header line
		processHeader((HSSFRow)iterator.next());
		//skip rest of header lines
		for (int i=1; i < getNumberOfHeaderLines();i++)
			processHeader((HSSFRow)iterator.next());
	}

	public void close() throws IOException {
		input.close();
		input = null;
		iterator = null;
		sheet = null;
		workbook = null;

	}

	public boolean hasNext() {
		if (isHeaderEmpty()) {
	    	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_START.toString(),""));
			processHeader();
	    	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_STOP.toString(),""));
		}
		try {
			return iterator.hasNext();
		} catch (Exception x) {
			logger.error(x);
			return false;
		}
	}
	protected void processRow(IAtomContainer mol) {
		
	}
	public Object next() {
		IMolecule mol = null;
		Map properties = new Hashtable();
		try {
			HSSFRow row = (HSSFRow) iterator.next();
			
			for (int col = 0; col < getNumberOfColumns(); col++ ) {
				HSSFCell cell = row.getCell(col);
				Object value = null;
				if (cell != null)				
				switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_BOOLEAN:
				    	value = cell.getBooleanCellValue();
				    	break;
					case HSSFCell.CELL_TYPE_NUMERIC:
				    	value = cell.getNumericCellValue();
				    	break;
					case HSSFCell.CELL_TYPE_STRING:
				    	value = cell.getStringCellValue();
				    	break;
					case HSSFCell.CELL_TYPE_BLANK:
						value = "";
				    	break;
					case HSSFCell.CELL_TYPE_ERROR:
						value = "";
				    	break;
					case HSSFCell.CELL_TYPE_FORMULA: 
						try {
							value = cell.getStringCellValue();
					    	break;
						} catch (Exception x) {
							try {
								value = cell.getNumericCellValue();
							} catch (Exception z) {	
								x.printStackTrace(); 
							}
						}
					}
				else 
					value = "";
				try {
					if (smilesIndex == col) {
						try {
							mol = sp.parseSmiles(value.toString());
							properties.put(AmbitCONSTANTS.SMILES, value.toString());
						} catch (InvalidSmilesException x) {
							logger.warn("Invalid SMILES!\t"+value);
							properties.put(AmbitCONSTANTS.SMILES, "Invalid SMILES");
						}						
					} 
					else 
						if (col< getNumberOfColumns())
							properties.put(getHeaderColumn(col), value);
				} catch (Exception x) {
					x.printStackTrace();
				}
	
			}
			if (mol == null) mol = new Molecule();
			mol.setProperties(properties);
			processRow(mol);
		} catch (Exception x) {
			logger.error(x);
		}
		return mol;
		
	}
	
	protected void processHeader(HSSFRow row) {
			
			Iterator cols = row.cellIterator();
			TreeMap columns = new TreeMap();
			while (cols.hasNext()) {
				HSSFCell cell = (HSSFCell) cols.next();
				String value = cell.getStringCellValue();

				if (value.equals(defaultSMILESHeader))
					smilesIndex = cell.getColumnIndex();
				columns.put(new Integer(cell.getColumnIndex()), value);
			}
			Iterator i = columns.keySet().iterator();
			while (i.hasNext()) {
				Integer key = (Integer)i.next();
				setHeaderColumn(key.intValue(), columns.get(key).toString());
			}
	}

	public String toString() {
		return "Reads Microsoft Office Excel file (*.xls) " ;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return new XLSFileFormat();
    }
}


