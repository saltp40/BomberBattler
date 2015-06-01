package client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import stage.LoadImages;

// ゲームの結果を表示する画面
//パネルタイプ: 9
public class ResultPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage;
	private Graphics buffer;
	private Dimension size;
	private JLabel winner;
	private JLabel help;

	ResultPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		// 背景画像のロード
		backImage = LoadImages.getBackGround(1);

		// レイアウトマネージャーを無効にする
		setLayout(null);
		
		// labelの作成
		help = new JLabel("ENTERキーを押してください");
		help.setBounds(240, 300, 200, 30);
		add(help);
		
		winner = new JLabel("引き分けでした");
		winner.setBounds(100, 100, 300, 30);
		winner.setFont(new Font("MS ゴシック", Font.BOLD, 20));
		add(winner);
	}

	// Panelタイプを返す
	public int getPanelType(){
		return 9;
	}

	// ラベルをセットする
	public void setWinner(int winnum, String name){
		String str = winnum + "P: " + name + "の勝ちです";
		winner.setText(str);
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
