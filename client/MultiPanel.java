package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import stage.LoadImages;

// 部屋を作るか部屋に参加するがを決める画面
public class MultiPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Graphics buffer;
	private Dimension size;
	private JButton MakeButton;     // 部屋を作るセレクトボタン
	private JButton EnterButton;      // 部屋に入るセレクトボタン
	private JButton ReturnButton;     // 戻るボタン

	MultiPanel my = this;

	MultiPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		// 背景画像のロード
		backImage = LoadImages.getBackGround(0);

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// 使用するボタンの作成
		MakeButton = new JButton("部屋を作る");
		EnterButton = new JButton("部屋にはいる");
		ReturnButton = new JButton("←戻る");
		MakeButton.addActionListener(new makeActionListener());
		EnterButton.addActionListener(new enterActionListener());
		ReturnButton.addActionListener(new returnActionListener());
		MakeButton.setBounds(140, 350, 150, 50);
		EnterButton.setBounds(390, 350, 150, 50);
		ReturnButton.setBounds(10, 10, 90, 30);
		add(MakeButton);
		add(EnterButton);
		add(ReturnButton);
	}

	// makeボタンを押したときのアクション
	class makeActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(3);
		}
	}

	// enterボタンを押したときのアクション
	class enterActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(4);
		}
	}

	// returnボタンを押したときのアクション
	class returnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.newPanel(0);
		}
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 2;
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

