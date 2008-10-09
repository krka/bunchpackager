/*
    Copyright (c) 2005, 2008 Kristofer Karlsson

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package se.kristofer.karlsson.util.bunchpackager.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import se.kristofer.karlsson.util.bunchpackager.backend.BunchPackager;

public class BunchPackagerGUI implements ActionListener, Runnable, WindowListener {
	private FileTree inputTree;
    private FileTree outputTree;
    private JButton runButton;
    private Thread runningThread;
    private CountDown countdownObject;
    private JProgressBar progressbar;
    
    private JTextField runtimeField;
    private JTextField maxWidthField;
    private JTextField maxHeightField;
    private JTextField percentageField;
    private JCheckBox forcedupdateField;
    private String userHome;
    private String userSettings;

	private JTextArea runOutput;
	private BunchPackager bp;
    
    public static void main(String[] args) {
        if (args.length > 0) {
            BunchPackager.main(args);
        } else {
        	BunchPackager.printHelp();
            new BunchPackagerGUI();
        }
	}
	
	public BunchPackagerGUI() {
	    JFrame frame = new JFrame("BunchPackager GUI");
	    
	    Container cp = frame.getContentPane();
	    cp.setLayout(new BorderLayout());

	    cp.add(topFrame(), BorderLayout.CENTER);
	    
	    JPanel bottom = new JPanel();
	    bottom.setLayout(new BorderLayout());
	    bottom.add(centerFrame(), BorderLayout.NORTH);
	    bottom.add(bottomFrame(), BorderLayout.CENTER);
	    
	    cp.add(bottom, BorderLayout.SOUTH);

	    frame.addWindowListener(this);
	    
	    frame.pack();
	    frame.setSize(800, 600);
        frame.setVisible(true);
        
	    userHome = System.getProperty("user.home") + File.separator;
	    userSettings = userHome + ".bunchpackager.txt";
	    
	    try {
            Scanner scan = new Scanner(new File(userSettings));
            while (scan.hasNext()) {
                String key = scan.next();
                
                scan.skip(Pattern.compile("[ \\t]+"));
                String value = scan.nextLine();
                
                if (key.equals("inputdir")) {
                    inputTree.setCurrent(value);
                }
                if (key.equals("outputdir")) {
                    outputTree.setCurrent(value);
                }
                if (key.equals("runtime")) {
                    runtimeField.setText(value);
                }
                if (key.equals("maxwidth")) {
                    maxWidthField.setText(value);
                }
                if (key.equals("maxheight")) {
                    maxHeightField.setText(value);
                }
                if (key.equals("percentage")) {
                    percentageField.setText(value);
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
        }
        
	}

    private Component topFrame() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1, 2));
        
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.add(new JLabel("Input directory"), BorderLayout.NORTH);
        inputTree = new FileTree();
	    left.add(inputTree, BorderLayout.CENTER);
	    
        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        right.add(new JLabel("Output directory"), BorderLayout.NORTH);
        outputTree = new FileTree();
	    right.add(outputTree, BorderLayout.CENTER);
	    
	    p.add(left);
	    p.add(right);
	    
	    return p;
    }

    private Component bottomFrame() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        
        runOutput = new JTextArea(10, 80);
        
        JScrollPane scrollPane = new JScrollPane(runOutput);
        
        left.add(new JLabel("Run status:"), BorderLayout.NORTH);
        left.add(scrollPane, BorderLayout.CENTER);
        
        p.add(left, BorderLayout.CENTER);
        
        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        
        right.add(progressbar = new JProgressBar(0, 1), BorderLayout.CENTER);
        progressbar.setStringPainted(true);
        progressbar.setBorderPainted(true);
        progressbar.setIndeterminate(false);
        progressbar.setOrientation(JProgressBar.HORIZONTAL);
        progressbar.setVisible(false);
                
        right.add(runButton = new JButton("Run"), BorderLayout.EAST);
        runButton.addActionListener(this);
        
        p.add(right, BorderLayout.SOUTH);
        
        return p;
    }

    private Component centerFrame() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        
        JPanel rightPane = new JPanel();
        rightPane.setLayout(new GridLayout(5, 2));
        
        rightPane.add(new JLabel("Runtime per bunch (s):"));
        rightPane.add(runtimeField = new JTextField("5"));
        
        rightPane.add(new JLabel("Max width (px):"));
        rightPane.add(maxWidthField = new JTextField(""));

        rightPane.add(new JLabel("Max height (px):"));
        rightPane.add(maxHeightField = new JTextField(""));

        rightPane.add(new JLabel("Good enough percentage (%):"));
        rightPane.add(percentageField = new JTextField(""));

        rightPane.add(new JLabel("Force update:"));
        rightPane.add(forcedupdateField = new JCheckBox());
        
        p.add(rightPane, BorderLayout.EAST);
        
        return p;
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == runButton) {
            if (runningThread == null) {
	            runningThread = new Thread(this);
	            runningThread.start();
            } else {
                stopThread();
            }
        }
    }

    public void run() {
        int runtime = 0;
        try {
            runtime = Integer.parseInt(runtimeField.getText());
        } catch (NumberFormatException e) {
            runOutput.append("Runtime error: " + e.getMessage() + "\n");
            return;
        }
        
        if (countdownObject == null) {
        	countdownObject = new CountDown(progressbar);
        }
        countdownObject.setRuntime(runtime);
        PrintWriterRedirect pwr = PrintWriterRedirect.create(runOutput, countdownObject);

        try {

            runButton.setText("Stop");
            runOutput.setText("");
            bp = new BunchPackager();
	        bp.setForcedupdate(forcedupdateField.isSelected());
	        bp.setInputdir(inputTree.getCurrent());
	        bp.setOutputdir(outputTree.getCurrent());
	        
	        try {
	            bp.setMaxwidth(Integer.parseInt(maxWidthField.getText()));
	        } catch (NumberFormatException nfe) {
	        }
	        try {
		        bp.setMaxheight(Integer.parseInt(maxHeightField.getText()));
	        } catch (NumberFormatException nfe) {
	        }
	        try {
		        bp.setPercentage(Double.parseDouble(percentageField.getText()));
	        } catch (NumberFormatException nfe) {
	        }
	        bp.setPrinter(pwr);
	        bp.setRuntime(runtime);

	        bp.execute();
        } catch (Exception e) {
            runOutput.append("Runtime error: " + e.getMessage() + "\n");
            e.printStackTrace(pwr);
        }

        runButton.setText("Run");
        runningThread = null;

        countdownObject.stop();
    }
    
    public void stopThread() {
    	if (bp != null) {
    		
    		new Thread(new Runnable() {

				public void run() {
					runButton.setEnabled(false);
					bp.waitForStop();
					runButton.setEnabled(true);
					
				}}).start();
    	}
    }

    public void windowOpened(WindowEvent arg0) {
    }

    public void windowClosing(WindowEvent arg0) {
    	stopThread();
        try {
            PrintWriter pw = new PrintWriter(new File(userSettings));
            pw.println("inputdir " + inputTree.getCurrent().getAbsolutePath());
            pw.println("outputdir " + outputTree.getCurrent().getAbsolutePath());
            pw.println("runtime " + runtimeField.getText());
            pw.println("maxwidth " + maxWidthField.getText());
            pw.println("maxheight " + maxHeightField.getText());
            pw.println("percentage " + percentageField.getText());
            pw.close();
        } catch (FileNotFoundException e) {
        }
        arg0.getWindow().dispose();
        System.out.println("Done");
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowDeactivated(WindowEvent arg0) {
    }
}
