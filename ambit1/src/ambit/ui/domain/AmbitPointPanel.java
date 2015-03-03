/**
 * Created on 2005-3-30
 *
 */
package ambit.ui.domain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import ambit.data.molecule.AmbitPoint;
import ambit.ui.CorePanel;
import ambit.ui.data.AmbitPointTableModel;
import ambit.ui.data.QCompoundPanel;

/**
 * A panel to display {@link ambit.data.molecule.AmbitPoint} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitPointPanel extends CorePanel {
	protected TreeMap labels;
	protected TreeMap edits;	
	protected AmbitPoint ambitPoint;
	protected QCompoundPanel qpanel;
	protected JTable xtable;
	protected AmbitPointTableModel model;
	protected JLabel coverage;
	/**
	 * @param title
	 */
	public AmbitPointPanel(String title) {
		super(title);

	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public AmbitPointPanel(String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);

	}
	protected void initLayout() {
		layout = new BorderLayout();
		setLayout((BorderLayout) layout);
		setBackground(backClr);
		setBorder(BorderFactory.createMatteBorder(5,5,5,5,backClr));
		
   }
	/* (non-Javadoc)
	 * @see ambit.ui.CorePanel#addWidgets()
	 */
	protected void addWidgets() {
		qpanel = new QCompoundPanel("",backClr,foreClr);
		if (ambitPoint != null)
			qpanel.setCompound(ambitPoint.getCompound());
		qpanel.setPreferredSize(new Dimension(200,400));			
		qpanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));		
		add(qpanel,BorderLayout.CENTER);
		
		boolean editable = true;
		//TODO Load strings from resource
		model = new AmbitPointTableModel(ambitPoint);
		xtable = new JTable(model );
		xtable.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
     	xtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     	xtable.setBackground(backClr);
     	xtable.setForeground(foreClr);
		xtable.setOpaque(true);

     	coverage = new JLabel("");
     	coverage.setForeground(foreClr);
     	coverage.setBackground(backClr);
     	coverage.setOpaque(true);
     	coverage.setHorizontalTextPosition(SwingConstants.RIGHT);
     	add(coverage,BorderLayout.NORTH);

     	JScrollPane pane = new JScrollPane(xtable);
     	pane.setAutoscrolls(true);
     	pane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
     	pane.setBackground(backClr);
     	pane.setForeground(foreClr);
     	pane.setOpaque(true);     

     	
     	add(pane,BorderLayout.SOUTH);
		setPreferredSize(new Dimension(300,Integer.MAX_VALUE));
		
		xtable.getParent().setBackground(backClr);		
		
	}

	public AmbitPoint getAmbitPoint() {
		return ambitPoint;
	}
	public void setAmbitPoint(AmbitPoint ambitPoint) {
		this.ambitPoint = ambitPoint;
		if (qpanel != null) {
			qpanel.setCompound(ambitPoint.getCompound());
			qpanel.revalidate();
		}
		model.setObject(ambitPoint);
		
		if (coverage != null)
			coverage.setText(ambitPoint.coverageStatus());

		//yobserved.setText(ambitPoint.getYObserved());
		//ypredicted.setText(ambitPoint.getYPredicted());
		
	}
}