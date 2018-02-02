package edu.ucla.astro.irlab.util;
//. NOTE: This class is for testing tail, and is not included in Makefile
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TailTester implements TailListener{
	String currentReadPath = "/u/mosdev/kroot/kss/mosfire/gui/temp/";
	JButton openButton = new JButton("Open");
	Tail tail = new Tail();
	public TailTester() {
		tail.addTailListener(this);
		JFrame frame = new JFrame();
		JPanel panel = (JPanel) frame.getContentPane();
		panel.add(openButton);
		
		openButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				openButton_actionPerformed();
			}
			
		});
		
		frame.pack();
		frame.setVisible(true);
	}
	
	protected void openButton_actionPerformed() {
    //. launch dialog for opening file
    JFileChooser fc = new JFileChooser(currentReadPath);
    fc.setDialogTitle("Open Log File");
    //. filter on *.DDF
//    fc.setFileFilter(new OsirisFileFilters.DDFFileFilter());
    //. initialize with last opened file
    //. open dialog
    int retVal = fc.showOpenDialog(openButton);
    //. if ok was hit, open file
    if (retVal == JFileChooser.APPROVE_OPTION) {
      java.io.File file = fc.getSelectedFile();
      //. save path
      currentReadPath = file.getAbsolutePath();
      try {
      	//. try to open
      	tail.startReading(file.getAbsolutePath());
      	//openLogFile(file.getAbsolutePath());
      } catch (FileNotFoundException fnfEx) {
      	fnfEx.printStackTrace();
      }
    }				
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TailTester();
	}
	public void receiveNewData(byte[] data) {
		System.out.println(new String(data));
	}

}
