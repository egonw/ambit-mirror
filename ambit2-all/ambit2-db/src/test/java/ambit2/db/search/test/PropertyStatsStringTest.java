/* PropertyStatsStringTest.java
 * Author: nina
 * Date: Apr 9, 2009
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

package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.db.search.property.PropertyStatsString;

public class PropertyStatsStringTest  extends QueryTest<PropertyStatsString>  {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";	
	}
	@Override
	protected PropertyStatsString createQuery() throws Exception {
		PropertyStatsString q = new PropertyStatsString();
		q.setFieldname(Property.getInstance("Property 1",""));
		q.setValue("C");
		return q;
	}

	@Override
	protected void verify(PropertyStatsString query, ResultSet rs)
			throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			String stat = query.getObject(rs);
			System.out.println(stat);
		}
		Assert.assertEquals(0,records);
		
	}

}
