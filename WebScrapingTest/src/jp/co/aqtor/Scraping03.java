// �������������őS�A�[�e�B�X�g���𒊏o����B

package jp.co.aqtor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;

public class Scraping03 implements ThreadInterface {
	private ScrapingThread th;
	private JFrame frame;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scraping03 window = new Scraping03();
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
	public Scraping03() {
		initialize();
	}

	private Iterator ite;
	private JButton btnNewButton_1;

	// �{�^���������̂ŃX�^�[�g
	public void PushButton() {
		// CSV�t�@�C���ǂݍ���
		CsvMapping csm = new CsvMapping();
		Map<String,String> map = csm.getCsvMap("artistindexlist.csv");
		ite = map.entrySet().iterator();

		// �Ăяo��
		ScrapingArtist();
		//StartDBWriteArtist();	// �e�X�g
	}

	// �u�A�[�e�B�X�g�v�S���o
	public void ScrapingArtist() {
		int i;

		// �z��`�F�b�N
		if( !ite.hasNext() ) {
			textArea.append("�����I\n");
			return;
		}

		// �}�b�v���猟�������ƃR�[�h���擾����
		Map.Entry entry = (Map.Entry)ite.next();
		String key = (String)entry.getKey();
		int val = Integer.valueOf((String)entry.getValue());

		// �X�N���C�s���O�E�X���b�h�J�n
		th = ScrapingArtist(key, val);
	}

	@Override
	public void Callback() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		ScrapingArtist();
		System.out.println("�R�[���o�b�N���s�B");
	}

	// �A�[�e�B�X�g�����i�C���f�b�N�X�ʁj
	public ScrapingThread ScrapingArtist(String str, int idx) {
		// �X���b�h���s
		ScrapingThread th = new ScrapingThread();		// ����new���Ďg���̂āB
		th.SetScrapingMode( ScrapingMode.ARTIST );
		th.SetScrapingIndex(str, idx);
		th.SetTextArea(textArea);
		th.registerCallback(this);
		th.SetDBWriteMode(true);
		th.start();		// �X���b�h�J�n

		textArea.append("�X���b�h�J�n�I str=[" + str + "], idx=[" + idx + "].\n");
		System.out.println("�X���b�h�J�n�I str=[" + str + "], idx=[" + idx + "].");
		return th;
	}

	// �ꎞ��~�{�^��
	public void SuspendButton() {
		if(th != null) {
			th.SuspendProcess();
		}	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 512, 595);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("\u5B9F\u884C\uFF01");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButton();
			}
		});
		btnNewButton.setBounds(123, 525, 91, 21);
		frame.getContentPane().add(btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 472, 426);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		btnNewButton_1 = new JButton("\u4E00\u6642\u505C\u6B62");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SuspendButton();
			}
		});
		btnNewButton_1.setBounds(314, 525, 91, 21);
		frame.getContentPane().add(btnNewButton_1);

		JLabel label = new JLabel("\u30A2\u30FC\u30C6\u30A3\u30B9\u30C8\u4E00\u89A7\u3092\u62BD\u51FA\u3059\u308B\u3063\u3059");
		label.setBounds(22, 446, 192, 13);
		frame.getContentPane().add(label);
	}
}
