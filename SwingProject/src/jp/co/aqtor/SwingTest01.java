package jp.co.aqtor;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class SwingTest01 implements ActionListener {

	private JFrame frame;
	private JPanel panel;
	private JLabel lbl;
	private JPersonUnitLabel pl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingTest01 window = new SwingTest01();
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
	public SwingTest01() {
		initialize();
	}

	// JLabeに画像を張り付ける。
	void SetLabel() {
		ImageIcon img = new ImageIcon( "res/hito_a_32x48.png");
		JLabel lbl = new JLabel( img );
		lbl.setBounds( 100, 100, 48, 48 );
		panel.add(lbl);
		frame.repaint();

		LineBorder border = new LineBorder(Color.RED, 1, false);
		lbl.setBorder(border);
	}

	/**
	 *	画像の「一部」を切り出して表示する方法
	 **/
	void SetLabel2() {
			File ff = new File( "res/vx_chara02_a.png");
			BufferedImage bs;
			BufferedImage bd;

			try {
				bs = javax.imageio.ImageIO.read( ff );
				bd = bs.getSubimage( 32 * 5,  48 * 5,  32,  48 );
				ImageIcon img = new ImageIcon( bd );
				lbl = new JLabel( img );
				lbl.setBackground(Color.GREEN);
				lbl.setOpaque(false);
				lbl.setBounds( 0, 0, 100, 100 );
				panel_1.add(lbl);
				frame.repaint();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
	}

	/**
	 *	ラベル画像を変更する。
	 **/
	void ChangeLabelImage() {
		ImageIcon img = new ImageIcon( "res/hito_a_32x48.png");
		lbl.setIcon(img);
		lbl.setBounds( 100, 100, 48, 48 );
		frame.repaint();
	}

	void InitMap() {
		//HaichiChara( 1, 1 );
		HaichiChara( 1, 1 );
	}

	// タイマースタート。
	public void StartTimer() {
		Timer timer = new Timer(300, this);
		timer.start();
	}

	void HaichiChara(int x, int y) {
		pl = new JPersonUnitLabel( 0 );
		pl.setPosition(10, 20);
		//		pl.setSize( 100, 100 );
		panel.add(pl);
		frame.repaint();
		StartTimer();
	}

	// キャラが動く！
	void MoveChara( int x, int y ) {
		if( pl != null ) {
			pl.setPosition(x, y);
			frame.repaint();
		}
	}

	private int dire = 0;
	// キャラの向きが変わる！
	void ChangeCharaDirection( boolean flg) {
		if( flg == true ) {
			// 向きを変える
			if( ++dire > 3) dire = 0;
		} else {
			// モーションを変える
			pl.ChangeMotion();
		}
		pl.ChangeDirection( dire );
	}

	private int chara = 0;
	private JPanel panel_1;

	// キャラを変更する！！
	void ChangeCharaKind() {
		if( ++chara > 7 ) chara = 0;
		pl.SelectChara( chara );
		frame.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		ChangeCharaDirection( false );	// アニメーション
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 456, 407);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		panel = new JPanel();
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				switch( paramMouseEvent.getButton() ) {
				case MouseEvent.BUTTON1:
					ChangeCharaDirection( true );
					break;
				case MouseEvent.BUTTON3:
					ChangeCharaDirection( false );
					ChangeCharaKind();
					break;
				default:
					ChangeCharaKind();
				}
			}
		});
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				MoveChara( arg0.getX(), arg0.getY() );
			}
		});
		panel.setBounds(12, 10, 328, 233);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		panel_1 = new JPanel();
		panel_1.setBounds(351, 10, 77, 83);
		frame.getContentPane().add(panel_1);

		JButton btnNewButton = new JButton("PUT");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitMap();
			}
		});
		btnNewButton.setBounds(12, 253, 129, 21);
		frame.getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("切り出し");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetLabel2();
			}
		});
		btnNewButton_1.setBounds(352, 103, 76, 21);
		frame.getContentPane().add(btnNewButton_1);

		JLabel lblNewLabel = new JLabel("左クリックでキャラの向きを変えます。");
		lblNewLabel.setBounds(12, 305, 328, 13);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblPut = new JLabel("PUTボタンを押して、マウスをWindow中心部に移動して下さい。");
		lblPut.setBounds(12, 284, 408, 13);
		frame.getContentPane().add(lblPut);

		JLabel lblNewLabel_2 = new JLabel("右クリックで、キャラを変更します。");
		lblNewLabel_2.setBounds(12, 325, 408, 13);
		frame.getContentPane().add(lblNewLabel_2);
	}
}
