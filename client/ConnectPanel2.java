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

// どの部屋に入るかを決める画面
// パネルタイプ: 4
class ConnectPanel2 extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Graphics buffer;
	private Dimension size;
	private JButton returnButton;     // 戻るセレクトボタン
	private JButton updateButton;     // 更新ボタン
	private JButton[] roomButton;     // 部屋用のボタン
	private JLabel title;

	private int max=4;

	ConnectPanel2(Dimension size, Image back){
		this.size = size;
		this.back = back;

		roomButton = new JButton[max];

		// 背景画像のロード
		backImage = LoadImages.getBackGround(1);

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// 使用するボタンの作成
		returnButton = new JButton("←戻る");
		returnButton.addActionListener(new returnActionListener());
		returnButton.setBounds(10, 10, 90, 30);
		add(returnButton);

		updateButton = new JButton("更新");
		updateButton.addActionListener(new updateActionListener());
		updateButton.setBounds(330, 10, 90, 30);
		add(updateButton);


		for(int i=0; i<max; i++){
			roomButton[i] = new JButton("No Room");
			roomButton[i].addActionListener(new roomButtonActionListener());
			roomButton[i].setBounds(130, 80+60*i, 150, 40);
			add(roomButton[i]);
			roomButton[i].setEnabled(false);
		}

		// labelの作成
		title = new JLabel("参加可能ルーム");
		title.setBounds(130, 30, 150, 40);
		add(title);
	}

	// returnボタンを押したときのアクション
	class returnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(2);
		}
	}

	// updateボタンを押したときのアクション
	class updateActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.getRecieveThread().sendString("UPDATEINFO");
		}
	}

	// roomButtonを押したときのアクション
	class roomButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int j;
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());

			for(j=0; j<max; j++)
				if(e.getSource() == roomButton[j]){
					mf.newPanel2(3);
					break;
				}
			mf.getRecieveThread().sendString("ENTERROOM " + j);
		}
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 4;
	}

	// ラベルをセットする     (ルーム名: 4/4 とか・・・
	public void setLabelButton(int t, String str, int n){
		String aaa;
		aaa = str + " : " + n + "/4 人";

		if(str.equals("empty")){
			if(t<max){
				roomButton[t].setText("No Room");
				roomButton[t].setEnabled(false);
			}
		}
		else{
			if(t<max){
				roomButton[t].setText(aaa);
				roomButton[t].setEnabled(true);
			}
			if(n == max) roomButton[t].setEnabled(false);
		}
	}

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

     g.drawImage(back,0,0,this);
	}
}



