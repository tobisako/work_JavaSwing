package aqtor.co.jp;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class WebApiClientTest01 {

	private JFrame frame;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WebApiClientTest01 window = new WebApiClientTest01();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void PushButton() {
		URL url;
		String ret="";

		try{
			//url = new URL("http://aqtor.co.jp/index.html");
			url = new URL("http://localhost:8080/servlet-samples/ServletTest01");

			if ( url != null ) {
				LogOut( "なんか帰ってきてるっぽい" );
			} else {
				LogOut( "ぬるぽ" );
			}

			//コネクションを開く。
			URLConnection con = url.openConnection();

			//接続先のデータを取得。
			InputStream is = con.getInputStream();

			//取得した文字列を文字列にして返す。
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			String line = "";

			while( (line = input.readLine() ) != null ) ret += line + "\n";

			// 結果をログ出力
			LogOut( ret );

		} catch( MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ログ一行出力（引数２つ）
	public void LogOut( String t, boolean b ) {
		if( b ) {
			textArea.setText( textArea.getText() + t + "\n" );
		} else {
			textArea.setText( textArea.getText() + t );
		}
	}

	// ログ一行出力（引数１つ）
	public void LogOut( String t ) {
		LogOut( t, true );
	}

	/**
	 * Create the application.
	 */
	public WebApiClientTest01() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 479, 661);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		Label label = new Label("すまほん");
		label.setBounds(207, 577, 79, 23);
		panel.add(label);

		Button button = new Button("問合せボタン");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				PushButton();
			}
		});
		button.setBounds(141, 416, 187, 56);
		panel.add(button);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 43, 356, 356);
		panel.add(scrollPane);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
	}
}
