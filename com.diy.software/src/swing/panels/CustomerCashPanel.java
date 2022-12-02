package swing.panels;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.CashControl;
import com.diy.software.listeners.CashControlListener;

public class CustomerCashPanel extends JPanel implements CashControlListener {
  private static final long serialVersionUID = 1L;
  private CashControl cc;
  private JButton dollar100, dollar50, dollar20, dollar10, dollar5, dollar1;
  private JButton toonie, loonie, quarter, dime, nickel, penny;

  private boolean noteEnabled = false;
  private boolean coinEnabled = false;

  public CustomerCashPanel(StationControl sc) {
    super();
    cc = sc.getCashControl();
    cc.addListener(this);

    dollar100 = new JButton("$100");
    dollar50 = new JButton("$50");
    dollar20 = new JButton("$20");
    dollar10 = new JButton("$10");
    dollar5 = new JButton("$5");
    dollar1 = new JButton("$1");

    toonie = new JButton("$2");
    loonie = new JButton("$1");
    quarter = new JButton("$0.25");
    dime = new JButton("$0.10");
    nickel = new JButton("$0.05");
    penny = new JButton("$0.01");

    dollar100.setActionCommand("d 100");
    dollar50.setActionCommand("d 50");
    dollar20.setActionCommand("d 20");
    dollar10.setActionCommand("d 10");
    dollar5.setActionCommand("d 5");
    dollar1.setActionCommand("d 1");
    
    toonie.setActionCommand("c 200");
    loonie.setActionCommand("c 100");
    quarter.setActionCommand("c 25");
    dime.setActionCommand("c 10");
    nickel.setActionCommand("c 5");
    penny.setActionCommand("c 1");

    dollar100.addActionListener(cc);
    dollar50.addActionListener(cc);
    dollar20.addActionListener(cc);
    dollar10.addActionListener(cc);
    dollar5.addActionListener(cc);
    dollar1.addActionListener(cc);

    toonie.addActionListener(cc);
    loonie.addActionListener(cc);
    quarter.addActionListener(cc);
    dime.addActionListener(cc);
    nickel.addActionListener(cc);
    penny.addActionListener(cc);

    this.add(dollar100);
    this.add(dollar50);
    this.add(dollar20);
    this.add(dollar10);
    this.add(dollar5);
    this.add(dollar1);

    this.add(toonie);
    this.add(loonie);
    this.add(quarter);
    this.add(dime);
    this.add(nickel);
    this.add(penny);

    dollar100.setEnabled(false);
    dollar50.setEnabled(false);
    dollar20.setEnabled(false);
    dollar10.setEnabled(false);
    dollar5.setEnabled(false);
    dollar1.setEnabled(false);

    toonie.setEnabled(false);
    loonie.setEnabled(false);
    quarter.setEnabled(false);
    dime.setEnabled(false);
    nickel.setEnabled(false);
    penny.setEnabled(false);
  }

  private void updateButtonStates() {
    dollar100.setEnabled(noteEnabled);
    dollar50.setEnabled(noteEnabled);
    dollar20.setEnabled(noteEnabled);
    dollar10.setEnabled(noteEnabled);
    dollar5.setEnabled(noteEnabled);
    dollar1.setEnabled(noteEnabled);

    toonie.setEnabled(coinEnabled);
    loonie.setEnabled(coinEnabled);
    quarter.setEnabled(coinEnabled);
    dime.setEnabled(coinEnabled);
    nickel.setEnabled(coinEnabled);
    penny.setEnabled(coinEnabled);
  }

  @Override
  public void cashInserted(CashControl cc) {}

  @Override
  public void coinInsertionEnabled(CashControl cc) {
    coinEnabled = true;
    updateButtonStates();
  }

  @Override
  public void noteInsertionEnabled(CashControl cc) {
	noteEnabled = true;
	updateButtonStates();
  }

  @Override
  public void coinInsertionDisabled(CashControl cc) {
	coinEnabled = false;
	updateButtonStates();
  }

  @Override
  public void noteInsertionDisabled(CashControl cc) {
	noteEnabled = false;
	updateButtonStates();
  }

  @Override
  public void checkCashRejected(CashControl cc) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void changeReturned(CashControl cc) {
	// TODO Auto-generated method stub
	
  }
  
  @Override
  public void paymentFailed(CashControl cc) {
	// TODO Auto-generated method stub
	  
  }

}
