package swingstudy.autocomplete;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;

/**
 * Firefox search bar modeled on the rules of implementation
 * 
 * @author Univasity
 */
public class JTextAutoCompletePack {

	public static final int DefaultMaxVisibleRows = 5;
	/**
	 * Binding of the text component
	 */
	private JTextComponent textComponent;
	/**
	 * Results list to display the pop-up menu component
	 */
	private JPopupMenu popupMenu;
	/**
	 * Used to display a list of components matching results
	 */
	private JList resultList;
	/**
	 * To provide rolling support for the list of components
	 */
	private JScrollPane scrollPane;
	/**
	 * The data used for matching
	 */
	private ArrayList matchData;
	/**
	 * Tag matches the data is changed
	 */
	private boolean matchDataChanged;
	/**
	 * Record the current text that was matched
	 */
	private String matchedText;
	/**
	 * Edit the original text
	 */
	private String originalEditText;
	/**
	 * Help determine whether the matching components
	 */
	private DataMatchHelper dataMatchHelper;
	/**
	 * Determine the listener, the default for the press ' Enter 'or click on
	 * the mouse will be triggered
	 */
	private CommitListener commitListener;
	/**
	 * Thread pool
	 */
	private BlockingQueue<Runnable> queue; // The task queue used to store
	private ThreadPoolExecutor executor; // Thread pool object
	private boolean matchDataAsync = false; // Matching operation is
											// asynchronous

	/**
	 * Bound given by the text component to construct an object
	 * 
	 * @param textComponent
	 */
	public JTextAutoCompletePack(JTextComponent textComponent) {
		/**
		 * To ensure that the plug is not bound null
		 */
		if (textComponent == null) {
			throw new IllegalArgumentException("  Parameter can not be  null!");
		}
		this.textComponent = textComponent;
		resetAll();
	}

	/**
	 * Set as the default configuration, the original data will be cleared
	 */
	public synchronized void resetAll() {
		initTextComponent();
		initResultList();
		initValues();
		setFocusOnTextComponent();
		updateUI();
	}

	public synchronized void updateUI() {
		popupMenu.pack();
		popupMenu.updateUI();
	}

	/**
	 * Specifies the value to match the data set
	 * 
	 * @param data
	 */
	public synchronized void setMatchData(Object[] data) {
		clearMatchData();
		if (data != null) {
			for (Object value : data) {
				this.matchData.add(value);
			}
		}
		notifyDataChanged();
	}

	/**
	 * Setting specifies the value to match the data
	 * 
	 * @param data
	 */
	public synchronized void setMatchData(Vector data) {
		clearMatchData();
		if (data != null) {
			for (Object value : data) {
				this.matchData.add(value);
			}
		}
		notifyDataChanged();
	}

	/**
	 * Add the specified value to match the data
	 * 
	 * @param value
	 */
	public synchronized void addMatchData(Object value) {
		if (value != null) {
			this.matchData.add(value);
		}
		notifyDataChanged();
	}

	/**
	 * Remove the specified index match the data in the data ( If it exists )
	 * 
	 * @param index
	 */
	public synchronized void removeMatchData(int index) {
		if (index < 0 || index >= matchData.size()) {
			return;
		}
		matchData.remove(index);
	}

	/**
	 * Remove the specified data matches the data ( If it exists )
	 * 
	 * @param obj
	 */
	public synchronized void removeMatchData(Object obj) {
		if (obj != null) {
			matchData.remove(obj);
		}
	}

	/**
	 * Remove the specified index match the data in the data set ( If it exists
	 * )
	 * 
	 * @param indices
	 */
	public synchronized void removeMatchData(int[] indices) {
		if (indices == null) {
			return;
		}
		for (int index : indices) {
			removeMatchData(index);
		}
	}

	/**
	 * Match the data in the specified group removed the data ( If it exists )
	 * 
	 * @param data
	 */
	public synchronized void removeMatchData(Object[] data) {
		if (data == null) {
			return;
		}
		for (Object obj : data) {
			removeMatchData(obj);
		}
	}

	/**
	 * Clear match data
	 */
	public synchronized void clearMatchData() {
		matchData.clear();
	}

	/**
	 * Get the current match data
	 * 
	 * @return
	 */
	public synchronized Object[] getMatchData() {
		return matchData.toArray();
	}

	public synchronized void clearMatchResult() {
		collapseResultList();
		if (queue != null) {
			queue.clear();
		}
		((DefaultListModel) resultList.getModel()).removeAllElements();
	}

	/**
	 * Change the tag matches the data
	 */
	private void notifyDataChanged() {
		matchDataChanged = true;
	}

	public void setCommitListener(CommitListener commitListener) {
		this.commitListener = commitListener;
	}

	public void setDataMatchHelper(DataMatchHelper dataMatchHelper) {
		if (dataMatchHelper != null) {
			this.dataMatchHelper = dataMatchHelper;
		} else {
			this.dataMatchHelper = new DefaultDataMatchHelper();
		}
	}

	/**
	 * Get the current text that was matched
	 * 
	 * @return
	 */
	public synchronized String getMatchText() {
		return matchedText;
	}

	/**
	 * Get the current match result
	 * 
	 * @return
	 */
	public synchronized Object[] getMatchResult() {
		return ((DefaultListModel) resultList.getModel()).toArray();
	}

	/**
	 * Get the current value of the selected
	 * 
	 * @return
	 */
	public synchronized Object getSelectedValue() {
		return resultList.getSelectedValue();
	}

	/**
	 * Determine the final selection of the specified text
	 * 
	 * @param text
	 */
	public synchronized void commitText(String text) {
		originalEditText = text;
		textComponent.setText(text);
		if (commitListener != null) {
			commitListener.commit(text);
		}
	}

	/**
	 * Get the index of the currently selected item
	 * 
	 * @return
	 */
	public synchronized int getSelectedIndex() {
		return resultList.getSelectedIndex();
	}

	/**
	 * Select the index value specified
	 * 
	 * @param index
	 */
	public synchronized void setSelectedIndex(int index) {
		if (index < 0 || index >= getResultListSize()) {
			return;
		}
		resultList.setSelectedIndex(index);
		// The selected item in the visible range
		resultList.ensureIndexIsVisible(index);
	}

	/**
	 * Open the list of results ( If minors match, the automated matching
	 * processing , If there is no effective results will not be launched )( The
	 * focus will shift to the list )
	 * 
	 * @return
	 */
	public synchronized boolean expandResultList() {
		if (!hasMatched()) {
			if (doMatch()) {
				// Expand List
				updateResultListUI();
				popupMenu.show(textComponent, 0, textComponent.getHeight());
			}
		} else if (getResultListSize() > 0) {
			popupMenu.setVisible(true);
		}
		return popupMenu.isVisible();
	}

	/**
	 * Off the list of results ( Data will not be clear, direct re-opened again
	 * displayed )
	 */
	public synchronized void collapseResultList() {
		removeSelectionInterval();
		popupMenu.setVisible(false);
	}

	/**
	 * Be opened to determine whether the results list
	 * 
	 * @return
	 */
	public synchronized boolean isResultListExpanded() {
		return popupMenu.isVisible();
	}

	/**
	 * Get the current number of entries in the list of results
	 * 
	 * @return
	 */
	public synchronized int getResultListSize() {
		return ((DefaultListModel) resultList.getModel()).getSize();
	}

	/**
	 * For the display of a maximum number of rows ( The surplus to be displayed
	 * by dragging the scroll bar )
	 * 
	 * @param rows
	 */
	public synchronized void setMaxVisibleRows(int rows) {
		resultList.setVisibleRowCount(rows);
	}

	/**
	 * To set the focus to the text editor box
	 */
	public synchronized void setFocusOnTextComponent() {
		textComponent.requestFocus();
	}

	/**
	 * To set the focus to results list
	 */
	public synchronized void setFocusOnResultList() {
		resultList.requestFocus();
	}

	/**
	 * Determine whether the focus on the text edit box
	 * 
	 * @return
	 */
	public synchronized boolean isFocusOnTextComponent() {
		return textComponent.isFocusOwner();
	}

	/**
	 * Determine whether the focus on the results list
	 * 
	 * @return
	 */
	public synchronized boolean isFocusOnResultList() {
		return resultList.isFocusOwner();
	}

	/**
	 * Cancel the current list of selected ( So selectedIndex==-1)
	 */
	public synchronized void removeSelectionInterval() {
		final int selectedIndex = resultList.getSelectedIndex();
		resultList.removeSelectionInterval(selectedIndex, selectedIndex);
	}

	/**
	 * After a match to determine whether ( Testing should be conducted before
	 * match, match operation to avoid duplication )
	 * 
	 * @return
	 */
	public synchronized boolean hasMatched() {
		if (matchDataChanged) {
			return false;
		}
		if (matchedText == null || matchedText.length() < 1) {
			return false;
		}
		String text = textComponent.getText();
		if (text == null || !text.equals(matchedText)) {
			return false;
		}
		return true;
	}

	/**
	 * Perform the matching operation
	 * 
	 * @return
	 */
	public synchronized boolean doMatch() {
		// Clear the original results
		clearMatchResult();

		matchedText = textComponent.getText();
		originalEditText = matchedText;
		String keyWord = matchedText;
		if (keyWord != null) {
			keyWord = matchedText.trim();
		}

		if (dataMatchHelper != null) {
			if (!dataMatchHelper.isMatchTextAccept(keyWord)) {
				return false;
			}
		}

		if (matchDataAsync) {
			doMatchAsync(keyWord);
			matchDataChanged = false;
			return true;
		} else {
			doMatchSync(keyWord);
			matchDataChanged = false;
			return getResultListSize() > 0;
		}
	}

	/**
	 * Asynchronous data set matches
	 * 
	 * @param async
	 */
	public synchronized void setMatchDataAsync(boolean async) {
		if (this.matchDataAsync != async) {
			this.matchDataAsync = async;
			if (async) {
				queue = new LinkedBlockingQueue<Runnable>();
				// Create a maximum of two tasks running , Support the 10 tasks
				// , Delay of 20 seconds to allow the thread pool
				executor = new ThreadPoolExecutor(2, 10, 20, TimeUnit.SECONDS, queue);
			} else {
				if (queue != null) {
					queue.clear();
				}
				if (executor != null) {
					executor.shutdown();
				}
				queue = null;
				executor = null;
			}
		}
	}

	/**
	 * Determine whether the asynchronous match the current
	 * 
	 * @return
	 */
	public synchronized boolean isMatchDataAsync() {
		return this.matchDataAsync;
	}

	/**
	 * In the results list select the item displayed on the prompt bar too
	 * 
	 * @param asNeed
	 *            Whether the need to display (true-> When the text is longer
	 *            than the display range display )
	 */
	public synchronized void showToolTipOnResultListBySelectedValue(boolean asNeed) {
		Object value = resultList.getSelectedValue();
		if (value != null) {
			// Show tips
			String txt = value.toString();
			if (txt != null) {
				if (asNeed) {
					// Out of range before displaying the prompt
					int txtW = SwingUtilities.computeStringWidth(resultList.getFontMetrics(resultList.getFont()), txt);
					if (txtW >= resultList.getFixedCellWidth()) {
						resultList.setToolTipText(txt);
						return;
					}
				} else {
					resultList.setToolTipText(txt);
					return;
				}
			}
		}
		resultList.setToolTipText(null);
	}

	/**
	 * In the results list displays the specified text as a prompt
	 * 
	 * @param text
	 */
	public void showToolTipOnResultListBy(String text) {
		if (text != null) {
			resultList.setToolTipText(text);
		} else {
			resultList.setToolTipText(null);
		}
	}

	/**
	 * To obtain a maximum number of rows visible
	 * 
	 * @return
	 */
	public synchronized int getMaxVisibleRows() {
		return resultList.getVisibleRowCount();
	}

	/**
	 * Get the width of the result list item elements
	 * 
	 * @return
	 */
	public synchronized int getResultListCellWidth() {
		return resultList.getFixedCellWidth();
	}

	/**
	 * Unit for the height of the result list
	 * 
	 * @return
	 */
	public synchronized int getResultListCellHeight() {
		return resultList.getFixedCellHeight();
	}

	/**
	 * Whether the specified point within the text box
	 * 
	 * @param p
	 * @return
	 */
	public synchronized boolean isTextFieldContains(Point p) {
		if (p == null) {
			return false;
		}
		return textComponent.contains(p);
	}

	/**
	 * Whether the specified point range in the results list
	 * 
	 * @param p
	 * @return
	 */
	public synchronized boolean isResultListContains(Point p) {
		if (p == null) {
			return false;
		}
		return resultList.contains(p);
	}

	private synchronized void initTextComponent() {
		textComponent.setVisible(true);
		textComponent.setEnabled(true);
		textComponent.setEditable(true);
		// Must remove and then add, otherwise repeat ....
		textComponent.removeKeyListener(DefaultTextFieldKeyAdapter);
		textComponent.addKeyListener(DefaultTextFieldKeyAdapter);
	}

	private synchronized void initResultList() {
		/**
		 * list
		 */
		if (resultList != null) {
			resultList.removeAll();
		} else {
			resultList = new JList(new DefaultListModel());
			resultList.addMouseListener(DefaultResultListMouseAdapter);
			resultList.addMouseMotionListener(DefaultResultListMouseMotionAdapter);
		}
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultList.setVisibleRowCount(DefaultMaxVisibleRows);
		// Allow the prompt box
		ToolTipManager.sharedInstance().registerComponent(resultList);

		/**
		 * scroll pane
		 */
		if (scrollPane == null) {
			scrollPane = new JScrollPane(resultList);
		}
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		/**
		 * popup menu
		 */
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
		}
		popupMenu.add(scrollPane);
		popupMenu.setVisible(false);
		popupMenu.setFocusable(false);
		popupMenu.setBorder(BorderFactory.createEmptyBorder()); // Remove border
	}

	private synchronized void initValues() {
		/**
		 * Match Data
		 */
		if (matchData != null) {
			matchData.clear();
		} else {
			matchData = new ArrayList();
		}

		/**
		 * Other
		 */
		setDataMatchHelper(null);
		setCommitListener(null);

		System.gc();

		matchedText = null;
		matchDataChanged = true;
		this.matchDataAsync = false;
		originalEditText = textComponent.getText();
	}

	/**
	 * The implementation of the given value match operation ( The operation is
	 * asynchronous )
	 * 
	 * @param content
	 * @return
	 */
	private synchronized void doMatchAsync(String content) {
		final String matchText = content;

		if (queue != null) {
			queue.clear();
		}

		executor.execute(new Runnable() {

			public void run() {
				/**
				 * Match
				 */
				doMatchInner(matchText);
				/**
				 * If no match, close the currently displayed
				 */
				if (getResultListSize() > 0) {
					updateResultListUI();
				} else {
					collapseResultList();
				}
			}
		});
	}

	/**
	 * The implementation of the given value match operation ( This operation is
	 * synchronized )
	 * 
	 * @param content
	 * @return
	 */
	private synchronized void doMatchSync(String content) {
		/**
		 * Match
		 */
		doMatchInner(content);
	}

	/**
	 * Matching Treatment ( Internal call )
	 * 
	 * @param matchText
	 */
	private void doMatchInner(String matchText) {
		if (matchData != null) {
			DefaultListModel listModel = (DefaultListModel) resultList.getModel();
			for (Object value : matchData) {
				if (dataMatchHelper != null) {
					if (dataMatchHelper.isDataMatched(matchText, value)) {
						listModel.addElement(value);
					}
				} else {
					// Added directly
					listModel.addElement(value);
				}
			}
		}
	}

	/**
	 * Set the current options for the final selected value
	 */
	private void commitTextBySelectedValue() {
		Object value = getSelectedValue();
		if (value != null) {
			commitText(value.toString());
		}
		collapseResultList();
	}

	/**
	 * To shift the focus to the text edit box and close the list of results
	 */
	private void changeFocusToTextField() {
		// Deselect
		removeSelectionInterval();
		// To shift the focus to the text box
		setFocusOnTextComponent();
		// Set the text value of the original edit
		textComponent.setText(originalEditText);
	}

	/**
	 * Set the current value of the selected item into the text box
	 */
	private void showCurrentSelectedValue() {
		Object value = getSelectedValue();
		if (value != null) {
			textComponent.setText(value.toString());
		}
	}

	/**
	 * Refresh the display of the results list ( The focus will shift to the
	 * list )
	 */
	private synchronized void updateResultListUI() {

		DefaultListModel listModel = (DefaultListModel) resultList.getModel();
		int dataSize = listModel.getSize();

		/**
		 * Set the input box to match the display size
		 */
		resultList.setFixedCellWidth(textComponent.getWidth());
		resultList.setFixedCellHeight(textComponent.getHeight());

		int preferredWidth = textComponent.getWidth();
		if (dataSize > resultList.getVisibleRowCount()) {
			preferredWidth += scrollPane.getVerticalScrollBar().getPreferredSize().width;
		}
		int preferredHeight = Math.min(resultList.getVisibleRowCount(), dataSize) * resultList.getFixedCellHeight() + 3; // Reserve
																															// some
																															// space
																															// for
																															// more,
																															// this
																															// value
																															// can
																															// make
																															// their
																															// own
																															// adjustments
																															// not
																															// very
																															// accurate

		scrollPane.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		resultList.updateUI();
		popupMenu.pack();
	}

	/**
	 * The default list of results provided by the mouse movement event handler
	 */
	private MouseMotionAdapter DefaultResultListMouseMotionAdapter = new MouseMotionAdapter() {

		@Override
		public void mouseMoved(MouseEvent e) {
			/**
			 * The operating result is : Select the mouse option, and display
			 * the prompt
			 */
			Point p = e.getPoint();
			if (isResultListContains(p)) {
				/**
				 * The mouse moves within the region in the list
				 */
				int index = p.y / getResultListCellHeight();
				// Follow the cursor
				setSelectedIndex(index);
				// Show tips long text
				showToolTipOnResultListBySelectedValue(true);
				// Back to text edit box focus
				setFocusOnTextComponent();
			}
		}
	};
	/**
	 * The results provided a list of the default button on a mouse event
	 * handler
	 */
	private final MouseAdapter DefaultResultListMouseAdapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			/**
			 * The operating result is : Set the edit box text for the selected
			 * item, close the list of results , The focus back to edit box,
			 * while the trigger commit Monitor
			 */
			Point p = e.getPoint();
			if (isResultListContains(p)) {
				/**
				 * Mouse click the list item
				 */
				int index = p.y / getResultListCellHeight();
				// The selected
				setSelectedIndex(index);
				//
				if (getSelectedIndex() == index) {
					commitTextBySelectedValue();
				}
				// Back to text edit box focus
				setFocusOnTextComponent();
			}
		}
	};
	/**
	 * The default text editor provided by the event handler keyboard box
	 */
	private final KeyAdapter DefaultTextFieldKeyAdapter = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			/**
			 * Only be dealt with in the current focus
			 */
			if (!e.getComponent().isFocusOwner()) {
				return;
			}

			switch (e.getKeyCode()) {

			case KeyEvent.VK_ENTER:
				/**
				 * The operating result is : Set the edit box text for the
				 * selected item, close the list of results , The focus back to
				 * edit box, while the trigger commit Monitor
				 */
				commitTextBySelectedValue();
				break;

			case KeyEvent.VK_DOWN:
				/**
				 * The operating result is : 1. If the results list is not open,
				 * open the list of results , And select the first item, set the
				 * edit box text 2. If the currently selected item is the last
				 * one, so the focus back to edit box 3. Otherwise, down options
				 * , And change the text for the current edit box Option
				 */
				if (isResultListExpanded()) {
					/**
					 * If the list is expanded
					 */
					final int selectedIndex = getSelectedIndex();
					if (selectedIndex == getResultListSize() - 1) {
						/**
						 * And select the entry for the last
						 */
						// The focus to a text box
						changeFocusToTextField();
					} else {
						/**
						 * Otherwise,
						 */
						// Down a
						setSelectedIndex(selectedIndex + 1);
						showCurrentSelectedValue();
						setFocusOnTextComponent();
					}
				} else {
					if (expandResultList()) {
						/**
						 * Successfully opened the list of results
						 */
						// Select the first item
						setSelectedIndex(0);
					}
				}
				break;

			case KeyEvent.VK_UP:
				/**
				 * The operating result is : 1. If the results list is not open,
				 * open the list of results , And select the last item, set the
				 * edit box text 2. If the currently selected item as the first
				 * item, so the focus back to edit box 3. Otherwise, the Move
				 * Options , And change the text for the current edit box Option
				 */
				if (isResultListExpanded()) {
					/**
					 * If the list is expanded
					 */
					final int selectedIndex = getSelectedIndex();
					if (selectedIndex == 0) {
						/**
						 * And select the entry for the first
						 */
						// The focus to a text box
						changeFocusToTextField();
					} else {
						/**
						 * Otherwise,
						 */
						if (selectedIndex == -1) {
							// Moved to the last
							setSelectedIndex(getResultListSize() - 1);
						} else {
							// Move a
							setSelectedIndex(selectedIndex - 1);
						}
						showCurrentSelectedValue();
					}
				} else {
					if (expandResultList()) {
						/**
						 * Successfully opened the list of results
						 */
						// Select the last item
						setSelectedIndex(getResultListSize() - 1);
					}
				}
				break;

			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT: // Operation around the same
				/**
				 * The operating result is : Set the edit text item is selected,
				 * and close the list of results , The focus back to edit box
				 */
				if (isResultListExpanded()) {
					/**
					 * If the list is expanded
					 */
					if (getSelectedIndex() != -1) {
						/**
						 * Option is selected and there
						 */
						showCurrentSelectedValue();
					}
					collapseResultList();
				}
				// To shift the focus to the text edit box
				changeFocusToTextField();
				break;
			}
			/**
			 * In order to ensure the focus is always in edit box
			 */
			setFocusOnTextComponent();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (!e.getComponent().isFocusOwner()) {
				return;
			}

			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
					|| keyCode == KeyEvent.VK_RIGHT
					|| keyCode == KeyEvent.VK_ENTER /*
													 * || keyCode ==
													 * KeyEvent.VK_BACK_SPACE
													 */) {
				return;
			}
			/**
			 * Open the list of results
			 */
			expandResultList();
			/**
			 * In order to ensure the focus is always in edit box
			 */
			setFocusOnTextComponent();
		}
	};

	/*********************************************************
	 * Some of the interface defined
	 */
	public interface CommitListener {

		public void commit(String value);
	}

	public interface DataMatchHelper {

		/**
		 * Determines whether the specified text is allowed to match
		 * 
		 * @param text
		 * @return
		 */
		public boolean isMatchTextAccept(String text);

		/**
		 * To determine whether a given text value matches the value
		 * 
		 * @param matchedText
		 * @param data
		 * @return
		 */
		public boolean isDataMatched(String matchText, Object data);
	}

	/*********************************************************
	 * Default implementation
	 */
	/**
	 * The default data matching assistant
	 * 
	 * @author Univasity
	 */
	public class DefaultDataMatchHelper implements DataMatchHelper {

		public boolean isMatchTextAccept(String text) {
			return (text != null && text.length() > 0);
		}

		public boolean isDataMatched(String matchText, Object value) {
			if (value != null && value.toString().indexOf(matchText) != -1) {
				return true;
			}
			return false;
		}
	}
}