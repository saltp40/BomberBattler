package stage;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JPanel;

// ステージやキャラ情報の描写に関わるクラス
public class DrawStageInfo extends JPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final int MAX_NUM = 4;
	private final int MAXROW = 15;    // ステージの行数
	private final int MAXCOLUMN = 15;  // ステージの列数
	private final int SETX = 230;  // ステージのX座標
	private final int SETY = 50;   // ステージのY座標
	private int stageType;

	// 時間測定用
	private int countT;

	private Stage stage;
	private Chara[] cha;
	private int[] charaNum;

	// 使用する画像を格納
	private Image[] charaImage;  // キャラの画像
	private Image[] items;     // アイテム画像
	private Image[] stageBlocks, stageFields;  // ステージ画像
	private Image[] explo;  // 爆発画像
	private ArrayList<Image[]> arrayGimmick;  // ギミック画像
	private Image[] gimmick01;  // 動く床

	public DrawStageInfo(){

		// キャラの生成
		cha = new Chara[MAX_NUM];
		charaNum = new int[MAX_NUM];

		for(int i=0; i<MAX_NUM; i++){
			cha[i] = new Chara();
		}

		// 画像のロード
		charaImage = LoadImages.getChara();
		items = LoadImages.getItems();
		stageBlocks = LoadImages.getStageB();
		stageFields = LoadImages.getStageF();
		explo = LoadImages.getExplosion();
		arrayGimmick = LoadImages.getGimmick();
		gimmick01 = arrayGimmick.get(0);

		// 時間カウントの初期化
		countT=0;
	}

	// キャラ画像のセット
	public void setCharaImage(int n, int cn){
		charaNum[n] = cn;
	}

	// キャラ座標のセット
	public void setChara(int n, int x, int y){
		cha[n].setPosition(x,y);
	}

	// キャラのスピードを取得
	public int getCharaSpeed(int n){
		return cha[n].getSpeed();
	}

	// キャラのパワーを取得
	public int getCharaPower(int n){
		return cha[n].getBombPower();
	}

	// キャラのボム所持数を取得
	public int getCharaBombNum(int n){
		return cha[n].getBombNum();
	}

	// キャラがボムを使用した
	public void useBomb(int n){
		int x = cha[n].getXPosition();
		int y = cha[n].getYPosition();
		if(stage.isPutBomb(x, y)){
			if(stage.isExBomb(x,y) && cha[n].useExBomb(n)){
				stage.putBomb(x, y);
			}
			else if(cha[n].useBomb(n)) stage.putBomb(x, y);
		}
	}

	// キャラがアイテムを取得した
	public void setCharaItem(int n, int x, int y){
		int itn = stage.getItem(x, y);
		cha[n].setItem(itn);
	}

	// キャラ状態フラグのセット
	public void setCharaFlag(int n, int f){
		cha[n].setStatus(f);
	}

	// キャラ状態フラグの取得
	public int getCharaFlag(int n){
		return cha[n].getStatus();
	}


	// ゲームの更新(ボムとステージ)
	public void updateGame(int ts){
		updateBomb(ts);
		stage.updateStage(ts);
	}

	// ボムの更新
	public void updateBomb(int ts){

		// ボム爆発時間更新
		for(int i=0; i<MAX_NUM; i++){
			for(int j=0; j<cha[i].getBombCurrent(); j++){
				if(!cha[i].minusBombTime(j, ts)){
					int[] inf = stage.doExplosion(cha[i].getBombX(j),
							cha[i].getBombY(j), cha[i].getSetPower(j));
					if(inf != null){
						for(int k=0; k<inf.length; k+=2){

							// ボムを置いた持ち主を検索
							for(int l=i; l<MAX_NUM; l++)
								for(int m=0; m<cha[l].getBombCurrent(); m++)
									if(inf[k]==cha[l].getBombX(m) && inf[k+1]==cha[i].getBombY(m)){
										cha[l].doBombChain(m);
										break;
									}
						}
					}
				}
			}
		}

		// ボムの更新
		for(int i=0; i<MAX_NUM; i++){
			cha[i].updateBomb();
		}
	}


	// ステージタイプを返す
	public int getStageType(){
		return stage.getStageType();
	}

	// ステージの生成
	public void newStage(long l, int t, int[] itemnums, int blocknum){
		stage = new Stage();
		stageType = t;
		stage.setItemSwitch(itemnums, blocknum);
		stage.newStage(t, l);
	}

	// 時間カウントの増加
	public void nextCountT(){
		countT = (countT+1)%125;  // countTが125になったら0に戻す
	}

	// ステージの画像を描写
	public void drawStageInfo(Graphics buffer){
		drawStage(buffer);
		drawChara(buffer);
	}

	// ステージの描写
	public void drawStage(Graphics buffer){
		int[][] stageField = stage.getStageField();
		int[][] stageFunc = stage.getStageFuncField();

		int timeN = countT/5; // 変化の速度調整

		for(int i=0; i<MAXROW; i++){
			for(int j=0; j<MAXCOLUMN; j++){

				// フィールド画像描写
				buffer.drawImage(stageFields[stageType],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);

				// ステージギミックの描写
				switch(stageFunc[i][j]){
				// 動く床
				case 1: // 左方向
					buffer.drawImage(gimmick01[0],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,timeN,0,25+timeN,25,this);
					break;
				case 2: // 右方向
					buffer.drawImage(gimmick01[1],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,25-timeN,0,50-timeN,25,this);
					break;
				case 3: // 下方向
					buffer.drawImage(gimmick01[2],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,25-timeN,25,50-timeN,this);
					break;
				case 4: // 上方向
					buffer.drawImage(gimmick01[3],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,timeN,25,25+timeN,this);
					break;

				default:
				}

				// ブロックやアイテムの描写
				switch(stageField[i][j]){

				// ボムの描写
				case -51:
					buffer.drawImage(explo[0],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;

					// ブロックの描写
				case -21:
					buffer.drawImage(stageBlocks[stageType*2+1],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,
							0,0,stageBlocks[stageType*2+1].getWidth(null),stageBlocks[stageType*2+1].getHeight(null),this);
					break;

					// 壁の描写
				case -1:
					buffer.drawImage(stageBlocks[stageType*2],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,
							0,0,stageBlocks[stageType*2].getWidth(null),stageBlocks[stageType*2].getHeight(null),this);
					break;

					// ボム爆破の描写 1~20
				case 1:
					buffer.drawImage(explo[1],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 2:
					buffer.drawImage(explo[2],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 3:
					buffer.drawImage(explo[3],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 4:
					buffer.drawImage(explo[4],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 5:
					buffer.drawImage(explo[5],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 6:
					buffer.drawImage(explo[6],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 7:
					buffer.drawImage(explo[7],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;

					// アイテムの描写 21~
				case 21:
					buffer.drawImage(items[0],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 22:
					buffer.drawImage(items[1],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 23:
					buffer.drawImage(items[2],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 24:
					buffer.drawImage(items[3],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 25:
					buffer.drawImage(items[4],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;
				case 26:
					buffer.drawImage(items[5],SETX+j*25,SETY+i*25,SETX+j*25+25,SETY+i*25+25,0,0,25,25,this);
					break;

				//特に何もなし
				default:
				}
			}
		}
	}

	// キャラ画像の描写
	public void drawChara(Graphics buffer){
		Graphics2D buffer2 = (Graphics2D) buffer;

		for(int i=0; i<MAX_NUM; i++){

			if(charaNum[i] < 0) continue;

			int status = cha[i].getStatus();
			int x = cha[i].getXPosition();
			int y = cha[i].getYPosition();

			if(status >= 0){  // 通常のステータスのとき
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
				buffer2.setComposite(ac);
				buffer2.drawImage(charaImage[charaNum[i]],SETX+x,SETY+y,SETX+x+25,SETY+y+25,0,0,25,25,this);
			}else if(status == -1){   // 爆発したとき
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
				buffer2.setComposite(ac);
				buffer2.drawImage(charaImage[charaNum[i]],SETX+x,SETY+y,SETX+x+25,SETY+y+25,0,0,25,25,this);
			}
		}
	}
}
