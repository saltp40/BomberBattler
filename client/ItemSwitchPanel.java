package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import stage.LoadImages;

// アイテムスイッチ画面
// パネルタイプ: 8
public class ItemSwitchPanel extends JPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back, backImage, itemImages[];
	private Graphics buffer;
	private Dimension size;
	private JButton returnButton;     // 戻るセレクトボタン
	private JButton decideButton;     // 決定ボタン
	private JScrollBar bars[];     // 値を調整するためのスクロールバー
	private JTextField values[];   // アイテムの値を入れる
	private JLabel sumLabel;       // アイテムの合計数
	private JTextField sumText;    // アイテムの合計数を表示
	private JScrollBar blockBar;   // ブロック数を調整するためのスクロールバー
	private JLabel blockLabel;     // ブロック数
	private JTextField blockText;  // ブロック数を表示
	private int itemNum;   // アイテムの種類を格納
	//private boolean isClicked;   // マウスがクリックされているかどうか


	ItemSwitchPanel(Dimension size, Image back){
		this.size = size;
		this.back = back;

		// 背景画像のロード
		backImage = LoadImages.getBackGround(4);

		// アイテム画像のロード
		itemImages = LoadImages.getItems();
		itemNum = 0;

		// レイアウトマネージャーを無効にする
		setLayout(null);

		// アイテムの合計数に関するものを初期化
		sumLabel = new JLabel("アイテム合計: ");
		sumLabel.setBounds(300, 400, 100, 30);
		add(sumLabel);

		sumText = new JTextField();
		sumText.setBounds(400, 400, 40, 30);
		sumText.setEditable(false);
		add(sumText);

		// ブロック数に関するものを初期か
		blockLabel = new JLabel("ブロック数 ");
		blockLabel.setBounds(100, 400, 100, 30);
		add(blockLabel);

		blockBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 0, 10, 120);
		blockBar.setBounds(175,400,60,30);
		blockBar.setValue(100);
		blockBar.addAdjustmentListener(new myAdjustmentListener());
		//blockBar.addMouseListener(new myScrollListener());
		add(blockBar);

		blockText = new JTextField();
		blockText.setBounds(240, 400, 40, 30);
		blockText.setEditable(false);
		add(blockText);

		// 使用するボタンの作成
		returnButton = new JButton("←戻る");
		returnButton.addActionListener(new returnActionListener());
		returnButton.setBounds(10, 10, 90, 30);
		add(returnButton);

		decideButton = new JButton("決定");
		decideButton.addActionListener(new decideActionListener());
		decideButton.setBounds(470, 400, 90, 30);
		add(decideButton);

		//isClicked = false;
	}

	// returnボタンを押したときのアクション
	class returnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			mf.getRecieveThread().sendString("GOSTAGESEL");
		}
	}

	// decideボタンを押したときのアクション
	class decideActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
			String str = "ITEMSWITCH DECIDE " + blockText.getText() + " " + getItemSwitch();
			mf.getRecieveThread().sendString(str);
		}
	}

	// スクロールバーを調整したとき
	public class myAdjustmentListener implements AdjustmentListener{
		public void adjustmentValueChanged(AdjustmentEvent e) {
			for(int i=0; i<itemNum; i++){
				if(e.getSource() == bars[i]){

					int c = bars[i].getValue();  //バーの値を得る(0～30）
					int bn = Integer.parseInt(blockText.getText());
					int sum = getItemSum();

					// ブロックの生成数よりアイテム設置数の方が多い場合の処理
					if(sum > bn){
						int error = sum - bn;
						c -= error; // 多い分を減らす
					}
					String s =Integer.toString(c);  //テキストフィールドに入れるために数値を文字列に変える
					values[i].setText(s);
					sumText.setText(Integer.toString(getItemSum()));

					if(bars[i].getValueIsAdjusting() == false){
						bars[i].setValue(c);
						MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
						String str = "ITEMSWITCH ONE " + i + " " + c;
						mf.getRecieveThread().sendString(str);
					}
				}
			}
			if(e.getSource() == blockBar){
				int c = blockBar.getValue();  //バーの値を得る(10～120）
				int sum = getItemSum();

				// ブロックの生成数よりアイテム設置数の方が多い場合の処理
				if(sum > c){
					int error = sum - c;
					c += error; // 少ない分を増やす
				}
				String s =Integer.toString(c);  //テキストフィールドに入れるために数値を文字列に変える
				blockText.setText(s);


				if(blockBar.getValueIsAdjusting() == false){
					blockBar.setValue(c);
					MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
					String str = "ITEMSWITCH BLOCK " + c;
					mf.getRecieveThread().sendString(str);
				}

			}
		}
	}

/*
	// スクロールバー操作時のマウスの判定
	public class myScrollListener extends MouseAdapter{
		public void mouseReleased(MouseEvent e){
			for(int i=0; i<itemNum; i++){
				if(e.getSource() == bars[i]){
					int  c = bars[i].getValue();  //バーの値を得る(0～30）

					MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
					String str = "ITEMSWITCH ONE " + i + " " + c;
					mf.getRecieveThread().sendString(str);
					break;
				}
			}
			if(e.getSource() == blockBar){
				int  c = blockBar.getValue();  //バーの値を得る(10～120）

				MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor((Component)e.getSource());
				String str = "ITEMSWITCH BLOCK " + c;
				mf.getRecieveThread().sendString(str);
			}
			//isClicked = false;
		}
		public void mousePressed(MouseEvent e){
			//isClicked = true;
		}
	}*/


	// Panelタイプを返す
	public int getPanelType(){
		return 8;
	}

	// アイテムスイッチ状況を設定
	public void setItemSwitch(String[] nums, String bn){
		itemNum = nums.length;

		// アイテムスイッチの値を決める部分の初期化
		values = new JTextField[itemNum];
		bars = new JScrollBar[itemNum];

		for(int i=0; i<itemNum; i++){
			int n = Integer.parseInt(nums[i]);
			int x = (i/4)*130;
			int y = (i%4)*80;
			values[i] = new JTextField();
			values[i].setBounds(120+x,80+y,30,30);
			values[i].setEditable(false);   // 直接変更できないようにする
			values[i].setText(nums[i]);
			add(values[i]);

			bars[i] = new JScrollBar(JScrollBar.HORIZONTAL);
			bars[i].setBounds(60+x,80+y,60,30);
			bars[i].setValues(n, 0, 0, 30);
			bars[i].addAdjustmentListener(new myAdjustmentListener());
			//bars[i].addMouseListener(new myScrollListener());
			add(bars[i]);
		}

		blockText.setText(bn);  // ブロックの数をセット
		blockBar.setValue(Integer.parseInt(bn));

		sumText.setText(Integer.toString(getItemSum()));  // アイテムの合計数をセット
		repaint();
	}

	// アイテムスイッチ一つだけ情報を変更
	public void setOneItemSwitch(int n, String num){
		AdjustmentListener[] ad = bars[n].getAdjustmentListeners();
		if(ad != null){
			bars[n].removeAdjustmentListener(ad[0]);
			values[n].setText(num);
			bars[n].setValue(Integer.parseInt(num));
			sumText.setText(Integer.toString(getItemSum()));
			bars[n].addAdjustmentListener(ad[0]);
		}
	}

	// アイテムスイッチ状況を取得
	public String getItemSwitch(){
		String str = "";
		for(int i=0; i<itemNum; i++){
			str += values[i].getText() + " ";
		}
		return str;
	}

	// ブロック数を変更
	public void setBlockNum(String bn){
		AdjustmentListener[] ad = blockBar.getAdjustmentListeners();
		if(ad != null){
			blockBar.removeAdjustmentListener(ad[0]);
			blockText.setText(bn);
			blockBar.setValue(Integer.parseInt(bn));
			blockBar.addAdjustmentListener(ad[0]);
		}
	}

	// アイテム合計を取得
	public int getItemSum(){
		int sum = 0;
		for(int i=0; i<itemNum; i++){
			sum += bars[i].getValue();
		}
		return sum;
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

		// アイテム画像の表示
		for(int i=0; i<itemNum; i++){
			int x = (i/4)*130;
			int y = (i%4)*80;
			buffer.drawImage(itemImages[i],30+x,80+y,30+x+25,80+y+25,0,0,25,25,this);
		}

		g.drawImage(back,0,0,this);
	}
}
