// あいうえお順で全楽曲を抽出する。

package jp.co.aqtor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.JLabel;

public class Scraping04 implements ThreadInterface {
	private ScrapingThread th;
	private Iterator ite;
	private JFrame frame;
	private JTextArea textArea;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scraping04 window = new Scraping04();
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
	public Scraping04() {
		initialize();
	}

	public void PushButton() {
		// CSVファイル読み込み
		CsvMapping csm = new CsvMapping();
		Map<String,String> map = csm.getCsvMap("gakkyokuindexlist.csv");
		ite = map.entrySet().iterator();

		// 呼び出し
		ScrapingAllGakkyoku();
	}

	// 「楽曲」全抽出
	public void ScrapingAllGakkyoku() {
		int i;

		// 配列チェック
		if( !ite.hasNext() ) {
			textArea.append("完了！\n");
			return;
		}

		// マップから検索文字とコードを取得する
		Map.Entry entry = (Map.Entry)ite.next();
		String key = (String)entry.getKey();
		int val = Integer.valueOf((String)entry.getValue());

		//ScrapingGakkyoku("ず", 158);	// 158-ず
		// スクレイピング・スレッド開始
		th = ScrapingGakkyoku(key, val);
	}

	@Override
	public void Callback() {
		// TODO 自動生成されたメソッド・スタブ
		ScrapingAllGakkyoku();
		System.out.println("コールバック実行。");
	}

	// 楽曲検索（アーティストＩＤ別）
	public ScrapingThread ScrapingGakkyoku(String str, int idx) {
		// スレッド実行
		ScrapingThread th = new ScrapingThread();		// 毎回newして使い捨て。
		th.SetScrapingMode( ScrapingMode.GAKKYOKU );
		th.SetScrapingIndex(str, idx);
		th.SetTextArea(textArea);
		th.registerCallback(this);
		th.SetDBWriteMode(true);
		th.start();		// スレッド開始

		textArea.append("スレッド開始！ str=[" + str + "], ArtistID=[" + idx + "].\n");
		System.out.println("スレッド開始！ str=[" + str + "], idx=[" + idx + "].");
		return th;
	}

	// 一時停止ボタン
	public void SuspendButton() {
		if(th != null) {
			th.SuspendProcess();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 563, 625);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("\u5B9F\u884C");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButton();
			}
		});
		btnNewButton.setBounds(178, 555, 91, 21);
		frame.getContentPane().add(btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 523, 434);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		textField = new JTextField();
		textField.setText("200193");
		textField.setBounds(12, 454, 131, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnNewButton_1 = new JButton("\u4E00\u6642\u505C\u6B62");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SuspendButton();
			}
		});
		btnNewButton_1.setBounds(378, 555, 91, 21);
		frame.getContentPane().add(btnNewButton_1);

		JLabel lblNewLabel = new JLabel("\u697D\u66F2\u3092\u3042\u3044\u3046\u3048\u304A\u9806\u306B\u53D6\u5F97\u3057\u3066\u3044\u304F\u51E6\u7406\u3002");
		lblNewLabel.setBounds(22, 485, 426, 21);
		frame.getContentPane().add(lblNewLabel);
	}
}
