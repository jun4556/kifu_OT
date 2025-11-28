package com.objetdirect.gwt.umldrawer.client.canvas;

import com.google.gwt.user.client.rpc.RemoteService;
import com.objetdirect.gwt.umldrawer.client.beans.EditEvent;

public interface CanvasService extends RemoteService{

	public void saveCanvas( String studentId, int exercisesId, String canvasUrl);

	public EditEvent loadCanvas(String studentId, int exercisesId);

	public EditEvent undo(String studentId, int exercisesId);

	public boolean saveCanvasAsAnswer( String studentId, int exercisesId, String canvasUrl);

	public String getAnswer(int exerciseId);
	
	// --- マージ機能のために追加するメソッド ---
		/**
		 * AIマージサーバーを呼び出し、マージ結果の文字列（Base64）を取得します。
		 * @param myUrl 自分のUMLデータ
		 * @param opponentUrl 相手のUMLデータ
		 * @return マージされたUMLデータ
		 */
		public String mergeCanvas(String myUrl, String opponentUrl);

//		/**
//		 * マージ対象の候補となる、最近の保存イベントリストを取得します。
//		 * @param exerciseId 課題ID
//		 * @param currentStudentId 自分の学生ID（除外用）
//		 * @return 保存イベント情報のリスト
//		 */
//		public List<SaveEventInfo> getRecentSaves(int exerciseId, String currentStudentId);
		
		/**
		 * マージされたキャンバスデータを、共通IDをキーとして保存します。
		 * @param commonId マージ識別のための共通ID
		 * @param exerciseId 課題ID
		 * @param canvasUrl マージされたUMLデータ
		 */
		public void saveMergedCanvas(String commonId, int exerciseId, String canvasUrl);
		
}
