package jp.co.aqtor;

import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JRadioButton;

// �W���C�T�E���h���X�N���C�s���O���悤 by R.Tobisako
public class Scraping01 implements ActionListener {
	private ScrapingThread th;
	private ButtonGroup group;
	private Timer timer;
	private JFrame frmForSoysound;
	private JTextArea textArea;
	private JTextField textField;
	private JComboBox comboBox;
	private JRadioButton rdbtnR1;
	private JRadioButton rdbtnR2;
	private JRadioButton rdbtnR3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scraping01 window = new Scraping01();
					window.frmForSoysound.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Scraping01() {
		initialize();
		// �{�^�����O���[�v������ł��B
		group = new ButtonGroup();
		group.add(rdbtnR1);
		group.add(rdbtnR2);
		group.add(rdbtnR3);

		JLabel label = new JLabel("\u30B9\u30EC\u30C3\u30C9\u304C\u52D5\u3044\u3066\u308B\u3088\u3093\u30A4\u30F3\u30B8\u30B1\u30FC\u30BF\u30FC");
		label.setBounds(12, 529, 237, 13);
		frmForSoysound.getContentPane().add(label);

		GoTimer();
	}

	// �^�C�}�J�n
	public void GoTimer() {
		timer = new Timer(200, this);
		timer.start();
	}

	// �^�C�}�������ɌĂяo�����֐�
	public void actionPerformed(ActionEvent e) {
		// �X���b�h����󋵂��`�F�b�N
		if( th != null && th.isAlive()) {
			// �ꎞ��~���H
			if(!th.isSuspendProcess()) {
				// �X���b�h���s���I�C���W�P�[�^�[���A�j���[�V��������
				if(rdbtnR1.isSelected()) {
					rdbtnR2.setSelected(true);
				} else if(rdbtnR2.isSelected()) {
					rdbtnR3.setSelected(true);
				} else {
					rdbtnR1.setSelected(true);
				}
			}
		} else {
			// �X���b�h��~��ԁB�C���W�P�[�^�[���N���A�ɂ���B
			group.clearSelection();
		}
	}

	// �X�N���C�s���O�u�A�[�e�B�X�g�v
	public void PushButoon_ScrapingArtist() {
		// ���d�N���`�F�b�N
		if(th != null && th.isAlive()) {
			JOptionPane.showMessageDialog(frmForSoysound, "���A�����������Ă�˂��c�I���܂ő҂��āB�B");
			return;
		}

		// �X���b�h���s
		th = new ScrapingThread();		// ����new���Ďg���̂āB
		th.SetScrapingMode( ScrapingMode.ARTIST );
		th.SetScrapingIndex(comboBox.getSelectedIndex() + 101);
//		th.SetScrapingIndex(176);
		th.SetTextArea(textArea);
		th.start();		// �X���b�h�J�n
	}

	// �X�N���C�s���O�u�y�ȁv
	public void PushButton_Gakkyoku() {
		// ���d�N���`�F�b�N
		if(th != null && th.isAlive()) {
			JOptionPane.showMessageDialog(frmForSoysound, "���A������������Ă鏊��˂񂯂ǁc�B");
			return;
		}

		// �A�[�e�B�X�g�h�c�t�B�[���h�`�F�b�N
		if(textField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(frmForSoysound, "�A�[�e�B�X�g�h�c����͂��Ă�c�B");
			return;
		}

		// �X���b�h���s
		th = new ScrapingThread();		// ����new���Ďg���̂āB
		th.SetScrapingMode( ScrapingMode.GAKKYOKU );
		th.SetScrapingIndex(comboBox.getSelectedIndex());
		th.SetTextArea(textArea);
		th.SetArtistID(textField.getText());
		th.start();		// �X���b�h�J�n
	}

	// �\���G���A�������{�^��
	public void PushButtonClear() {
		textArea.setText("");
	}

	// �ً}��~�{�^��
	public void PushButtonStop() {
		if(th != null) {
			th.StopThread();
		}
	}

	// �ꎞ��~�E�ĊJ�{�^��
	public void PushButtonSuspend() {
		if(th != null) {
			th.SuspendProcess();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmForSoysound = new JFrame();
		frmForSoysound.setTitle("\u30B9\u30AF\u30EC\u30A4\u30D4\u30F3\u30B0 for SOYSOUND WEB\u30B5\u30A4\u30C8");
		frmForSoysound.setBounds(100, 100, 578, 601);
		frmForSoysound.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmForSoysound.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("\u30A2\u30FC\u30C6\u30A3\u30B9\u30C8\u540D\u306E\u691C\u7D22\uFF01");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButoon_ScrapingArtist();
			}
		});
		btnNewButton.setBounds(101, 415, 228, 35);
		frmForSoysound.getContentPane().add(btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 8, 538, 374);
		frmForSoysound.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		JButton btnNewButton_2 = new JButton("\u66F2\u540D\u306E\u691C\u7D22\uFF08\u30AB\u30E9\u30AA\u30B1\u306E\uFF09");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButton_Gakkyoku();
			}
		});
		btnNewButton_2.setBounds(119, 473, 196, 35);
		frmForSoysound.getContentPane().add(btnNewButton_2);

		textField = new JTextField();
		textField.setBounds(12, 478, 96, 25);
		frmForSoysound.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblArtistid = new JLabel("artistID");
		lblArtistid.setBounds(12, 460, 123, 19);
		frmForSoysound.getContentPane().add(lblArtistid);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"\u3042", "\u3044", "\u3046", "\u3048", "\u304A", "\u304B", "\u304D", "\u304F", "\u3051", "\u3053", "\u3041"}));
		comboBox.setBounds(12, 420, 77, 25);
		frmForSoysound.getContentPane().add(comboBox);

		JLabel label_1 = new JLabel("\u3042\u3044\u3046\u3048\u304A\u691C\u7D22\u3002");
		label_1.setBounds(12, 392, 113, 18);
		frmForSoysound.getContentPane().add(label_1);

		JButton btnNewButton_3 = new JButton("\u30E1\u30C3\u30BB\u30FC\u30B8\u30DC\u30C3\u30AF\u30B9\u30AF\u30EA\u30A2");
		btnNewButton_3.setFont(new Font("MS UI Gothic", Font.PLAIN, 10));
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButtonClear();
			}
		});
		btnNewButton_3.setBounds(420, 392, 130, 35);
		frmForSoysound.getContentPane().add(btnNewButton_3);

		JButton btnNewButton_1 = new JButton("\u505C\u6B62\u30DC\u30BF\u30F3");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButtonStop();
			}
		});
		btnNewButton_1.setBounds(427, 518, 123, 35);
		frmForSoysound.getContentPane().add(btnNewButton_1);

		JButton btnNewButton_4 = new JButton("\u4E00\u6642\u505C\u6B62\u30FB\u518D\u958B\u30DC\u30BF\u30F3");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PushButtonSuspend();
			}
		});
		btnNewButton_4.setBounds(355, 463, 177, 21);
		frmForSoysound.getContentPane().add(btnNewButton_4);

		rdbtnR1 = new JRadioButton("");
		rdbtnR1.setEnabled(false);
		rdbtnR1.setBounds(17, 542, 21, 21);
		frmForSoysound.getContentPane().add(rdbtnR1);

		rdbtnR2 = new JRadioButton("");
		rdbtnR2.setEnabled(false);
		rdbtnR2.setBounds(43, 542, 21, 21);
		frmForSoysound.getContentPane().add(rdbtnR2);

		rdbtnR3 = new JRadioButton("");
		rdbtnR3.setEnabled(false);
		rdbtnR3.setBounds(68, 542, 21, 21);
		frmForSoysound.getContentPane().add(rdbtnR3);
	}
}


/* �X�N���C�s���O�E�w��I�t�Z�b�g����擾����B
public int ScrapingBody(Htmlparsercallback htmlpc, String urlstr, int offset) {
	URL url;
	HttpURLConnection httpoc;
	BufferedReader bstr;
	int cnt = offset;

	try {
		// 1.URL�I�u�W�F�N�g�̐����@�䂸�iJoysound�j
		urlstr += String.format( "&offset=%d", offset );

		textArea.setText(textArea.getText() + "URL:" + urlstr + "\n");

		// url = new URL("http://joysound.com/ex/search/artist.htm?artistId=4383");
		url = new URL(urlstr);

		// 2.�ڑ��I�u�W�F�N�g�̎擾
		httpoc = (HttpURLConnection)url.openConnection();

		// 3.�ڑ�
		httpoc.connect();

		bstr = new BufferedReader(new InputStreamReader(httpoc.getInputStream(), "UTF-8"));

	/ *	String str;
		while ( (str=bstr.readLine())!= null ){
			System.out.println(str);
		}* /

		// ��͏���
		ParserDelegator pd = new ParserDelegator();
		pd.parse(bstr, htmlpc, true);
		httpoc.disconnect();
//		System.out.println(SearchStr);
		bstr.close();
		httpoc.disconnect();

		cnt = htmlpc.GetCount();

		ArrayList<Artist> artistArray = htmlpc.GetArtistArray();
		for(int i = 0; i < artistArray.size(); i++) {
			textArea.setText( textArea.getText() + artistArray.get(i).GetArtistId() +
								artistArray.get(i).GetArtistName() + "\n" );
		}

		// textArea.setText( textArea.getText() + htmlpc.getStr() );
		textArea.setText( textArea.getText() + "cnt = " + cnt );

	} catch (Exception e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

	return cnt;
}
	int MaxIndex;

	// �X�N���C�s���O�E�S�A�[�e�B�X�g���擾����
	public void ScrapingArtistList() {
		ScrapingParserCallback htmlpc = new ScrapingParserCallback();
		int currentNum = 0;
		int loopcnt = 0;

		// URL����
		// �ucharIndexKbn=01�v�E�E�E���������������AABC�����B
		// �ucharIndex1=101�v�c����B
		// �uoffset=0�v�E�E�E�I�t�Z�b�g�B
//		String url = "http://joysound.com/ex/search/artistsearchindex.htm?searchType=02&searchWordType=2&charIndexKbn=01&charIndex1=101";
		String url = "http://joysound.com/ex/search/artistsearchindex.htm?searchType=02&searchWordType=2";
		url += "&charIndexKbn=01&charIndex1=129";
//		url += "&charIndexKbn=01&charIndex1=101";

		do {
			// �w��URL�̃A�[�e�B�X�g�������ׂĒ��o����B
//			currentNum = ScrapingBody(htmlpc, url, currentNum);

			loopcnt ++;
			if( loopcnt > 5 ) {
				sleep(1000);	// �P�b�E�F�C�g
				loopcnt = 0;
			}

			// �܂��A�P���ڂŁA�u�S���X�g�v���擾����B
		} while( htmlpc.GetMaxHit() > currentNum );
//		} while( test < 3 );

		textArea.setText(textArea.getText() + "Done. All Count = " + currentNum + "\n");
		textArea.setText(textArea.getText() + "Max Hit = " + htmlpc.GetMaxHit() + "\n");
	} */
