package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import stage.DrawStageInfo;
import stage.LoadImages;

// ゲームプレイ画面
// パネルタイプ: 6
class PlayGamePanel extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Graphics buffer;
	private Dimension size;

	private JLabel[] lname;   // 参加者の名前
	private JLabel[] lstatus;  // キャラのステータス

	private DrawStageInfo dsi;  // ステージやキャラの描写

	private int max=4;
	//private int selectStageNum; // 現在選択されているステージの番号
	private int playerNum;  // 自分自身のプレイヤー番号

	private JTextField commentField;  // コメント入力エリア
	private JTextArea commentArea;  // コメント表示エリア
	private JScrollPane scrollpane; // コメント欄用スクロールパネル

	PlayGamePanel(Dimension size, Image back, DrawStageInfo dsi){
		this.size = size;
		this.back = back;
		this.dsi = dsi;

		// 背景画像のロード
		backImage = LoadImages.getStageBG(0);

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// labelの作成
		lname = new JLabel[max];

		for(int i=0; i<max; i++){
			lname[i] = new JLabel("---");
			lname[i].setBounds(50, 40+70*i, 90, 30);
			add(lname[i]);
		}

		lstatus = new JLabel[max];
		for(int i=0; i<max; i++){
			lstatus[i] = new JLabel("P  , B  , S");
			lstatus[i].setBounds(50, 60+70*i, 150, 30);
			add(lstatus[i]);
		}

		// コメント表示エリアの作成
		commentArea = new JTextArea();
		commentArea.setBounds(10, 320, 210, 120);
		commentArea.setEditable(false);  // 編集不可にする
		scrollpane = new JScrollPane(commentArea); // スクロールバーをつける
		scrollpane.setBounds(10, 320, 210, 120);
		add(scrollpane);

		// コメント入力エリアの作成
		commentField = new JTextField("ここにコメントを入力");
		commentField.setBounds(300, 410, 230, 30);
		commentField.addKeyListener(new myCommentKeyListener());
		commentField.setVisible(false);  // 初期は見えない状態
		add(commentField);

		// エンター以外でコメント欄を消す時用のマウスリスナー
		addMouseListener(new myMouseListener());
	}

	// マウスの判定
	public class myMouseListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.requestFocusInWindow();    // メインフレームにフォーカスする
			commentField.setVisible(false);    // コメントを書く欄を隠す
		}
	}

	// コメントキーリスナーの判定
	public class myCommentKeyListener implements KeyListener {

		// キーが押されたときの処理
		public void keyPressed(KeyEvent e) {

			// エンターが押されたときコメントを送信
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
				String str = commentField.getText();
				if(!str.equals("")) mf.getRecieveThread().sendString("COMMENT "+ str);

				mf.requestFocusInWindow();    // メインフレームにフォーカスする
				commentField.setVisible(false);    // コメントを書く欄を隠す
			}
		}

		// キーが離されたときの処理
		public void keyReleased(KeyEvent e) {}

		// キーがタイプされたときの処理
		public void keyTyped(KeyEvent e) {}
	}

	// コメントを書く準備
	public void startComment(){
		commentField.setText("");         // コメントの初期化
		commentField.setVisible(true);    // コメントを書く欄を表示
		commentField.requestFocusInWindow();   // コメント欄にフォーカスする
	}

	// コメントをセットする
	public void setComment(String com, String name){
		String crlf = System.getProperty("line.separator");  // 改行コードの取得
		String mess = name + ": " + com + crlf;
		commentArea.append(mess);
		commentArea.setCaretPosition(commentArea.getDocument().getLength());  // コメント追加時に自動的にスクロール
	}

	// プレイヤー番号の設定
	public void setPlayerNum(int pn){
		playerNum = pn;
	}

	// キャラクターの画像設定
	public void setCharaImage(int n, int cn){
		dsi.setCharaImage(n, cn);
	}

	// キャラクターの位置設定
	public void setChara(int n, int x, int y){
		dsi.setChara(n, x, y);
		setCharaItem(n, x, y);
		repaint();
	}

	// キャラクターの状態設定
	public void setCharaFlag(int n, int f){
		dsi.setCharaFlag(n, f);
		repaint();
	}

	// ボムのセット
	public void setBomb(int n){
		dsi.useBomb(n);
		repaint();
	}

	// タイムの更新
	public void updateGame(int t){
		dsi.updateGame(t);
		repaint();
	}

	// 描写の更新
	public void updateDraw(){
		dsi.nextCountT();
		repaint();
	}

	// アイテムの取得
	public void setCharaItem(int n, int x, int y){
		dsi.setCharaItem(n, x, y);
		repaint();
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 6;
	}


	// メンバーの情報を設定
	public void setMemberLabel(int n, String name){
		if(n < max){
			if(name.equals("NULL")) lname[n].setText("---");
			else if(dsi.getCharaFlag(n) == -1){
				lname[n].setText((n+1) + "P: " + name);
				lstatus[n].setText("＼(^o^)／デデーン");
			}
			else{
				lname[n].setText((n+1) + "P: " + name);
				lstatus[n].setText("P " + dsi.getCharaPower(n) + ", B "
						+ dsi.getCharaBombNum(n) +", S " + dsi.getCharaSpeed(n));
			}
		}
	}

	// パネルの描写
	public void paintComponent(Graphics g){
		if (back==null)  return;
     buffer= back.getGraphics();
     if (buffer==null)   return;
		size = getSize();
     buffer.setColor(getBackground());
     buffer.fillRect(0, 0, size.width, size.height);

     // 背景の描写
     if(backImage!=null){
			buffer.drawImage(backImage,0,0,640,480,0,0,640,480,this);
     }

     dsi.drawStageInfo(buffer);

     g.drawImage(back,0,0,this);
	}
}

