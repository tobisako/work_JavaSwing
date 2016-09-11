package jp.co.aqtor;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class KeyTestForm implements ActionListener {
	private Timer timer;
	private KeyListenerCls lis;
	private int x, y, w, h;
	private JLabel lala;
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KeyTestForm window = new KeyTestForm();
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
	public KeyTestForm() {
		initialize();

		// タイマー開始。
		timer = new Timer(200, this);
		timer.start();

		// キーリスナークラスの生成
		lis = new KeyListenerCls(this);
		frame.addKeyListener(lis);

		// 初回ラベル生成
		x = 50;
		y = 100;
		w = 90;
		h = 20;

		lala = new JLabel("[LABEL]");
		lala.setBounds(x, y, w, h);
		frame.getContentPane().add(lala);
		frame.repaint();
	}

	public void MoveLabel(int vx, int vy) {
		x = x + vx;
		y = y + vy;
		lala.setBounds(x, y, w, h);
		frame.repaint();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 379, 246);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("キーボードの上下左右ボタンで、ラベルが動きます。");
		lblNewLabel.setBounds(12, 10, 410, 13);
		frame.getContentPane().add(lblNewLabel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}
}

