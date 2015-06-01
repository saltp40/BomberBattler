package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import stage.LoadImages;

// キャラクターセレクト画面
// パネルタイプ:7
class CharaSelectPanel extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Image chara[];   // キャラビュー画像を格納
	private Image frame[];   // セレクトフレーム画像を格納
	private Graphics buffer;
	private Dimension size;
	private JButton returnButton;     // 戻るセレクトボタン
	private JLabel keyLabel;      // 入力キーを表示するラベル

	private int[] posPlayer;  // 参加者のキャラ番号
	private boolean[] isDecide;  // 決定を押したか
	private boolean[] isExist;   // プレイヤーが存在するか

	CharaSelectPanel my = this;

	private int max=4;
	private int charaMax;  // キャラクターの種類
	private int selectCharaNum; // 現在選択されているキャラクターの番号
	private boolean scnflag;  // 決定押した状態かどうかの判定

	public CharaSelectPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		posPlayer = new int[max];
		isDecide = new boolean[max];
		isExist = new boolean[max];
		scnflag = false;

		// 背景画像のロード
		backImage = LoadImages.getBackGround(3);

		// フレーム画像のロード
		frame = LoadImages.getSelFrame();

		// キャラ画像のロード
		chara = LoadImages.getChara();
		charaMax = chara.length;

		// キャラ選択の初期化
		selectCharaNum = 0;
		for(int i=0; i<max; i++){
			posPlayer[i] = 0;
			isDecide[i] = false;
			isExist[i] = false;
		}

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// 使用するボタンの作成
		returnButton = new JButton("←戻る");
		returnButton.addActionListener(new returnActionListener());
		returnButton.setBounds(10, 10, 90, 30);
		add(returnButton);

		// labelの作成
		keyLabel = new JLabel("Zキーで決定");
		keyLabel.setBounds(300, 400, 120, 40);
		add(keyLabel);
	}

	// returnボタンを押したときのアクション
	class returnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(2);
		}
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 7;
	}

	// キーラベルのセット
	public void setKeyLabel(boolean f){
		if(f) keyLabel.setText("SPACEでキャンセル");
		else keyLabel.setText("Zキーで決定");
	}

	// キャラ決定フラグのセット
	public void setScnFlag(boolean f){
		scnflag = f;
		setKeyLabel(f);
	}

	// キャラクターの選択番号を返す
	public int getSelectCharaNum(){
		return selectCharaNum;
	}

	// キャラクターの選択設定
	public int selectChara(int t){
		if(!scnflag){
			selectCharaNum += t;

			if(selectCharaNum < 0){
				selectCharaNum += charaMax;
			}else if(selectCharaNum >= charaMax){
				selectCharaNum -= charaMax;
			}
			return selectCharaNum;
		}
		return -1;
	}

	// 選択の決定をしたかフラグをセット
	public void setIsDecide(int n, boolean f){
		isDecide[n] = f;
	}

	// メンバーの情報を設定
	public void setMemberChara(int n, int cn){
		if(n < max){
			posPlayer[n] = cn;
		}
		repaint();
	}

	// メンバーがいるかどうか
	public void setMember(int n, String name){
		if(name.equals("NULL")){
			isExist[n] = false;
		}
		else isExist[n] = true;
		repaint();
	}

//	// パネルの移動
//	public void inimCharaSelP2(){
//		MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor(this);
//		mf.newPanel2(5);
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

        // キャラ画像の描写
        int j=0;
        for(int i=0; i<charaMax; i++){
        	if(i%6 == 0) j++;
        	buffer.drawImage(chara[i],i*50+160,j*50+70,i*50+185,j*50+95,0,0,25,25,this);
        }

        // キャラごとのフレーム画像を表示
        for(int i=0; i<max; i++){
        	int x = posPlayer[i]%6;
        	int y = posPlayer[i]/6;
        	if(isExist[i]) buffer.drawImage(frame[i],x*49+137,y*49+102,x*49+214,y*49+156,0,0,77,54,this);
        }

        // プレイヤーごとのキャラ画像を表示
        // 1P
        if(isDecide[0]) buffer.drawImage(chara[posPlayer[0]],25,155,95,225,0,0,25,25,this);
        // 2P
        if(isDecide[1]) buffer.drawImage(chara[posPlayer[1]],25,325,95,395,0,0,25,25,this);
        // 3P
        if(isDecide[2]) buffer.drawImage(chara[posPlayer[2]],525,85,595,155,0,0,25,25,this);
        // 4P
        if(isDecide[3]) buffer.drawImage(chara[posPlayer[3]],525,285,595,355,0,0,25,25,this);

        g.drawImage(back,0,0,this);
	}
}
