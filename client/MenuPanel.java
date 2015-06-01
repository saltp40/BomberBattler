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

// スタート画面
//パネルタイプ: 0
class MenuPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Graphics buffer;
	private Dimension size;
	JButton SingleButton;     // 一人用セレクトボタン
	JButton MultiButton;      // マルチ用セレクトボタン
	MenuPanel my = this;
	JLabel ver;  // バージョンを表示するラベル

	MenuPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		// 背景画像のロード
		backImage = LoadImages.getBackGround(0);

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// バージョンラベルを作成
		ver = new JLabel("ver. 1.20");
		ver.setBounds(560, 20, 120, 30);
		add(ver);

		// 使用するボタンの作成
		SingleButton = new JButton("SINGLE");
		MultiButton = new JButton("MULTI");
		SingleButton.addActionListener(new singleActionListener());
		MultiButton.addActionListener(new multiActionListener());
		SingleButton.setBounds(160, 350, 100, 50);
		MultiButton.setBounds(400, 350, 100, 50);
		add(SingleButton);
		add(MultiButton);
	}

	// singleボタンを押したときのアクション
	class singleActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			// シングルゲーム
		}
	}

	// multiボタンを押したときのアクション
	class multiActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(2);
		}
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 0;
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

