/*
 * Listeners pertaining to receipt printing
 */

package com.diy.software.pay;

import com.diy.software.SoftwareController;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterD;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;

public class ReceiptListener implements ReceiptPrinterListener {
	
	/*
	 * IMPORTANT NOTE: paper and ink must be added and something must be printed before anything anything relating to the
	 * receipt can be accurately registered due to restrictions of the hardware. The reason why is because in order for 
	 * the system to be able to register that the printer has no/low paper or ink print must first be called to register 
	 * there is no/low ink/paper, which can only be done without error if there is paper or ink in the system.
	 */
	
	//used to make sure that the printer has paper and ink before enabling (this must be ensured manually by adding ink/paper)
	Boolean isOutOfPaper = false;
	Boolean isOutOfInk = false;

    // Used for testing
    Boolean isLowOnPaper = false;
    Boolean isLowOnInk = false;

    public Boolean getIsOutOfPaper(){
        return this.isOutOfPaper;
    }

    public Boolean getIsOutOfInk(){
        return this.isOutOfInk;
    }

    public Boolean getIsLowOnPaper(){
        return this.isLowOnPaper;
    }

    public Boolean getIsLowOnInk(){
        return this.isLowOnInk;
    }


    // When out of paper, put a hold on the station, notify the attendant and the customer
    @Override
    public void outOfPaper(IReceiptPrinter printer) {
    	isOutOfPaper = true;
    	SoftwareController.hold();
        SoftwareController.notifyAttendantMessage("The receipt printer is out of paper, more must be added for the station to continue running");
        SoftwareController.notifyMessage("The receipt printer has ran out of paper, wait until more is added to resume using the station");
        
        //TODO: This is a placeholder until attendant station is implemented in iteration 3
        SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: The receipt printer is out of paper, more must be added for the station to continue running");
    }

    // When out of in, put a hold on the station, notify the attendant and the customer
    @Override
    public void outOfInk(IReceiptPrinter printer) {
    	isOutOfInk = true;
    	SoftwareController.hold();
        SoftwareController.notifyAttendantMessage("The receipt printer is out of ink, more must be added for the station to continue running");
        SoftwareController.notifyMessage("The receipt printer has ran out of paper, wait until more is added to resume using the station");
        
        //TODO: This is a placeholder until attendant station is implemented in iteration 3
        SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: The receipt printer is out of ink, more must be added for the station to continue running");
    }

    // When low on paper, notify the attendant
    @Override
    public void lowPaper(IReceiptPrinter printer) {

        isLowOnPaper = true;
        SoftwareController.notifyAttendantMessage("The receipt printer is low on paper");
        
        //TODO: This is a placeholder until attendant station is implemented in iteration 3
        SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: The receipt printer is low on paper");
    }

    // When low on ink, notify the attendant
    @Override
    public void lowInk(IReceiptPrinter printer) {
        isLowOnInk = true;
        SoftwareController.notifyAttendantMessage("The receipt printer is low on ink");
        
        //TODO: This is a placeholder until attendant station is implemented in iteration 3
        SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: The receipt printer is low on ink");
    }

    // When paper is added, if still out of ink, notify attendant.
    // Else unhold the station and notify attendant
    @Override
    public void paperAdded(IReceiptPrinter printer) {
        isLowOnPaper = false;
        isOutOfPaper = false;
        if(isOutOfInk == true) {
        	SoftwareController.notifyAttendantMessage("Paper has successfully been added to the receipt printer, but ink is still needed for the station to be able to print again");
        	
            //TODO: This is a placeholder until attendant station is implemented in iteration 3
            SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: Paper has successfully been added to the receipt printer, but ink is still needed for the station to be able to print again");
        }
        else{
            SoftwareController.unHold();
            SoftwareController.notifyAttendantMessage("Paper has successfully been added to the receipt printer, the station can now print again");
            SoftwareController.notifyMessage("Use of the station has resumed, receipts can now be printed again");
            
            //TODO: This is a placeholder until attendant station is implemented in iteration 3
            SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: Paper has successfully been added to the receipt printer, the station can now print again");
        }
    }

    // When ink is added, if still out of paper, notify attendant.
    // Else unhold the station and notify attendant
    @Override
    public void inkAdded(IReceiptPrinter printer) {
        isLowOnInk = false;
        isOutOfInk = false;
        if(isOutOfPaper == true) {
        	SoftwareController.notifyAttendantMessage("Ink has successfully been added to the receipt printer, but paper is still needed for the station to be able to print again");
        	
            //TODO: This is a placeholder until attendant station is implemented in iteration 3
            SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: Ink has successfully been added to the receipt printer, but paper is still needed for the station to be able to print again");
        }
        else{
            SoftwareController.unHold();
            SoftwareController.notifyAttendantMessage("Ink has successfully been added to the receipt printer, the station can now print again");
            SoftwareController.notifyMessage("Use of the station has resumed, receipts can now be printed again");
            
            //TODO: This is a placeholder until attendant station is implemented in iteration 3
            SoftwareController.notifyMessage("PLACEHOLDER UNTIL ATTENDANT STATION IS ADDED IN ITERATION 3: Ink has successfully been added to the receipt printer, the station can now print again");
        }
    }
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {}
}
