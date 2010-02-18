/* DbStructureWriter.java
 * Author: nina
 * Date: Mar 28, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.update.dataset.DatasetAddStructure;
import ambit2.db.update.structure.CreateStructure;

public class DbStructureWriter extends AbstractRepositoryWriter<IStructureRecord, IStructureRecord> {
	protected DatasetAddStructure datasetAddStruc = new DatasetAddStructure();
	protected CreateStructure createStructure = new CreateStructure();
	protected PropertyValuesWriter propertyWriter;
	protected SourceDataset dataset;	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5831358598266151159L;

	public DbStructureWriter() {
		propertyWriter = new PropertyValuesWriter();
	}

	@Override
	public void open() throws DbAmbitException {
		super.open();
		propertyWriter.open();
	}
	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		propertyWriter.setConnection(connection);
	}	
	public SourceDataset getDataset() {
		return dataset;
	}
	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
		propertyWriter.setDataset(dataset);
	}	
	@Override
	public IStructureRecord write(IStructureRecord structure) throws SQLException,AmbitException, OperationNotSupportedException {

		writeStructure(structure);
		return structure;
	}

	public List<IStructureRecord> writeStructure(IStructureRecord structure) throws SQLException, AmbitException, OperationNotSupportedException {
		 List<IStructureRecord> sr = new ArrayList<IStructureRecord>();
        if (structure.getIdstructure() <= 0) {
        	createStructure.setObject(structure);
	        exec.process(createStructure);
        	writeDataset(structure);
       		writeProperties(structure);
            sr.add(structure);
        } else {
        	try {
        		writeDataset(structure);
        		writeProperties(structure);
        	} catch (AmbitException x) {
        		logger.warn(x);
        	}        	
        }
		return sr;
	}	

		
	public void close() throws SQLException {
        try {

        if (propertyWriter != null)
        	propertyWriter.close();
     
         } catch (SQLException x) {
            logger.error(x);
        }
        super.close();
	}
	
	protected void writeDataset(IStructureRecord structure) throws SQLException, AmbitException, OperationNotSupportedException {
		if (getDataset() == null) setDataset(new SourceDataset("Default"));
		
		datasetAddStruc.setObject(structure);
		datasetAddStruc.setGroup(dataset);
		exec.process(datasetAddStruc);
	}
	
	public void writeProperties(IStructureRecord structure) throws SQLException, AmbitException {
			propertyWriter.process(structure);
			structure.clearProperties();
	}	
	
}
