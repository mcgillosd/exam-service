/* 
/*
 * Created on Aug 26, 2013 2:52:25 PM
 * 
 * Taken from the Java Tutorials Code Sample � TreeDemo.java
 * with some modifications. 
 * 
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;



/**
 * 
 */

/**
 * @author OSD Admin
 *
 */
public class PanelHelp extends JPanel implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
    private static boolean DEBUG = false;
    int width;
    int height;
         
	public PanelHelp() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)d.getWidth();
		height = (int)d.getHeight();
		
		int width2 = width;
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge =
	   		GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int j = 0; j < gs.length; j++) {
	   		GraphicsDevice gd = gs[j];
	   		GraphicsConfiguration[] gc = gd.getConfigurations();
	   		for (int i = 0; i < gc.length; i++) {
	   			virtualBounds = virtualBounds.union(gc[i].getBounds());
	   			if (j == 0) {
	      		  width2 = virtualBounds.width;
	      		  break;
	   			}
	   			
	   		}
	   		
		}
		if (gs.length > 1) {
			width -= width2;
		}
		
		
		Box.createVerticalGlue();
		add(Box.createRigidArea(new Dimension((int)(width/6.0), 0)));
		add(createPanel());
		add(Box.createRigidArea(new Dimension((int)(width/6.0), 0)));
	}
	
	private JPanel createPanel() {
		
			
		
		JPanel pane_main = new JPanel();
		pane_main.setLayout(new BorderLayout());
		
		JPanel panel_center = new JPanel();
		panel_center.setLayout(new BoxLayout(panel_center, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel(new GridLayout(0,1));
		DefaultMutableTreeNode top =
		            new DefaultMutableTreeNode("OSD Exams Management User Guide");
		createNodes(top);
		
		tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
 
        //Listen for when the selection changes.
        tree.addTreeSelectionListener((TreeSelectionListener) this);
        tree.setFont(new Font("Georgia", Font.PLAIN, 16));
		tree.setRowHeight(24);
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);
      
        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        initHelp();
        JScrollPane htmlView = new JScrollPane(htmlPane);
        
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(treeView);
        splitPane.setRightComponent(htmlView);
        
        splitPane.setDividerLocation(width/6); 
    
        splitPane.setMinimumSize(new Dimension((int)(width*3/5), (int)(height*0.36)));
       
      
        Font font_text = new Font("Georgia", Font.PLAIN, 20);
    	JButton button = new JButton("Exit");
    	button.setMaximumSize(new Dimension(120, 40));
		button.setFont(font_text);
		button.addActionListener(new ExitActionListener()); 
		
		
		
		panel.add(splitPane);
        
		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.LINE_AXIS));
		panel_buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_buttons.add(button);
        
		
		panel_center.add(Box.createRigidArea(new Dimension(0,60)));
        panel_center.add(panel);
        panel_center.add(Box.createRigidArea(new Dimension(0,20)));
        panel_center.add(panel_buttons);
        panel_center.add(Box.createRigidArea(new Dimension(0,30)));
        
        pane_main.add(panel_center, BorderLayout.CENTER);
        return pane_main;

	}
	
	class ExitActionListener implements ActionListener {
		
			
		public ExitActionListener() {
			
		}
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	/** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();
 
        if (node == null) return;
 
        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            BookInfo book = (BookInfo)nodeInfo;
            displayURL(book.bookURL);
            if (DEBUG) {
                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
            displayURL(helpURL); 
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }
	private void initHelp() {
        String s = "exams_help.html";
        helpURL = this.getClass().getResource(s);
        if (helpURL == null) {
            System.err.println("Couldn't open help file: " + s);
        } else if (DEBUG) {
            System.out.println("Help URL is " + helpURL);
        }
 
        displayURL(helpURL);
    }
	
	private void displayURL(URL url) {
		try {
			if (url != null) {
                htmlPane.setPage(url);
            } 
			else { //null url
				htmlPane.setText("File Not Found");
                if (DEBUG) {
                    System.out.println("Attempted to display a null URL.");
                }
            }
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }
	
	private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;
 
        category = new DefaultMutableTreeNode("Midterms");
        top.add(category);
 
        book = new DefaultMutableTreeNode(new BookInfo
                ("Description", "description_midterm.html"));
        category.add(book);
            
        book = new DefaultMutableTreeNode(new BookInfo
            ("Required files", "files_midterm.html"));
        category.add(book);
 
        category = new DefaultMutableTreeNode("Finals");
        top.add(category);
 
        
        book = new DefaultMutableTreeNode(new BookInfo
            ("Description", "description_final.html"));
        category.add(book);
        
        book = new DefaultMutableTreeNode(new BookInfo
            ("Required files", "files_final.html"));
        category.add(book);
 
               
        category = new DefaultMutableTreeNode("Editor");
        top.add(category);
 
        book = new DefaultMutableTreeNode(new BookInfo
            ("Description", "description_editor.html"));
        category.add(book);
    }
	
	private class BookInfo {
        public String bookName;
        public URL bookURL;
 
        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = getClass().getResource(filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                                   + filename);
            }
        }
 
        public String toString() {
            return bookName;
        }
    }

}
