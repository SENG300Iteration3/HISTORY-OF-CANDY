package swing.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.SystemControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;

public class AttendantPanel extends JPanel implements AttendantControlListener, BagsControlListener{

	private static final long serialVersionUID = 1L;
	private SystemControl sc;
	private BagsControl bc;
	private AttendantControl ac;
	private boolean cusAddedBags = false;
	private double weightToDisplay = 0.0;
	JButton approveAddedBagsButton;
	JButton addInkToPrinterButton;
	JButton addPaperToPrinterButton;
	JButton approveNoBagging;
	JLabel weightDisplayLabel, weightDescrepancyMssg;
	GridBagConstraints grid = new GridBagConstraints();
	
	public AttendantPanel(SystemControl sc) {
		super();
		this.sc = sc;
		bc = sc.getBagsControl();
		bc.addListener(this);
		
		ac =  sc.getAttendantControl();
		ac.addListener(this);
				
		approveAddedBagsButton = new JButton("approvedAddedBags(()");
		approveAddedBagsButton.setActionCommand("approve added bags");
		approveAddedBagsButton.addActionListener(ac);
		
		addInkToPrinterButton = new JButton("Add ink");
		addInkToPrinterButton.setActionCommand("addInk");
		addInkToPrinterButton.addActionListener(ac);
		
		addPaperToPrinterButton = new JButton("Add paper");
		addPaperToPrinterButton.setActionCommand("addPaper");
		addPaperToPrinterButton.addActionListener(ac);
		
		approveNoBagging = new JButton("Approve no bagging");
		approveNoBagging.setActionCommand("No bagging");
		approveNoBagging.addActionListener(ac);
		
		
		weightDisplayLabel = new JLabel("");
		weightDescrepancyMssg = new JLabel();
    	
		grid.gridx = 0;
		grid.gridy = 0;
		this.add(weightDisplayLabel);
		grid.gridy = 1;
    	this.add(approveAddedBagsButton);
    	grid.gridy = 2;
    	this.add(addInkToPrinterButton);
    	grid.gridy = 3;
    	this.add(addPaperToPrinterButton);
    	grid.gridy = 4;
    	this.add(approveNoBagging);
    	
    	this.add(weightDescrepancyMssg);
    	
    	addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);
		approveNoBagging.setEnabled(false);
		
    	approveAddedBagsButton.setEnabled(cusAddedBags);
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		
		approveAddedBagsButton.setEnabled(true);
		weightDisplayLabel.setText("Weight on station scale: " + sc.getWeightOfLastItemAddedToBaggingArea() + "g");
	}
	
	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		
		approveAddedBagsButton.setEnabled(false);
		weightDisplayLabel.setText("");
	}
	
	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		
		approveAddedBagsButton.setEnabled(false);
		weightDisplayLabel.setText("");
	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addPaperState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(true);
	}

	@Override
	public void addInkState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(true);
		addPaperToPrinterButton.setEnabled(false);	
	}

	@Override
	public void printerNotLowState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		weightDescrepancyMssg.setText(updateMessage);
		
	}

	@Override
	public void noBaggingRequestState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(true);
	}

	@Override
	public void initialState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(false);
		weightDescrepancyMssg.setText("");
	}
}
