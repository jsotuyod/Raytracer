package cg.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cg.ui.components.ImageLoad;



public class showImage extends JFrame {
	private static showImage instance = null;

	private ImageLoad originalPreview;

	
	private JScrollPane leftPanel;

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;

	private JPanel jPanel2 = null;


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getJPanel2(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setBounds(new Rectangle(7, 7, 800, 600));
			jPanel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	        jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));
	        TitledBorder titled;
	        JPanel innerPanel;
	        titled = BorderFactory.createTitledBorder(showImage.fileName);
	        innerPanel = addCompForBorder(titled, jPanel2);
	        originalPreview = new ImageLoad();
	        
	        leftPanel = new JScrollPane(originalPreview);
	        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	        innerPanel.add(leftPanel);
	        
	        jPanel2.add(innerPanel);
		}
		return jPanel2;
	}


	private static String fileName;
	
	public static void show(final String filename) {
		showImage.fileName = filename;
		
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				showImage thisClass = showImage.getInstance();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setPreferredSize(new Dimension(840, 630));
				//thisClass.setResizable(false);
				thisClass.pack();
				thisClass.setVisible(true);
				showImage.getInstance().originalPreview.loadImage( new File(filename) );
				showImage.getInstance().leftPanel.setViewportView(showImage.getInstance().originalPreview);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	private showImage() {
		super();
		initialize();
	}
	
	public static showImage getInstance() {
		
		if ( instance == null ) {
			instance = new showImage();
			instance.setSize(new Dimension(130, 138));
		}
		return instance;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(875, 617);
		this.setContentPane(getJContentPane());
		this.setTitle("Raytracing");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private JPanel addCompForBorder(Border border,
			Container container) {
		JPanel comp = new JPanel(new GridLayout(1, 1), false);
		comp.setBorder(border);

		return comp;
	}
	

}
