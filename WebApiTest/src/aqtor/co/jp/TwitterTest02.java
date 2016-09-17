// こっちが「本番ソース」です。
package aqtor.co.jp;

import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterTest02 {

	public Twitter tw;
	Vector<String> imgListData = new Vector<String>();
	private BufferedImage image = null;
	private JFrame frmver;
	private JTextArea textArea;
	private JButton btnTuBuYaKuButton;
	private JButton btnGetTweetListButton;
	private JButton btnLogInButton;
	private JList list;
	private Canvas canvas;
	private JTextField textField;
	private Checkbox checkbox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TwitterTest02 window = new TwitterTest02();
					window.frmver.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// ログインボタン押下時の動作
	public void PushButtonLogin() {
		SetTextOneLine( "初期化中…", false );
		InitTwitter();

		checkbox.setEnabled(true);
		btnTuBuYaKuButton.setEnabled(true);
		btnGetTweetListButton.setEnabled(true);
		btnLogInButton.setEnabled(false);
		SetTextOneLine( "ＯＫ！" );
	}

	// 一覧取得ボタン押下時の動作
	public void PushButtonGetList() {
		SetTextOneLine( "リスト取得…", false );
		GetTweetList();

		SetTextOneLine( "ＤＯＮＥ！" );
	}

	// リストをクリックした時の動作
	public void ClickListItem() {
		int idx = list.getSelectedIndex();

		SetTextOneLine( "No." + idx + "の画像を取得…", false );
		getPicFromURL( imgListData.get( idx ) );

		SetTextOneLine( "描画…", false );
		drawImageForCanvas();

		SetTextOneLine( "完了。" );
	}

	// 「つぶやく」ボタン押下時の動作
	public void PushButtonTweet() {
		SetTextOneLine( "文章「" + textField.getText() + "」をつぶやきま…", false );
		DoTweet( textField.getText() );

		SetTextOneLine( "した。" );
	}

	// ツイッター初期化
	public void InitTwitter() {
		// コンシューマーキー・アクセストークン定義（とびさこ固有情報）
		String consumerKey    = "***EKZwf3GZ8AZv2GJBpw";
		String consumerSecret = "***D8lMG8JYiPunuZDfyaLanTS71QoVWIuMDVKYX2Y";
		String accessToken    = "***44011-4gS6fOELdyDSCQJyG4O5OjYgTH0VHy42wFxLpW7F0";
		String accessSecret   = "***xybrT9hJZFJpCZGpXo6mXI2KbJ79nwC0ysdmG0I";

		// OAuth認証をせずにアクセストークンを入手した場合の例（デベロッパーテスト用と考えるべき）
		tw = new TwitterFactory().getInstance();
		AccessToken at = new AccessToken(accessToken, accessSecret);
		tw.setOAuthConsumer( consumerKey, consumerSecret );
		tw.setOAuthAccessToken(at);
	}

	// ツイートする
	public void DoTweet( String t ) {
		try {
		    tw.updateStatus( t );
		} catch (Exception e) {
		    e.printStackTrace();
		    SetTextOneLine( "例外！" );
		}
	}

	// ツイートを取得する
	public void GetTweetList() {
		try {
			ResponseList<Status> homeTl;
			Vector<String> initListData = new Vector<String>();
			StringBuffer sb;
			imgListData.removeAllElements();

			// タイムラインの取得（チェックボックスにより動作を変える）
			if( checkbox.getState() ) {
				homeTl = tw.getUserTimeline();
			} else {
		        homeTl = tw.getHomeTimeline();
			}

			// タイムラインを分解する
			for (twitter4j.Status status : homeTl) {
				sb = new StringBuffer();
				sb.append( "[" );
				sb.append( status.getUser().getScreenName() );
				sb.append( "] " );
				sb.append( status.getUser().getLocation() );
				sb.append( " : " );
				sb.append( status.getText() );
				initListData.add(new String(sb));
				imgListData.add(new String( status.getUser().getProfileImageURL().toString() ));
			}
		    list.setListData(initListData);
		} catch (TwitterException e) {
			e.printStackTrace();
			if(e.isCausedByNetworkIssue()){
				//Toast.makeText(getApplicationContext(), "ネットワークに接続して下さい", Toast.LENGTH_LONG);
				JOptionPane.showMessageDialog( frmver, "ネットワークに接続して下さい");
			}else{
				//Toast.makeText(getApplicationContext(), "エラーが発生しました。", Toast.LENGTH_LONG);
				JOptionPane.showMessageDialog( frmver, "エラーが発生しました。" );
			}
		}
	}

	// 画像ＵＲＬから画像を取得
	public void getPicFromURL( String turl ) {
		image = null;
		try {
			image = ImageIO.read( new URL( turl ) );
		} catch ( IOException ex ) {
			ex.printStackTrace();
			SetTextOneLine( "例外発生!" );
		}
	}

	// 画像イメージをキャンバスに描画
	public void drawImageForCanvas() {
		Graphics g = canvas.getGraphics();
        Graphics2D g2 = ( Graphics2D ) g;

        g2.drawImage( image, 0, 0, 100, 100, canvas );		// イメージをキャンパスへ展開する。
	}

	// ステータス表示テキスト窓へ１行追加関数
	public void SetTextOneLine( String t, boolean b ) {
		if( b ) {
			textArea.setText( textArea.getText() + t + "\n" );
		} else {
			textArea.setText( textArea.getText() + t );
		}
	}
	public void SetTextOneLine( String t ) {
		SetTextOneLine( t, true );
	}

	/**
	 * Create the application.
	 */
	public TwitterTest02() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmver = new JFrame();
		frmver.setTitle("ツイッターで一覧見たりつぶやいたりするだけVer1.0");
		frmver.setBounds(100, 100, 665, 479);
		frmver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmver.getContentPane().setLayout(null);

		btnLogInButton = new JButton("ログイン");
		btnLogInButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				PushButtonLogin();	// ログイン処理を実行する
			}
		});
		btnLogInButton.setBounds(12, 375, 137, 53);
		frmver.getContentPane().add(btnLogInButton);

		Label label = new Label("ステータス");
		label.setBounds(10, 10, 168, 17);
		frmver.getContentPane().add(label);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 28, 304, 99);
		frmver.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 11));
		scrollPane.setViewportView(textArea);

		textField = new JTextField();
		textField.setBounds(342, 27, 303, 37);
		frmver.getContentPane().add(textField);
		textField.setColumns(10);

		Label label_1 = new Label("つぶやく？");
		label_1.setBounds(342, 10, 168, 17);
		frmver.getContentPane().add(label_1);

		btnTuBuYaKuButton = new JButton("つぶやくボタン");
		btnTuBuYaKuButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PushButtonTweet();
			}
		});
		btnTuBuYaKuButton.setEnabled(false);
		btnTuBuYaKuButton.setBounds(508, 74, 137, 32);
		frmver.getContentPane().add(btnTuBuYaKuButton);

		Label label_2 = new Label("一覧欲しい？");
		label_2.setBounds(12, 153, 168, 17);
		frmver.getContentPane().add(label_2);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(10, 176, 635, 193);
		frmver.getContentPane().add(scrollPane_1);

		list = new JList();
		list.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 12));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ClickListItem();	//test2();
			}
		});
		scrollPane_1.setViewportView(list);

		btnGetTweetListButton = new JButton("一覧ゲットボタン");
		btnGetTweetListButton.setEnabled(false);
		btnGetTweetListButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PushButtonGetList();
			}
		});
		btnGetTweetListButton.setBounds(462, 383, 183, 37);
		frmver.getContentPane().add(btnGetTweetListButton);

						canvas = new Canvas();
						canvas.setIgnoreRepaint(true);
						canvas.setForeground(Color.RED);
						canvas.setBounds(342, 70, 117, 100);
						frmver.getContentPane().add(canvas);

						checkbox = new Checkbox("俺だけチェック");
						checkbox.setEnabled(false);
						checkbox.setBounds(544, 153, 101, 23);
						frmver.getContentPane().add(checkbox);
	}
}
