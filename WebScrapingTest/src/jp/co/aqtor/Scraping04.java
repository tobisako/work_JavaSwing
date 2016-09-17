// �������������őS�y�Ȃ𒊏o����B

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
		// CSV�t�@�C���ǂݍ���
		CsvMapping csm = new CsvMapping();
		Map<String,String> map = csm.getCsvMap("gakkyokuindexlist.csv");
		ite = map.entrySet().iterator();

		// �Ăяo��
		ScrapingAllGakkyoku();
	}

	// �u�y�ȁv�S���o
	public void ScrapingAllGakkyoku() {
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

		//ScrapingGakkyoku("��", 158);	// 158-��
		// �X�N���C�s���O�E�X���b�h�J�n
		th = ScrapingGakkyoku(key, val);
	}

	@Override
	public void Callback() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		ScrapingAllGakkyoku();
		System.out.println("�R�[���o�b�N���s�B");
	}

	// �y�Ȍ����i�A�[�e�B�X�g�h�c�ʁj
	public ScrapingThread ScrapingGakkyoku(String str, int idx) {
		// �X���b�h���s
		ScrapingThread th = new ScrapingThread();		// ����new���Ďg���̂āB
		th.SetScrapingMode( ScrapingMode.GAKKYOKU );
		th.SetScrapingIndex(str, idx);
		th.SetTextArea(textArea);
		th.registerCallback(this);
		th.SetDBWriteMode(true);
		th.start();		// �X���b�h�J�n

		textArea.append("�X���b�h�J�n�I str=[" + str + "], ArtistID=[" + idx + "].\n");
		System.out.println("�X���b�h�J�n�I str=[" + str + "], idx=[" + idx + "].");
		return th;
	}

	// �ꎞ��~�{�^��
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
