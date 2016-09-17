// いろいろテスト用

package jp.co.aqtor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Scraping05 {

	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private JComboBox comboBox;
	private JButton btnNewButton_2;
	private JButton btnNewButton_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scraping05 window = new Scraping05();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Scraping05() {
		initialize();
	}

	// アーティスト一覧を抽出するッチュー話ですわ。いやほんま。
	private void PushArtistButton() {
		Connection con = DBManager.getConnection();
		String idx = (String)comboBox.getSelectedItem();
		String sql = "select artistid,name from artist where nameindex='" + idx + "'";

		textArea.append("get artist list.\n");
		textArea.append("[" + sql + "]\n");
		try {
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery( sql );

			int cnt = 0;
			while (result.next()) {
				textArea.append("[" + result.getString(1) + "][" + result.getString(2) + "]\n");
				textArea.setCaretPosition( textArea.getText().length() );
				cnt++;
			}
			textArea.append("num=[" + cnt + "]\n");

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// 楽曲一覧を抽出するです。
	private void PushGakkyokuButton() {
		Connection con = DBManager.getConnection();
		String idx = (String)comboBox.getSelectedItem();
		String sql = "select gakkyokuid,title from gakkyoku where titleindex='" + idx + "'";

		textArea.append("get gakkyoku list.\n");
		textArea.append("[" + sql + "]\n");
		try {
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery( sql );

			int cnt = 0;
			while (result.next()) {
				textArea.append("[" + result.getString(1) + "][" + result.getString(2) + "]\n");
				textArea.setCaretPosition( textArea.getText().length() );
				cnt++;
			}
			textArea.append("num=[" + cnt + "]\n");

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// アーティストＩＤを元に、楽曲一覧をピックアップするボタン
	private void PushGakkyokuFromArtistButton() {
		Connection con = DBManager.getConnection();
		String artistid = (String)textField.getText();
		String sql = "select gakkyokuid,title from gakkyoku where artistid=" + artistid;

		textArea.append("get gakkyoku list from artistID.\n");
		textArea.append("[" + sql + "]\n");

		try {
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery( sql );

			int cnt = 0;
			while (result.next()) {
				textArea.append("[" + result.getString(1) + "][" + result.getString(2) + "]\n");
				textArea.setCaretPosition( textArea.getText().length() );
				cnt++;
			}
			textArea.append("num=[" + cnt + "]\n");

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// 楽曲の書き込み実験
	private void PushJikkenButton() {
		Connection con = DBManager.getConnection();
		String sql = "INSERT INTO kara.gakkyoku (gakkyokuid, artistid, titleindex, title) " +
						"VALUES (77777, 123, 'っ', 'っほげほげさにさに')";
		String sql2 = "INSERT INTO kara.gakkyoku (gakkyokuid, artistid, titleindex, title) " +
						"VALUES (88888, 123, 'っ', 'っぴよぴよ')";

		// ＤＢ接続
		try {
			Statement stmt = con.createStatement();

			boolean bResult = stmt.execute( sql );
			textArea.append("DB-WRITE!(" + bResult + ")\n");
			textArea.setCaretPosition( textArea.getText().length() );

			bResult = stmt.execute( sql2 );
			textArea.append("DB-WRITE!(" + bResult + ")\n");

			stmt.close();

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			textArea.append("DB-WRITE-ERROR!(" + e + ")\n");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 551, 614);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 511, 404);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JButton btnNewButton = new JButton("\u30A2\u30FC\u30C6\u30A3\u30B9\u30C8\u4E00\u89A7");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushArtistButton();
			}
		});
		btnNewButton.setBounds(12, 484, 113, 21);
		frame.getContentPane().add(btnNewButton);

		textField = new JTextField();
		textField.setBounds(137, 455, 157, 19);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"\u3042", "\u3044", "\u3046", "\u3048", "\u304A", "\u304B", "\u304D", "\u304F", "\u3051", "\u3053", "\u3055", "\u3057", "\u3059", "\u305B", "\u305D", "\u305F", "\u3061", "\u3064", "\u3066", "\u3068", "\u306A", "\u306B", "\u306C", "\u306D", "\u306E", "\u306F", "\u3072", "\u3075", "\u3078", "\u307B", "\u307E", "\u307F", "\u3080", "\u3081", "\u3082", "\u3084", "\u3086", "\u3088", "\u3089", "\u308A", "\u308B", "\u308C", "\u308D", "\u308F", "\u304A", "\u3093", "\u304C", "\u304E", "\u3050", "\u3052", "\u3054", "\u3056", "\u3058", "\u305A", "\u305C", "\u305E", "\u3060", "\u3062", "\u3065", "\u3067", "\u3069", "\u3070", "\u3073", "\u3076", "\u3079", "\u307C", "\u3071", "\u3074", "\u3077", "\u307A", "\u307D", "\u3041", "\u3043", "\u3045", "\u3047", "\u3049", "\u3063"}));
		comboBox.setBounds(12, 455, 113, 19);
		frame.getContentPane().add(comboBox);

		JButton btnNewButton_1 = new JButton("\u30A2\u30FC\u30C6\u30A3\u30B9\u30C8\u5225\u697D\u66F2\u4E00\u89A7");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushGakkyokuFromArtistButton();
			}
		});
		btnNewButton_1.setBounds(137, 484, 157, 21);
		frame.getContentPane().add(btnNewButton_1);

		JLabel label = new JLabel("\u30C7\u30FC\u30BF\u30D9\u30FC\u30B9\u304B\u3089\u30C7\u30FC\u30BF\u3092\u5F15\u3063\u5F35\u3063\u3066\u304F\u308B\u30C6\u30B9\u30C8\u3002");
		label.setBounds(12, 424, 253, 13);
		frame.getContentPane().add(label);

		btnNewButton_2 = new JButton("\u697D\u66F2\u4E00\u89A7");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushGakkyokuButton();
			}
		});
		btnNewButton_2.setBounds(12, 515, 113, 21);
		frame.getContentPane().add(btnNewButton_2);

		btnNewButton_3 = new JButton("\u5B9F\u9A13");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushJikkenButton();
			}
		});
		btnNewButton_3.setBounds(373, 484, 91, 21);
		frame.getContentPane().add(btnNewButton_3);
	}
}
