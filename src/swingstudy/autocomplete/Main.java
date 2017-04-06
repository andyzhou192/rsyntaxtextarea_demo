package swingstudy.autocomplete;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import swingstudy.autocomplete.JTextAutoCompletePack.CommitListener;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		JTextArea textArea = new JTextArea();
		JTextField textField = new JTextField();
		//   Create an object and binds a  JTextComponent
		JTextAutoCompletePack textAutoCompletePack = new JTextAutoCompletePack(textField);
		//   Set the parameters
		textAutoCompletePack.setMatchDataAsync(true);
		textAutoCompletePack.setMaxVisibleRows(6);
		textAutoCompletePack.setMatchData(new String[]{"a", "b", "c", "d"});
		textAutoCompletePack.setCommitListener(new CommitListener() {
		    //   Value was chosen to trigger the function when
		    public void commit(String value) {
		        System.out.println("commit:" + value);
		    }
		});
		frame.add(textField, BorderLayout.NORTH);
		frame.add(textArea, BorderLayout.CENTER);
		frame.setVisible(true);

	}

}
