package org.usfirst.frc.team25.Auton;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.*;

import javax.swing.*;

import com.google.gson.Gson;

public class AutonUI extends JFrame{

	private Container pane;
	private JButton addB = new JButton("Add Step");
	private JButton openB = new JButton("Open File");

	private AutonSet temp = new AutonSet();
	
	AutonUI() {
		super("Auton Helper");
		pane = this.getContentPane();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane.setLayout(new BorderLayout());
		
		addB.addActionListener((e) -> temp.add(new AutonMode("First")));
		openB.addActionListener((e) -> getFile());
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 2, 10, 10));
		northPanel.add(addB);
		northPanel.add(openB);
		pane.add(northPanel, BorderLayout.NORTH);
		
		this.setVisible(true);
		//this.revalidate(); // Fix graphics
		//this.repaint();
	}
	/**
	 * Created a file chooser dialog, and parses the selected file
	 */
	public void getFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose an AutonFile");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		while (fileChooser.showDialog(null, "Use") != JFileChooser.APPROVE_OPTION 
				|| fileChooser.getSelectedFile() == null);
		try (Scanner in = new Scanner(new FileInputStream(fileChooser.getSelectedFile()))) {
			StringBuilder bd = new StringBuilder(500); //500 chars seems long enough
			while (in.hasNextLine())
				bd.append(in.nextLine());
			temp = new Gson().fromJson(bd.toString(), AutonSet.class);
		} catch (com.google.gson.JsonSyntaxException jse) {
			System.out.println("Not in the right format. Check for any missing field and commas");
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find auton file");
			temp = new AutonSet();
		}
		
	}
	
	public void menubar() {
		JMenuBar bar = new JMenuBar();
		
		//File actions
		String[] fileItemsNames = new String[] { "New","Open", "Save","Exit" };
		int[] keys= {KeyEvent.VK_N,KeyEvent.VK_O,KeyEvent.VK_S,KeyEvent.VK_W};
		
		JMenuItem[] actions = new JMenuItem[fileItemsNames.length];
		for (int i = 0; i<fileItemsNames.length; i++)
			actions[i] = new JMenuItem(fileItemsNames[i]);
		
		
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.CTRL_MASK);
		for (int i = 0 ;i<keys.length;i++) {
			actions[i].addActionListener((e) -> {
				switch (e.getActionCommand()) {
				case "Open" : 
					getFile();
					break;
				case "New": 
					temp = new AutonSet();
					break;
				case "Save":
					break;
				case "Exit":
					break;
				}
			});
			actions[i].setMnemonic(keys[i]);
			actions[i].setAccelerator(KeyStroke.getKeyStroke(keys[i], KeyEvent.CTRL_MASK));
			file.add(actions[i]);
		}
		bar.add(file);
		
		this.setJMenuBar(bar);
		
		
	}




	public static void main(String[] args) {
		new AutonUI();
	}

}
