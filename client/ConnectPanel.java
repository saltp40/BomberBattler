package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import stage.LoadImages;

// 他の参加者を待つ画面
//パネルタイプ: 3
public class ConnectPanel extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Graphics buffer;
	private Dimension size;
	private JButton ReturnButton;     // 戻るセレクトボタン
	private JButton decideButton;     // 決定ボタン
	private JLabel[] lname;
	private JLabel title;

	private int max=4;

	ConnectPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		lname = new JLabel[max];


		// 背景画像のロード
		backImage = LoadImages.getBackGround(1);

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// 使用するボタンの作成
		ReturnButton = new JButton("←戻る");
		ReturnButton.addActionListener(new returnActionListener());
		ReturnButton.setBounds(10, 10, 90, 30);
		add(ReturnButton);

		decideButton = new JButton("決定");
		decideButton.addActionListener(new decideActionListener());
		decideButton.setBounds(350, 300, 90, 30);
		add(decideButton);
		//decideButton.setEnabled(false);

		// labelの作成
		title = new JLabel("参加者");
		title.setBounds(100, 40, 90, 30);
		add(title);

		for(int i=0; i<max; i++){
			lname[i] = new JLabel("---");
			lname[i].setBounds(100, 70+50*i, 90, 30);
			add(lname[i]);
		}
	}

	// returnボタンを押したときのアクション
	class returnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(2);
		}
	}

	// decideButtonを押したときのアクション
	class decideActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.getRecieveThread().sendString("DECIDEMEMBER");
		}
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 3;
	}

//	// パネルの移動
//	public void nextPane7(){
//		MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor(this);
//		mf.newPanel2(7);
//	}

	// ラベルをセットする
	public void setLabel(int t, String str){

		if(t<max){
			if(str.equals("NULL"))
				lname[t].setText("---");
			else
				lname[t].setText((t+1)+"P: "+str);
		}
		else{   // エラーが発生したとき
			JOptionPane.showMessageDialog(null, "部屋接続時にエラーが発生しました");
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor(this);
			mf.newPanel(2);
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
