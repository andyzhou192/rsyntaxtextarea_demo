package org.fife.ui.rsyntaxtextarea.demo;

import java.awt.Toolkit;

import javax.swing.*;


/**
 * Standalone version of the demo.
 *
 * @author Robert Futrell
 * @version 1.0
 */
@SuppressWarnings("serial")
public class RSyntaxTextAreaDemoApp extends JFrame {

	private DemoRootPane rootPane;

	public RSyntaxTextAreaDemoApp() {
		this.rootPane = new DemoRootPane();
		this.setRootPane(rootPane);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("RSyntaxTextArea Demo Application");
		this.pack();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.
											getSystemLookAndFeelClassName());
//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace(); // Never happens
				}
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				new RSyntaxTextAreaDemoApp().setVisible(true);
			}
		});
	}

}