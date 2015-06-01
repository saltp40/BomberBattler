package stage;

import java.awt.Image;
import java.awt.MediaTracker;
import java.util.ArrayList;

import javax.swing.JPanel;

// 画像のロードを行うクラス
// 基本的に必要なときに呼び出す
public class LoadImages extends JPanel{


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static Image[] chara;					// キャラ画像
	private static Image[] stageB, stageF;			// ステージのブロックやフィールド画像
	private static Image[] stageView;				// ステージセレクト時の画像
	private static Image[] stageBG;					// ゲーム中の背景画像
	private static Image[] items;					// アイテムの背景画像
	private static Image[] backGround; 				// ゲーム以外の背景画像
	private static Image[] explo;					// 爆発エフェクト画像
	private static Image[] selframe;				// キャラ選択時の囲い画像
	private static ArrayList<Image[]> gimmick;		// ギミック用の画像

	private String[] charaStr;
	private String[] stageBStr, stageFStr;
	private String[] stageViewStr;
	private String[] stageBGStr;
	private String[] itemsStr;
	private String[] backGroundStr;
	private String[] exploStr;
	private String[] selframeStr;
	private ArrayList<String[]> gimmickStr;

	private static final int charaNum = 12;
	private static final int itemNum = 10;
	private static final int stageBNum = 10, stageFNum = 5;
	private static final int stageViewNum = 5;
	private static final int stageBGNum = 6;
	private static final int backGroundNum = 5;
	private static final int exploNum = 8;
	private static final int selframeNum = 4;
	//private static final int gimmickNum = 4;

	MediaTracker mt = new MediaTracker(this);

	public void init(){
		// 使用する画像の初期化
		chara = new Image[charaNum];
		items = new Image[itemNum];
		stageB = new Image[stageBNum];
		stageF = new Image[stageFNum];
		stageView = new Image[stageViewNum];
		stageBG = new Image[stageBGNum];
		backGround = new Image[backGroundNum];
		explo = new Image[exploNum];
		selframe = new Image[selframeNum];
		gimmick = new ArrayList<Image[]>();

		// ロードパスを格納
		charaStr = new String[charaNum];
		itemsStr = new String[itemNum];
		stageBStr = new String[stageBNum];
		stageFStr = new String[stageFNum];
		stageViewStr = new String[stageViewNum];
		stageBGStr = new String[stageBGNum];
		backGroundStr = new String[backGroundNum];
		exploStr = new String[exploNum];
		selframeStr = new String[selframeNum];
		gimmickStr = new ArrayList<String[]>();

		// ロードパスのセット
		setLoadPass();

		// 画像のロード
		loadChara();
		loadStageB();
		loadStageF();
		loadStageView();
		loadStageBG();
		loadItem();
		loadBackGround();
		loadExplosion();
		loadSelframe();
		loadGimmick();
	}

	// ロードパスのセット
	public void setLoadPass(){

		charaStr[0] = "Pic/player01.png";
		charaStr[1] = "Pic/player02.png";
		charaStr[2] = "Pic/player03.png";
		charaStr[3] = "Pic/player04.png";
		charaStr[4] = "Pic/player05.png";

		itemsStr[0] = "Pic/item01.png";
		itemsStr[1] = "Pic/item02.png";
		itemsStr[2] = "Pic/item03.png";
		itemsStr[3] = "Pic/item04.png";
		itemsStr[4] = "Pic/item05.png";
		itemsStr[5] = "Pic/item06.png";

		stageBStr[0] = "Pic/block_type01.png";
		stageBStr[1] = "Pic/block_type02.png";
		stageBStr[2] = "Pic/block_type03.png";
		stageBStr[3] = "Pic/block_type04.png";
		stageBStr[4] = "Pic/block_type05.png";
		stageBStr[5] = "Pic/block_type06.png";
		stageBStr[6] = "Pic/block_type07.png";
		stageBStr[7] = "Pic/block_type08.png";
		stageBStr[8] = "Pic/block_type09.png";
		stageBStr[9] = "Pic/block_type10.png";

		stageFStr[0] = "Pic/stage_type01.png";
		stageFStr[1] = "Pic/stage_type02.png";
		stageFStr[2] = "Pic/stage_type03.png";
		stageFStr[3] = "Pic/stage_type04.png";
		stageFStr[4] = "Pic/stage_type05.png";

		stageViewStr[0] = "Pic/stage01_view.png";
		stageViewStr[1] = "Pic/stage02_view.png";
		stageViewStr[2] = "Pic/stage03_view.png";
		stageViewStr[3] = "Pic/stage04_view.png";
		stageViewStr[4] = "Pic/stage05_view.png";

		stageBGStr[0] = "Pic/play_stage1.png";

		backGroundStr[0] = "Pic/title_back.png";
		backGroundStr[1] = "Pic/con_back.png";
		backGroundStr[2] = "Pic/select_stage.png";
		backGroundStr[3] = "Pic/select_chara.png";
		backGroundStr[4] = "Pic/select_item.png";

		exploStr[0] = "Pic/bomb01.png";
		exploStr[1] = "Pic/explo01.png";
		exploStr[2] = "Pic/explo02.png";
		exploStr[3] = "Pic/explo03.png";
		exploStr[4] = "Pic/explo04.png";
		exploStr[5] = "Pic/explo05.png";
		exploStr[6] = "Pic/explo06.png";
		exploStr[7] = "Pic/explo07.png";

		selframeStr[0] = "Pic/select_frame01.png";
		selframeStr[1] = "Pic/select_frame02.png";
		selframeStr[2] = "Pic/select_frame03.png";
		selframeStr[3] = "Pic/select_frame04.png";

		String g[] = new String[4]; // 動く床
		g[0] = "Pic/gimmick01.png";
		g[1] = "Pic/gimmick02.png";
		g[2] = "Pic/gimmick03.png";
		g[3] = "Pic/gimmick04.png";
		gimmickStr.add(g);
	}

	// キャラ画像のロード
	public void loadChara(){
		for(int j=0; j<charaNum; j++){
			if(charaStr[j] != null)
				chara[j] = getToolkit().getImage(charaStr[j]);
		}
		waitImage(charaNum, chara);
	}

	// アイテム画像のロード
	public void loadItem(){
		for(int j=0; j<itemNum; j++){
			if(itemsStr[j] != null)
				items[j] = getToolkit().getImage(itemsStr[j]);
		}
		waitImage(itemNum, items);
	}

	// ステージブロック画像のロード
	public void loadStageB(){
		for(int j=0; j<stageBNum; j++){
			if(stageBStr[j] != null)
				stageB[j] = getToolkit().getImage(stageBStr[j]);
		}
		waitImage(stageBNum, stageB);
	}

	// ステージフィールド画像のロード
	public void loadStageF(){
		for(int j=0; j<stageFNum; j++){
			if(stageFStr[j] != null)
				stageF[j] = getToolkit().getImage(stageFStr[j]);
		}
		waitImage(stageFNum, stageF);
	}

	// ステージビュー画像のロード
	public void loadStageView(){
		for(int j=0; j<stageViewNum; j++){
			if(stageViewStr[j] != null)
				stageView[j] = getToolkit().getImage(stageViewStr[j]);
		}
		waitImage(stageViewNum, stageView);
	}

	// ゲーム中背景画像のロード
	public void loadStageBG(){
		for(int j=0; j<stageBGNum; j++){
			if(stageBGStr[j] != null)
				stageBG[j] = getToolkit().getImage(stageBGStr[j]);
		}
		waitImage(stageBGNum, stageBG);
	}

	// ステージ背景画像のロード
	public void loadBackGround(){
		for(int j=0; j<backGroundNum; j++){
			if(backGroundStr[j] != null)
				backGround[j] = getToolkit().getImage(backGroundStr[j]);
		}
		waitImage(backGroundNum, backGround);
	}

	// 爆発エフェクトのロード
	public void loadExplosion(){
		for(int j=0; j<exploNum; j++){
			if(exploStr[j] != null)
				explo[j] = getToolkit().getImage(exploStr[j]);
		}
		waitImage(exploNum, explo);
	}

	// キャラセレクトフレームのロード
	public void loadSelframe(){
		for(int j=0; j<selframeNum; j++){
			if(selframeStr[j] != null)
				selframe[j] = getToolkit().getImage(selframeStr[j]);
		}
		waitImage(selframeNum, selframe);
	}

	// ギミック画像のロード
	public void loadGimmick(){

		for(int j=0; j<gimmickStr.size(); j++){
			String[] str = gimmickStr.get(j);
			Image[] tmp = new Image[str.length];

			for(int k=0; k<str.length; k++){
				tmp[k] = getToolkit().getImage(str[k]);
			}
			waitImage(str.length, tmp);
			gimmick.add(tmp);
		}
	}


	// イメージのロードが終わるのを待つ
	public void waitImage(int n, Image[] img){
		for(int j=0; j<n; j++){
			mt.addImage(img[j], 0);
			try {
				mt.waitForAll();
			} catch (InterruptedException e) { }
		}
	}

	// キャラ画像を返す
	public static Image[] getChara(){
		return chara;
	}

	// 引数で選択したキャラ画像を返す
	public static Image getChara(int n){
		return chara[n];
	}

	// アイテム画像を返す
	public static Image[] getItems(){
		return items;
	}

	// ステージブロック画像を返す
	public static Image[] getStageB(){
		return stageB;
	}

	// ステージフィールド画像を返す
	public static Image[] getStageF(){
		return stageF;
	}

	// ステージビュー画像を返す
	public static Image[] getStageView(){
		return stageView;
	}

	// ゲーム中背景画像を返す
	public static Image getStageBG(int t){
		if(t < stageBGNum)
			return stageBG[t];
		else
			return null;
	}

	// 背景画像を返す
	public static Image getBackGround(int t){
		if(t < backGroundNum)
			return backGround[t];
		else
			return null;
	}

	// 爆発エフェクト画像を返す
	public static Image[] getExplosion(){
		return explo;
	}

	// セレクトフレーム画像を返す
	public static Image[] getSelFrame(){
		return selframe;
	}

	// ギミック画像リストを返す
	public static ArrayList<Image[]> getGimmick(){
		return gimmick;
	}
}


