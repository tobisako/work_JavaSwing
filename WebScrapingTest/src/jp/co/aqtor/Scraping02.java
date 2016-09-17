package jp.co.aqtor;

/*
 * ここのページを●パクリ。
 * http://allabout.co.jp/gm/gc/80633/4/
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

public class Scraping02 extends JFrame implements ActionListener,HyperlinkListener {
	private static final long serialVersionUID = 1L;
	private JTextField url;
	private JButton load,analyze;
	private JEditorPane html;
	private JTextArea src,href;

	public static void main(String[] args) {
		new Scraping02().setVisible(true);
	}

	public Scraping02(){
		this.setSize(300,300);
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		pane.add(p1,BorderLayout.NORTH);
		url = new JTextField();
		p1.add(url,BorderLayout.CENTER);
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(1,2));
		p1.add(p2,BorderLayout.EAST);
		load = new JButton("load");
		load.addActionListener(this);
		p2.add(load);
		analyze = new JButton("analyze");
		analyze.addActionListener(this);
		p2.add(analyze);
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(2,1));
		pane.add(p3,BorderLayout.CENTER);
		html = new JEditorPane();
		html.setEditable(false);
		html.addHyperlinkListener(this);
		p3.add(new JScrollPane(html));
		JPanel p4 = new JPanel();
		p4.setLayout(new GridLayout(2,1));
		p3.add(p4);
		src = new JTextArea();
		p4.add(new JScrollPane(src));
		href = new JTextArea();
		p4.add(new JScrollPane(href));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void hyperlinkUpdate(HyperlinkEvent ev) {
		if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				URL u = ev.getURL();
				url.setText(u.toString());
				html.setPage(u);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent ev){
		if (ev.getSource() == load) load();
		if (ev.getSource() == analyze) analyze();
	}

	public void load(){
		try {
			html.setPage(url.getText());
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"Webページの読み込みに失敗しました。");
		}
	}

	public void analyze(){
		src.setText("<IMG SRC>リスト\n");
		href.setText("<A HREF>リスト\n");
		try {
			URL u = new URL(url.getText());
			InputStream is = u.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ParserDelegator pd = new ParserDelegator();
			HTMLEditorKit.ParserCallback cb = new ParserCallback();
			pd.parse(br, cb, true);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"Webページの読み込みに失敗しました。");
		}
	}

	public void addHref(String s){
		href.append(s + "\n");
	}
	public void addSrc(String s){
		src.append(s + "\n");
	}
}

