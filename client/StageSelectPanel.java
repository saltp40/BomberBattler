package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import stage.LoadImages;

// ステージセレクト画面
// パネルタイプ: 5
class StageSelectPanel extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Image stage[];   // ステージビュー画像を格納
	private Graphics buffer;
	private Dimension size;
	private JButton returnButton;     // 戻るセレクトボタン

	private JLabel stageTitle;
	private String[] stageTitleName = {"ステージ１", "ステージ２",
			"ステージ３"};
	private JLabel[] lname;   // 参加者の名前
	private JLabel[] lwin; // 参加者の勝利数
	private JLabel helpLabel;  // 操作キーの説明
	private JTextField commentField;  // コメント入力エリア
	private JTextArea commentArea;  // コメント表示エリア
	private JLabel comment;  // コメントラベル
	private JScrollPane scrollpane; // スクロールパネル
	private JButton submitButton;  // コメントの送信ボタン

	private int stageMax = stageTitleName.length;  // 最大のステージ数
	private int max=4;  // 参加者の最大人数
	private int selectStageNum = 0; // 現在選択されているステージの番号

	public StageSelectPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		// 背景画像のロード
		backImage = LoadImages.getBackGround(2);

		// ステージビュー画像のロード
		stage = LoadImages.getStageView();

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// 使用するボタンの作成
		returnButton = new JButton("←戻る");
		returnButton.addActionListener(new returnActionListener());
		returnButton.setBounds(10, 10, 90, 30);
		add(returnButton);

		// labelの作成
		stageTitle = new JLabel("ステージ１");
		stageTitle.setBounds(400, 70, 150, 40);
		add(stageTitle);

		lname = new JLabel[max];

		for(int i=0; i<max; i++){
			lname[i] = new JLabel("---");
			lname[i].setBounds(50, 60+60*i, 90, 30);
			add(lname[i]);
		}

		lwin = new JLabel[max];

		for(int i=0; i<max; i++){
			lwin[i] = new JLabel("---");
			lwin[i].setBounds(50, 80+60*i, 200, 30);
			add(lwin[i]);
		}

		helpLabel = new JLabel("ステージ選択: ←→　　決定: Z 　アイテム: SPACE");
		helpLabel.setBounds(300, 370, 400, 30);
		add(helpLabel);

		// コメント表示エリアの作成
		commentArea = new JTextArea();
		commentArea.setBounds(20, 320, 250, 120);
		commentArea.setEditable(false);  // 編集不可にする
		scrollpane = new JScrollPane(commentArea); // スクロールバーをつける
		scrollpane.setBounds(20, 320, 250, 120);
		add(scrollpane);

		// コメント入力エリアの作成
		commentField = new JTextField("ここにコメントを入力");
		commentField.setBounds(300, 410, 230, 30);
		commentField.addMouseListener(new myCommentListener());
		commentField.addKeyListener(new myCommentKeyListener());
		add(commentField);

		// コメント送信ボタンの作成
		submitButton = new JButton("送信");
		submitButton.addActionListener(new submitActionListener());
		submitButton.setBounds(540, 410, 60, 30);
		add(submitButton);

		// コメントラベルの作成
		comment =  new JLabel("COMMENT");
		comment.setBounds(20, 300, 130, 30);
		add(comment);

		// マウスリスナーの追加
		this.addMouseListener(new myMouseListener());
	}

	// returnボタンを押したときのアクション
	class returnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(2);
		}
	}

	// submitボタンを押したときのアクション
	class submitActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());

			// 入力したコメントを送信
			String str = commentField.getText();
			if(str!=null && !str.equals("ここにコメントを入力")){
				commentField.setText("ここにコメントを入力");
				mf.getRecieveThread().sendString("COMMENT "+ str);
			}
			mf.requestFocusInWindow(); // メインフレームにフォーカスする
		}
	}

	// マウスの判定
	public class myMouseListener extends MouseAdapter{
	    public void mouseClicked(MouseEvent e){
	    	MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
	    	mf.requestFocusInWindow(); // メインフレームにフォーカスする
	    	commentField.setText("ここにコメントを入力");
	    }
	}

	// コメント入力時のマウスの判定
	public class myCommentListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			commentField.setText("");
		}
	}

	// コメントキーリスナーの判定
	public class myCommentKeyListener implements KeyListener {

		// キーが押されたときの処理
		public void keyPressed(KeyEvent e) {

			// エンターが押されたとき
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
				String str = commentField.getText();
				commentField.setText("");

				mf.getRecieveThread().sendString("COMMENT "+ str);
			}
		}

		// キーが離されたときの処理
		public void keyReleased(KeyEvent e) {}

		// キーがタイプされたときの処理
		public void keyTyped(KeyEvent e) {}
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 5;
	}

	// コメントをセットする
	public void setComment(String com, String name){
		String crlf = System.getProperty("line.separator");  // 改行コードの取得
		String mess = name + ": " + com + crlf;
		commentArea.append(mess);
		commentArea.setCaretPosition(commentArea.getDocument().getLength());  // コメント追加時に自動的にスクロール
	}

	// メンバーの情報を設定
	public void setMemberLabel(int n, String name, int win){
		if(n < max){
			if(name.equals("NULL")) lname[n].setText("---");
			else{
				lname[n].setText((n+1) + "P: " + name);

				//  勝利数に応じて表示をかえる
				String str = "";
				if(win > 30) str = "☆×" + win + ": You're king!!";
				else if(win > 20) str = "☆×" + win + ": You're strong!!";
				else if(win > 10) str = "☆×" + win + ": 超エキサイティング!!";
				else if(win > 5) str = "☆×" + win;
				else for(int i=0; i<win; i++) str += "☆";
				lwin[n].setText(str);
			}
		}
	}

	// ステージ番号を返す
	public int getStageNum(){
		return selectStageNum;
	}

	// ステージ画像＆名前を切り替える
	public void setStageImage(int n){
		selectStageNum = (selectStageNum + n) % stageMax;
		if(selectStageNum < 0)
			selectStageNum = stageMax -1;
		stageTitle.setText(stageTitleName[selectStageNum]);
		repaint();
	}

//	// パネルの移動
//	public void inimGameSelP2(int sn, long l){
//		MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor(this);
//		mf.newPanel3(6, l, sn);
//	}

	// パネルの描写
	public void paintComponent(Graphics g){
		if (back==null)     return;
        buffer= back.getGraphics();
        if (buffer==null)   return;
		size = getSize();
        buffer.setColor(getBackground());
        buffer.fillRect(0, 0, size.width, size.height);

        // 背景の描写
        if(backImage!=null){
			buffer.drawImage(backImage,0,0,640,480,0,0,640,480,this);
        }

        // ステージ画像の描写
        if(stage[selectStageNum]!=null){
        	buffer.drawImage(stage[selectStageNum],300,100,600,400,0,0,300,300,this);
        }

        g.drawImage(back,0,0,this);
	}
}

