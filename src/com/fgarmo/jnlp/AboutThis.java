/* 
 * JNLP is an open-source useful graphical desktop application for Natural Language Processing.
 * More details on <https://github.com/garmo/JNLP/blob/master/README.md>
 * Copyright (C) 2016 Fran García
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.fgarmo.jnlp;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
*
* @author Fran García <garmodev@gmail.com>
*/
public class AboutThis {

	private JEditorPane editorPane;
	
	public AboutThis(){
		// Cambiamos el anti-aliasing on
	    System.setProperty("awt.useSystemAAFontSettings", "on");
	    editorPane = new JEditorPane();

	    // Cambiamos las fuentes
	    editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);  
	    editorPane.setFont(new Font("Arial", Font.BOLD, 13));
	
	    editorPane.setPreferredSize(new Dimension(520,180));
	    editorPane.setEditable(false);
	    editorPane.setContentType("text/html");
	    editorPane.setText(
	            "<html>"
	            + "<body>"
	            + "<table border='0px' cxellpadding='10px' height='100%'>"
	            + "<tr>"
	            + "<td valign='center'>"
	            + "<img src='"
	//            url para la imagen
	//            + "http://a2.twimg.com/profile_images/1595129953/1318972002_Command_reasonably_small.png"
	//             Imagen almacenada en proyecto
	            + AboutThis.class.getResource("/resources/QG4Engineers.png").toExternalForm()
	            + "'>"
	            + "</td>"
	            + "<td>"
	            + "Developed by Fran García © 2016.<br /><br />Follow me on Twitter: "
	            + "<a href=\"http://twitter.com/#!/QG4Engineers\"><b>@QG4Engineers</b></a>"
	            + "<br /><br />"
	            + "You can visit my blog site too: "
	            + "<a href=\"http://quickguide4engineers.blogspot.com/\"><b>Quick Guide 4 Engineers</b></a>"
	            + "<br /><br />"
	            + "GitHub: "
	            + "<a href=\"https://github.com/garmo/\"><b>Go to GitHub</b></a>"
	            + "</td>"
	            + "</tr>"
	            + "</table>"
	            + "</body>"
	            + "</html>"
	            );
	
	    // Hacemos el JOptionPane que sea resizable (redimenzionable), para ello usamos el HierarchyListner
	    editorPane.addHierarchyListener(new HierarchyListener() {
	        public void hierarchyChanged(HierarchyEvent e) {
	            Window window = SwingUtilities.getWindowAncestor(editorPane);
	            if (window instanceof Dialog) {
	                Dialog dialog = (Dialog)window;
	                if (!dialog.isResizable()) {
	                    dialog.setResizable(true);
	                }
	            }
	        }
	    });
	
	    // add el hyperlink listner para controlar el evento de click en el enlace
	    editorPane.addHyperlinkListener(new HyperlinkListener() {
	        public void hyperlinkUpdate(final HyperlinkEvent e) {
	            if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) { //cuando se pone el cursor encima del enlace
	                EventQueue.invokeLater(new Runnable() {
	                    public void run() {
	                    	//Mostrar el cursor de la mano
	                        SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	
	                        // Mostramos el url como tooltip
	                        editorPane.setToolTipText(e.getURL().toExternalForm());
	                    }
	                });
	            } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) { //evento cuando quitas el cursor de encima del link
	                EventQueue.invokeLater(new Runnable() {
	                    public void run() {
	                        //mostramos el cursor por defecto
	                        SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getDefaultCursor());
	
	                        // reseteamos el tooltip
	                        editorPane.setToolTipText(null);
	                    }
	                });
	            } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) { //evento cuando se pulsa en el enlace
	            	// Mostrar la url en el browser
	                if (Desktop.isDesktopSupported()) {
	                    try {
	                        Desktop.getDesktop().browse(e.getURL().toURI());
	                    } catch (Exception ex) {
	                        Logger.getLogger(AboutThis.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
	                    }
	                }
	                //System.out.println("Go to URL: " + e.getURL());
	            }
	        }
	    });
	}

	public JEditorPane getEditorPane() {
		return editorPane;
	}

	public void setEditorPane(JEditorPane editorPane) {
		this.editorPane = editorPane;
	}
	

        
}
