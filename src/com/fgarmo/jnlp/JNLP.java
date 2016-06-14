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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.StringList;

/**
*
* @author Fran García <garmodev@gmail.com>
*/
public class JNLP {
	final int NUM_NGRAMS = 2;

	
	private JFrame frmJnlp;
	private JLabel lblLoadingCorpus;
	private JTextField tbTotalWords;
	private JTextField tbSearch;
	private List<List<String>> corpusNgrams;
	private List<NLPModel> nlpModels;
	private JTextArea tbSearchResults;
	private File lastDir;
	private String corpusContent;
	private JSpinner spMin;
	private JSpinner spMax;
	private JCheckBox cbRFPercentage;
	private static JProgressBar pbLoading;

	private List<NGramModel> ngramModels;
	private Integer totalCorpusWords = 0;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JNLP window = new JNLP();
					window.frmJnlp.setVisible(true);
					
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JNLP() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		 System.setProperty("apple.laf.useScreenMenuBar", "true");
	     System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Name");
	        
		frmJnlp = new JFrame();
		frmJnlp.setTitle("JNLP");
		frmJnlp.setBounds(0, 0, 1080, 734);
		frmJnlp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmJnlp.setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmLoadCorpusFolders = new JMenuItem("Load Corpus Folders");
		mntmLoadCorpusFolders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectCorpusFolder();
			}
		});
		mnFile.add(mntmLoadCorpusFolders);
		
		mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmQuit);
		
		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmOutThis = new JMenuItem("About this");
		mntmOutThis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutThis aboutThis = new AboutThis();

				JOptionPane.showMessageDialog(frmJnlp,
						aboutThis.getEditorPane(), "About this",
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		mnHelp.add(mntmOutThis);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Corpus Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Search Results", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 463, Short.MAX_VALUE)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGap(0, 550, Short.MAX_VALUE)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(20)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		tbSearchResults = new JTextArea();
		tbSearchResults.setEditable(false);
		scrollPane_1.setViewportView(tbSearchResults);
		panel_2.setLayout(gl_panel_2);
		
		toolBar = new JToolBar();
		GroupLayout groupLayout = new GroupLayout(frmJnlp.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE))
						.addComponent(toolBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1074, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JButton btnLoad = new JButton("Load Corpus Folders");
		toolBar.add(btnLoad);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectCorpusFolder();
			}
		});
		
		JLabel lblCorpusLoaded = new JLabel("Corpus Total Words:");
		
		tbTotalWords = new JTextField();
		tbTotalWords.setEditable(false);
		tbTotalWords.setColumns(10);
		
		lblLoadingCorpus = new JLabel("");
		
		tbSearch = new JTextField();
		tbSearch.setEnabled(false);
		tbSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(tbSearch.getText(), false, false);
			}
		});
		tbSearch.setColumns(10);
		
		JLabel lblSearch = new JLabel("Search:");
		
		JLabel lblNgramsMin = new JLabel("NGrams Min:");
		SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 8, 1);  
		spMin = new JSpinner(model1);
		spMin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(corpusContent != "" || corpusContent != null){
					loadNGrams(corpusContent);
				}
			}
		});
		
		JLabel lblNgramsMax = new JLabel("NGrams Max:");
		SpinnerNumberModel model2 = new SpinnerNumberModel(8, 1, 8, 1);
		spMax = new JSpinner(model2);
		spMax.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(corpusContent != "" || corpusContent != null){
					loadNGrams(corpusContent);
				}
			}
		});
		
		cbRFPercentage = new JCheckBox("Relative Frequency in %");
		cbRFPercentage.setSelected(true);
		
		pbLoading = new JProgressBar();
		pbLoading.setMaximum(400);
		//lblLoadingCorpus.setVisible(false);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(14)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblCorpusLoaded)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tbTotalWords, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pbLoading, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblLoadingCorpus, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSearch)
							.addGap(18)
							.addComponent(tbSearch, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
							.addComponent(lblNgramsMin)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spMin, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNgramsMax)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spMax, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(cbRFPercentage)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(14)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblCorpusLoaded)
							.addComponent(tbTotalWords, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(pbLoading, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblLoadingCorpus, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(tbSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblSearch)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNgramsMin)
						.addComponent(spMin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNgramsMax)
						.addComponent(spMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbRFPercentage))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		frmJnlp.getContentPane().setLayout(groupLayout);
	}
	
private String removeUrl(String commentstr){
	    String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	    Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(commentstr);
	    int i = 0;
	    while (m.find()) {
	    	if(commentstr != null && m != null && m.group(i) != null)
	    		commentstr = commentstr.replaceAll(m.group(i),"").trim();
	        i++;
	    }
	    return commentstr;
	}

	static Integer pbValue = 0;
	public static class PBThread implements Runnable{

	        public void run(){
	        	
	            for (int i=0; i<=100; i++, pbValue++){
			    	  pbLoading.setValue(pbValue);
			    	  pbLoading.repaint();
	                try{Thread.sleep(1);} //Sleep 1 milliseconds
	                catch (InterruptedException err){}
	            }
	        }
	    }

	
	public void selectCorpusFolder(){
		JFileChooser chooser = new JFileChooser(lastDir);
		chooser.setApproveButtonText("Choose Corpus Directories");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		lblLoadingCorpus.setText("Loading Corpus...");
		pbLoading.setValue(pbLoading.getMinimum());
		pbLoading.repaint();

		
		// Show the dialog; wait until dialog is closed
		if(chooser.showOpenDialog(frmJnlp) == JFileChooser.APPROVE_OPTION){	
			new Thread(new PBThread()).start(); //Start the thread

			clearLoad();
			tbSearch.setEnabled(false);
			File[] dirs = chooser.getSelectedFiles();
			List<File> filesInDirectory = new ArrayList<File>();
			corpusContent = "";
			
			for(File f : dirs){
				filesInDirectory.addAll(Arrays.asList(f.listFiles()));
			}
			
			lastDir = new File(dirs[dirs.length-1].getParent());
			int i=1;
			for ( File file : filesInDirectory ) {
				//content+="Doc numer "+i+" with path: "+file.getAbsolutePath()+"\n"+parseFiles(file.getAbsolutePath())+"\n................................\n\n";
				corpusContent+=parseFiles(file.getAbsolutePath())+" ";
				i++;
			}
			corpusContent = preprocessCorpus(corpusContent, false);
			

			
			//laod ngrams
			new Thread(new PBThread()).start(); //Start the thread
			loadNGrams(corpusContent);
			pbLoading.setValue(pbLoading.getMaximum());
			pbLoading.repaint();
			

			//String ngrams= getNgrams(corpusContent, (Integer) spMin.getValue(), (Integer) spMax.getValue());//getFrequencies(corpusContent,NUM_NGRAMS);
			
			//tbResults.setText(ngrams);
			tbSearch.setEnabled(true);
			spMin.setEnabled(true);
			spMax.setEnabled(true);
			
			File res = new File(chooser.getCurrentDirectory().getAbsolutePath()+"/result.txt");
			File corpus_loaded = new File(chooser.getCurrentDirectory().getAbsolutePath()+"/corpus_loaded.txt");
			FileWriter fw;
			try {
				// if file doesnt exists, then create it
				if (!corpus_loaded.exists()) {
					corpus_loaded.createNewFile();
				}
				
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(corpus_loaded), "UTF-8"));
				out.append(corpusContent);
				out.flush();
				out.close();
				
				lblLoadingCorpus.setText("Corpus Loaded");
				System.out.println("Done");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			lblLoadingCorpus.setText("");
	}
	
	private String preprocessCorpus(String corpus, Boolean isCaseSensitive){		
		corpus = isCaseSensitive ? corpus : corpus.toLowerCase();
		
		corpus = corpus.replaceAll("http.*?\\s", " ") //replace urls
				.replaceAll("([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)", "") // Replace emails 
                // by an empty string
					.replaceAll("\\p{Punct}", "") // Replace all punctuation. One of
													// !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
					.replaceAll("\\d", "") // Replace any digit by an empty string
					.replaceAll("®", "")
					.replaceAll("©", "")
					.replaceAll("™", "")
//					.replaceAll("[^\\x20-\\x7f]", "")
					.replaceAll("\\p{Blank}{2,}+", " "); // Replace any Blank (a  space or 
         												// a tab) repeated more than once
         												// by a single space.
		return corpus;
	}
	
	private void clearLoad(){
		tbSearch.setText("");
		tbTotalWords.setText("");
		tbSearchResults.setText("");
		ngramModels = new ArrayList<NGramModel>();
		spMin.setEnabled(false);
		spMax.setEnabled(false);
	}
	private String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	private String parseFiles(String path){
		File input = new File(path);
		Document doc;
		String res = "";
		
		try {
			String ext = getFileExtension(input);
			
			if(!ext.equals("DS_Store")){
				System.out.println("File: "+path);
				System.out.println("File Extension: "+ext);
				
				if(ext.equals("txt")){
					String encoding = getEnconding(path);
					System.out.println("Encoding read: "+encoding);
					
					//doc = Jsoup.parse(input, encoding);//, "http://example.com/");
					res = readTxtFile(path);
					return res;
				}
				else{
					doc = Jsoup.parse(input, "UTF-8");//, "http://example.com/");
				}
				
				
				Element metaCharset = doc.select("meta[http-equiv=content-type], meta[charset]").first();			
				System.out.println(metaCharset);
				
				if (metaCharset != null) { // if not found, will keep utf-8 as best attempt
			        String foundCharset = null;
			        if (metaCharset.hasAttr("http-equiv")) {
			            foundCharset = metaCharset.attr("content").split("charset=")[1];
			        }
			        if (foundCharset == null && metaCharset.hasAttr("charset")) {
			            try {
			                if (Charset.isSupported(metaCharset.attr("charset"))) {
			                    foundCharset = metaCharset.attr("charset");
			                }
			            } catch (IllegalCharsetNameException e) {
			                foundCharset = null;
			            }
			        }
			        if(foundCharset != null){
			        	if(foundCharset.equals("windows-1252") || (!foundCharset.equals("windows-1252") && !foundCharset.equals("UTF-8") && !foundCharset.equals("utf-8")))
			        		doc = Jsoup.parse(input, "ISO-8859-1");//, "http://example.com/");
			        }
				}
				else{
					Element head = doc.head();
	
		            if (head != null) {
		                head.appendElement("meta").attr("charset", doc.charset().displayName());
		            }
				}
				
				
		        // Remove obsolete elements
		        doc.select("meta[name=charset]").remove();
		        
		        
				//doc.select(":containsOwn(\u00a0)").remove();
	
				String ownText = doc.body().ownText();
				String text = doc.body().text().replace("\u00a0", " ");
				
				
				res = Jsoup.clean(text, Whitelist.simpleText()); //.removeTags("p","br", "span","a")
				
	
				// Parse string into a document
				doc = Jsoup.parse(res);
	
				// Adjust escape mode
				doc.outputSettings().escapeMode(EscapeMode.xhtml);
	
				// Get back the string
				res=doc.body().text();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;

	}
	
	private String getEnconding(String path){
		String encoding = "";
		FileInputStream fis = null;
	      InputStreamReader isr =null;
	      String s;
	      
	      try {
	         // new input stream reader is created 
	         fis = new FileInputStream(path);
	         isr = new InputStreamReader(fis);
	         
	         // the name of the character encoding returned
	         encoding=isr.getEncoding();
//	         System.out.print("Character Encoding: "+encoding);
	         
	         
	      // closes the stream and releases resources associated
	         if(fis!=null)
	            fis.close();
	         if(isr!=null)
	            isr.close();
	         
	         return encoding;
	         
	      } catch (Exception e) {
	      
	         // print error
	         System.out.print("The stream is already closed");
	      }
	      
	      return encoding;
	}
	
	private String readTxtFile(String path){
		String content = "";
		
		FileInputStream fis = null;
	      InputStreamReader isr =null;
	      String s;
	      
	      try {
	         // new input stream reader is created 
	         fis = new FileInputStream(path);	         
	         File f = new File(path);
	         
	         String[] charsetsToBeTested = {"UTF-8","ISO-8859-1","windows-1252", "windows-1253", "ISO-8859-7"};

	         CharsetDetector cd = new CharsetDetector();
	         Charset charset = cd.detectCharset(f, charsetsToBeTested);

	         if (charset != null) {
	             try {
	            	 isr = new InputStreamReader(new FileInputStream(f), charset);
	            	 BufferedReader reader = new BufferedReader(isr);
	                 String line;
	                 while ((line = reader.readLine()) != null) {
	                	 content += line;
	                	 System.out.println(line);
	                 }
	                 isr.close();
	             } catch (FileNotFoundException fnfe) {
	                 fnfe.printStackTrace();
	             }catch(IOException ioe){
	                 ioe.printStackTrace();
	             }

	         }else{
	             System.out.println("Unrecognized charset.");
	         }	         
	      } catch (Exception e) {
	         System.out.print("The stream is already closed");
	      }
	      
	      return content;
	}
	
	private String getNgrams(String text,Integer minLength,Integer maxLength){
		String r = "";
		
		String[] words = text.split("( )+");
		tbTotalWords.setText(new Integer(words.length).toString());
		
		//NGramGenerator g = new NGramGenerator();
		//List<String> ngrams = g.generate(Arrays.asList(words), length, " ");
		long time = System.currentTimeMillis();
		
		Integer curLength = minLength;
		//List<NGramModel> ngms = new ArrayList<NGramModel>();
		Boolean prevLoadedSoloNGram = minLength == maxLength && ngramModels.get(minLength-1) != null;
		Integer nextNgram =  prevLoadedSoloNGram ? minLength : minLength-1;
		
		for(int n = nextNgram; n < maxLength; n++,curLength++) {
			NGramModel ngm = new NGramModel();
			for(int i = 0; i < words.length-curLength+1; i++) {
		        String[] ngram = new String[curLength];
		        for(int j = 0; j < curLength; j++) {
		            ngram[j] = words[i+j];
		        }
		        ngm.add(new StringList(ngram));
		    }
			ngramModels.set(n, ngm);
		}
		
		if(!prevLoadedSoloNGram){
			int totalNgrams = 0;
			for(NGramModel n : ngramModels){
				if(n != null)
					totalNgrams+=n.numberOfGrams();
			}
			
			r="Total words: "+words.length+", number of NGrams: "+totalNgrams+"\n";
			time = System.currentTimeMillis()-time;
			System.err.println("Loaded in "+(time/1000)+ " s.");
			
			nlpModels = new ArrayList<NLPModel>();
			int i=1;
			for(NGramModel n : ngramModels){
				if(n != null){
					totalNgrams = n.numberOfGrams();
					r+="\n"+i+"Grams. Total: "+totalNgrams+"\n";
					for(StringList s : n){
						int freq = n.getCount(s);
						Double relativeFrecuency = (double) (freq*1.0D/totalNgrams);
						NLPModel nlpModel = new NLPModel(String.join(" ", s), freq, relativeFrecuency);
						nlpModels.add(nlpModel);
						r+=nlpModel.toString()+"\n";
					}
					i++;
				}
			}
		}
		
		return r;
	}
	
	
	String r="";
	Integer ocurrencies = 0;
	private JToolBar toolBar;
	private JMenu mnFile;
	private JMenuItem mntmLoadCorpusFolders;
	private JMenu mnHelp;
	private JMenuItem mntmOutThis;
	private JMenuItem mntmQuit;
	
	private String getFrequencies(String text,int length){
		//NGramModel ngram = new NGramModel();
		text=text.toLowerCase();
		
		String[] words = text.split("\\s+");
		for (int i = 0; i < words.length; i++) {
		    // You may want to check for a non-word character before blindly
		    // performing a replacement
		    // It may also be necessary to adjust the character class
			
		    words[i] = words[i].replaceAll("[^\\p{L}|\\p{Nd}|\\p{Punct}|\\d|\\s]+","").replaceAll("[^\\p{L}|\\p{Nd}|\\p{Punct}|\\d|\\s]+",""); //("[^\\w]", "");
		    System.out.println(words[i]);
		}
		
		
		tbTotalWords.setText(new Integer(words.length).toString());
		
		r = "";
		corpusNgrams = new ArrayList<List<String>>();
		
		for (int n = 1; n <= length; n++) {
			//List<String> ngramsList = new ArrayList<String>();
            //for (String ng : ngrams(n, text, words)){
            	//ngramsList.add(ng);
               //r+=ng+"\n";
            //}
            
            corpusNgrams.add(ngrams(n, text, words));
        }
		
		nlpModels = new ArrayList<NLPModel>();
		
		//Double relativeFrecuency = 0D;
		
		corpusNgrams.stream().forEach(ngram_terms->{
			ocurrencies=0;
			ngram_terms.stream().forEach(term->{
				NLPModel nlpModel = getNLPModelMatchesWith(term, true);
				
				if(nlpModel == null || (nlpModel != null && !nlpModels.contains(nlpModel))){
					Integer ocurrencies=0;
					for(String t : ngram_terms){
						if(t.equals(term))
							ocurrencies++;
					}
				
					Double relativeFrecuency = (double) (ocurrencies*1.0D/ngram_terms.size());
					nlpModel = new NLPModel(term, ocurrencies, relativeFrecuency);
					nlpModels.add(nlpModel);
					r+=nlpModel.toString()+"\n";
				}
			});
		});
			
		
		return r;
	}
	
	public static boolean hasNonWordCharacter(String str) {

	    int offset = 0, strLen = str.length();
	    while (offset < strLen) {
	        int curChar = str.codePointAt(offset);
	        offset += Character.charCount(curChar);
	        if (!Character.isLetter(curChar)) {
	            return true;
	        }
	    }

	    return false;
	}
	
	public static List<String> ngrams(int n, String str, String[] words) {
        List<String> ngrams = new ArrayList<String>();
        //String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++){
        	String gram=concat(words, i, i+n);
        	//if(!ngrams.contains(gram))
        		ngrams.add(gram);
        }
        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }
    
    private void loadNGrams(String corpus){
    	String r = "";
		
		String[] words = corpus.split("( )+");
		totalCorpusWords = words.length;
		tbTotalWords.setText(totalCorpusWords.toString());
		
		int minLength = (Integer) spMin.getValue();
		int maxLength = (Integer) spMax.getValue();
		
		long time = System.currentTimeMillis();
		
		Integer curLength = minLength;

		for(int n = 0; n < maxLength; n++,curLength++) {
			NGramModel ngm = new NGramModel();
			for(int i = 0; i < words.length-curLength+1; i++) {
		        String[] ngram = new String[curLength];
		        for(int j = 0; j < curLength; j++) {
		            ngram[j] = words[i+j];
		        }
		        ngm.add(new StringList(ngram));
		    }
			ngramModels.add(ngm);
		}
    }
    
    private void search(String searchText, Boolean casSensitive, Boolean ignoreAccents){
    	searchText = !casSensitive ? searchText.toLowerCase() : searchText;
    	searchText = ignoreAccents ? removeDiacriticalMarks(searchText) : searchText;
    	String[] s = searchText.split(" ");
    	Boolean hasAny = false;
    	Integer totalSearchWords = s.length;
    	
    	//get ngrams
		//String ngrams= getNgrams(corpusContent, s.length, s.length);//getNgrams(corpusContent, (Integer) spMin.getValue(), (Integer) spMax.getValue());//getFrequencies(corpusContent,NUM_NGRAMS);
    	
    	if(totalSearchWords > ngramModels.size()){
    		tbSearchResults.setText("No matches");
    	}
    	else{
	    	NGramModel myNGram = ngramModels.get(totalSearchWords-1);
	    	StringList sws = new StringList(s);
	    	hasAny = myNGram.contains(sws);
	    	
	    	if(hasAny){
	    		int totalNgrams = myNGram.size();
	        	int freq = myNGram.getCount(sws);
	        	Double relativeFrecuency = (double) ((freq*1.0D)/totalCorpusWords);
	        	NLPModel nlpModel = new NLPModel(String.join(" ", sws), freq, relativeFrecuency);
	        	String rf = cbRFPercentage.isSelected() ? new DecimalFormat("###.#################").format(nlpModel.getRelativeFrecuency()*100)+"%" : new DecimalFormat("###.#################").format(nlpModel.getRelativeFrecuency());
	    		tbSearchResults.setText("Total "+totalSearchWords+"Grams: "+totalNgrams+"\nOcurrencies: "+nlpModel.getOcurrencies()+"\nRelative Frequency: "+rf);//nlpModel.toString());
	    	}
	    	else{
	    		tbSearchResults.setText("No matches");
	    	}
    	}
    }
    
    public String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    private NLPModel getNLPModelMatchesWith(String searchText, Boolean ignoreAccents){
    	NLPModel res = null;
    	Boolean hasAny = false;
    	
    	for(NLPModel m : nlpModels){
    		hasAny = !ignoreAccents ? m.getTerm().equals(searchText) : removeDiacriticalMarks(m.getTerm()).equals(searchText);
    		
    		if(hasAny){
    			res = m;
    			break;
    		}
    	}
    	
    	return res;
    }
}
