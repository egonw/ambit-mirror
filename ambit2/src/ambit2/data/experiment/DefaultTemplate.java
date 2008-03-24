/* DefaultTemplate.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-2 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
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

package ambit2.data.experiment;

import java.util.Collection;

/**
 * Default template.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-2
 */
public class DefaultTemplate extends StudyTemplate {
    public static final String species = "Species";
    public static final String species_sex = "Species.sex";
    public static final String species_strain = "Species.strain";
    public static final String endpoint = "Endpoint";
    public static final String duration = "Duration";
    public static final String units = "Units";
    public static final String result = "Result";
    public DefaultTemplate() {
        super("Default");
        init();
    }
    /**
     * @param name
     */
    public DefaultTemplate(String name) {
        super(name);
        init();
    }

    /**
     * @param initialCapacity
     */
    public DefaultTemplate(int initialCapacity) {
        super(initialCapacity);
        init();
    }

    /**
     * @param c
     */
    public DefaultTemplate(Collection c) {
        super(c);
        init();
    }
    protected void init() {
        addFields(endpoint,"",false,false);
        addFields(duration,"",false,false);
        addFields(species,"",false,false);
        addFields(units,"",false,false);
        addFields(species_sex,"",false,false);
        addFields(species_strain,"",false,false);
        addFields(result,"",false,true);
    }
}
