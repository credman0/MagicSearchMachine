import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONObject;

public class GUI extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1L;
	private SearchField searchField;
	private Vector<String> resultNameVector;
	// JSONObject resultJson;
	private JList<String> resultList;
	
	// JSONObject d
	private JTextField cardNameField;
	private JTextField cardManaField;
	private JTextArea cardTextArea;
	private ImageDisplayCanvas imageCanvas;
	private JSONHandler jsonHandler;

	private String selectedCardName;

	public static void main(String args[]) {
		GUI main = new GUI();
		main.init();
	}

	public void init() {
		jsonHandler = new JSONHandler();
		jsonHandler.initilizeCardJSON();

		this.setSize(800, 600);
		this.setLayout(new BorderLayout());

		// add search field at top of frame
		Box topBox = Box.createHorizontalBox();
		searchField = new SearchField("");
		topBox.add(searchField);

		// add the box containing the search to the frame
		this.add(topBox, BorderLayout.NORTH);

		// add listener to fire when something is typed
		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateSearch();

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateSearch();

			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateSearch();

			}

		});

		// add key listener to move to list when requested
		searchField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN || arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					resultList.requestFocusInWindow();
					resultList.setSelectedIndex(0);
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		// we want space bar anywhere to redirect us back to the search
		searchField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(' '), "Jump Search");
		searchField.getActionMap().put("Jump Search", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				searchField.requestFocus();
				searchField.setCaretPosition(searchField.getText().length());
			}

		});

		// add output field on left of field
		resultNameVector = new Vector<String>();
		resultList = new JList<String>(resultNameVector);
		resultList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				selectedCardName = resultList.getSelectedValue();
				updateCardInfo();

			}

		});

		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane outputScrollPane = new JScrollPane(resultList);
		this.add(outputScrollPane, BorderLayout.WEST);

		// box to contain card info
		Box cardInfoBox = Box.createVerticalBox();

		// inner box contains just the card name + mana cost
		Box cardTitleBox = Box.createHorizontalBox();

		// add card name
		cardNameField = new JTextField("name");
		cardNameField.setEditable(false);
		cardNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardNameField.getPreferredSize().height));
		cardTitleBox.add(cardNameField);

		// add mana cost
		cardManaField = new JTextField("mana");
		cardManaField.setEditable(false);
		cardManaField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardManaField.getPreferredSize().height));
		cardTitleBox.add(cardManaField);

		// inner box add to info box
		cardInfoBox.add(cardTitleBox);

		// add card text container
		cardTextArea = new JTextArea("info");
		cardTextArea.setWrapStyleWord(true);
		cardTextArea.setEditable(false);
		cardTextArea.setLineWrap(true);
		cardInfoBox.add(cardTextArea);

		// add the card info
		this.add(cardInfoBox, BorderLayout.CENTER);

		// add canvas to display card images
		imageCanvas = new ImageDisplayCanvas();
		this.add(imageCanvas, BorderLayout.EAST);
		
		// load image cache if it exists
		ImageCacheHandler.loadCache();

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(this);
		this.setVisible(true);

		updateSearch();
	}
	


	// display the information of the currently selected card
	private void updateCardInfo() {
		if (selectedCardName != null) {
			cardNameField.setText(selectedCardName);
			cardManaField.setText(jsonHandler.getCardMana(selectedCardName));
			cardTextArea.setText(jsonHandler.getCardText(selectedCardName));
			imageCanvas.setImage(selectedCardName);
		}else {
			cardNameField.setText("");
			cardManaField.setText("");
			cardTextArea.setText("");
			imageCanvas.setImage(selectedCardName);
		}
	}

	private SearchThread searchThread = new SearchThread();
	private void updateSearch() {
		if (searchThread.isDone()) {
			SearchThread searchThread = new SearchThread();
			searchThread.execute();
		}else {
			searchThread.cancel(true);
			SearchThread searchThread = new SearchThread();
			searchThread.execute();
		}
		
	}
	
	private class SearchThread extends SwingWorker<Void,Void> {
		JSONObject resultJson;
		@Override
		public Void doInBackground() {
			String searchQuery = searchField.getText();
			resultJson = jsonHandler.getSearchResultList(searchQuery);
			resultNameVector.clear();
			// repopulate result vector if the result had elements
			if (resultJson.length() > 0)
				for (String name : JSONObject.getNames(resultJson)) {
					resultNameVector.addElement(name);
				}
			Collections.sort(resultNameVector);
			return null;
		}
		
		@Override
		public void done() {
			resultList.updateUI();
			updateCardInfo();
		}
		
	}

	private class SearchField extends JTextField {
		private static final long serialVersionUID = 1L;

		private SearchField(String initText) {
			this.setText(initText);
			addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					// SearchField.this.select(0, getText().length());
				}

				@Override
				public void focusLost(FocusEvent e) {
					SearchField.this.select(0, 0);
				}
			});
		}
	}

	private class ImageDisplayCanvas extends JPanel {
		private static final long serialVersionUID = 1L;
		protected BufferedImage image;
		protected String cardName;

		private ImageDisplayCanvas() {
			setPreferredSize(new Dimension(233, 310));
		}

		@Override
		public void paint(Graphics g) {
			setPreferredSize(new Dimension(233, 310));
			if (selectedCardName == null) 
				return;
			
			g.drawImage(image, 0, 0, this);
		}
		
		protected ImageFetcherWorker fetcher = new ImageFetcherWorker();
		
		public void setImage(String cardName) {
			this.cardName = cardName;
			if (fetcher.isDone()) {
				fetcher = new ImageFetcherWorker();
				fetcher.execute();
			}else {
				fetcher.cancel(true);
				fetcher = new ImageFetcherWorker();
				fetcher.execute();
			}
		}
		
		private class ImageFetcherWorker extends SwingWorker<Void,Void>{

			@Override
			protected Void doInBackground() {
				try {
					image = ImageCacheHandler.getCardImage(jsonHandler.getMultiverseID(cardName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void done() {
				repaint();
			}
			
		}
	}



	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
			File cacheFolder = new File("cache/");
			if (!cacheFolder.exists()){
				cacheFolder.mkdirs();
			}
			ImageCacheHandler.saveCache();
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
