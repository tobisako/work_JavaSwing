// こっちは「テストソース」です。
package aqtor.co.jp;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class TwitterTest01 {

	private JFrame frame;
	private JTextArea textArea;
	public Twitter tw;
	private JPanel panel;
	private String hogeurl;
	private BufferedImage image = null;
	private Canvas canvas;
	private JScrollPane scrollPane_1;
	private JList list;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TwitterTest01 window = new TwitterTest01();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

//	TwitterFactory twitterfactory = new TwitterFactory();

	// つぶやき開始処理とか。
	public void setAccessToken() {
		String consumerKey    = "***EKZwf3GZ8AZv2GJBpw";
		String consumerSecret = "***D8lMG8JYiPunuZDfyaLanTS71QoVWIuMDVKYX2Y";
		String accessToken    = "***44011-4gS6fOELdyDSCQJyG4O5OjYgTH0VHy42wFxLpW7F0";
		String accessSecret   = "***xybrT9hJZFJpCZGpXo6mXI2KbJ79nwC0ysdmG0I";

//		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
//		confbuilder.setOAuthConsumerKey(CONSUMERKEY);
//		confbuilder.setOAuthConsumerSecret(CONSUMERSECRET);
//		TwitterFactory twitterfactory = new TwitterFactory(confbuilder.build());
//		//Twitter twitter = twitterfactory.getOAuthAuthorizedInstance( new AccessToken(ACCESSTOKEN, ACCESSSECRET) );
//		Twitter twitter = twitterfactory.getInstance();
//		twitter.setOAuthConsumer( ACCESSTOKEN, ACCESSSECRET );

		// OAuth認証をせずにアクセストークンを入手した場合の例（デベロッパーテスト用と考えるべき）
		tw = new TwitterFactory().getInstance();
		AccessToken at = new AccessToken(accessToken, accessSecret);
		tw.setOAuthConsumer( consumerKey, consumerSecret );
		tw.setOAuthAccessToken(at);
		textArea.setText( "準備ＯＫ。" );
	}

	public void twitte() {
		try {
		    tw.updateStatus("（Javaからテスト）今日は良い天気だなぁ。");
		} catch (Exception e) {
		    e.printStackTrace();
		}
		textArea.setText( "つぶやいた。" );
	}

	public void tchiran() {
		try {
	        //TLの取得
	        ResponseList<Status> homeTl = tw.getHomeTimeline();

            //statuses = mTwitter.getHomeTimeline();  // 4

			for (twitter4j.Status status : homeTl) {

				textArea.setText( textArea.getText() + status.getUser().getScreenName() + "\n" );
				textArea.setText( textArea.getText() + status.getText() + "\n" );
				textArea.setText( textArea.getText() + status.getUser().getProfileImageURL().toString() + "\n" );
				hogeurl = status.getUser().getProfileImageURL().toString();

//				Status s = new Status();
//				s.setScreenName(status.getUser().getScreenName());
//				s.setProfileImageUrl(status.getUser().getProfileImageURL().toString());       // 5
//				s.setText(status.getText());
//				list.add(s);
			}
			textArea.setText( textArea.getText() + "最後にhoge:" + hogeurl + "\n" );
			gazou();

		} catch (TwitterException e) {
			e.printStackTrace();
			if(e.isCausedByNetworkIssue()){
				//Toast.makeText(getApplicationContext(), "ネットワークに接続して下さい", Toast.LENGTH_LONG);
				JOptionPane.showMessageDialog( frame, "ネットワークに接続して下さい");
			}else{
				//Toast.makeText(getApplicationContext(), "エラーが発生しました。", Toast.LENGTH_LONG);
				JOptionPane.showMessageDialog( frame, "エラーが発生しました。" );
			}
		}

		// JListに値を挿入
		{
			Vector<String> initData = new Vector<String>();

			StringBuffer sb;
		    for (int i = 0 ; i < 10 ; i++){
		      sb = new StringBuffer();
		      sb.append("リスト項目");
		      sb.append(i);
		      sb.append("番目");
		      initData.add(new String(sb));
		    }

		    list.setListData(initData);

		}


	}

	public void gazou() {
        image = null;
        try {
            image = ImageIO.read( new URL( hogeurl ) );
        } catch ( IOException ex ) {
            ex.printStackTrace();
    		textArea.setText( "例外発生。\n" );
        }
		textArea.setText( textArea.getText() + "イメージ展開完了。\n" );
		byouga();
	}

	public void byouga() {
		Graphics g = canvas.getGraphics();
        // イメージを展開する。
        Graphics2D g2 = ( Graphics2D ) g;
        g2.drawImage( image, 0, 0, 100, 100, canvas );
	}

	/**
	 * Create the application.
	 */
	public TwitterTest01() {
		initialize();

		//
	}

	public class Canv2 extends Canvas {
		public void paint(Graphics g) {
			textArea.setText( "うそだろ・・・" );
		}
	}

	public void Dro() {
		//ここでおもいきって再描画。
		textArea.setText( "くるか！？" );
		byouga();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 492);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JButton btnNewButton = new JButton("つぶやく");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				twitte();
			}
		});
		btnNewButton.setBounds(137, 427, 128, 21);
		panel.add(btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(71, 10, 359, 221);
		panel.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JButton btnNewButton_1 = new JButton("一覧を表示");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tchiran();
			}
		});
		btnNewButton_1.setBounds(292, 427, 138, 21);
		panel.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("準備");
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setAccessToken();
			}
		});
		btnNewButton_2.setBounds(12, 427, 91, 21);
		panel.add(btnNewButton_2);

		canvas = new Canvas();
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				//Dro();
			}
		});
		canvas.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				Dro();
			}
			@Override
			public void componentHidden(ComponentEvent arg0) {
				Dro();
			}
		});
		canvas.setIgnoreRepaint(true);
		canvas.setBounds(12, 237, 100, 75);
		panel.add(canvas);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(247, 258, 219, 132);
		panel.add(scrollPane_1);

		list = new JList();
		scrollPane_1.setViewportView(list);
	}
}

