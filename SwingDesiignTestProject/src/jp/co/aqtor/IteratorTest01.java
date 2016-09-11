package jp.co.aqtor;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class IteratorTest01 {

	private JFrame frame;
	private List ls;
	private int x = 0, y = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IteratorTest01 window = new IteratorTest01();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// List・・・重複要素を許可。要素の順番を保持。
	// Set・・・重複要素は持たない。
	// Map・・・キーと値が対になった要素を持つ。キーの重複は許可されず、
	// 　　　　　各キーは１つの値のみに対応付けられる。

	// コレクション・フレームワーク－２．List
	// http://www.javaroad.jp/java_collection2.htm
	public void PushButton() {
		JLabel lbl = new JLabel("[JLabelです]");
		lbl.setBounds( 10, y, 100, 20 );
		frame.getContentPane().add(lbl);
		ls.add( lbl );
		y += 25;
		x += 2;
		frame.repaint();
	}

	// 全ラベルを検索して動かす。
	public void PushButton2() {
		JLabel ltmp;
		Rectangle r;

		int size = ls.size();
		for( int i = 0; i < size; i++ ) {
			ltmp = (JLabel)ls.get( i );
			r = ltmp.getBounds();
			r.x += 10;
			ltmp.setBounds( r );
		}
		frame.repaint();
	}

	/**
	 * Create the application.
	 */
	public IteratorTest01() {
		initialize();
		ls = new ArrayList();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 485, 488);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("PUT");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButton();
			}
		});
		btnNewButton.setBounds(30, 396, 122, 44);
		frame.getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("MOVE");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButton2();
			}
		});
		btnNewButton_1.setBounds(212, 393, 134, 51);
		frame.getContentPane().add(btnNewButton_1);
	}
}
