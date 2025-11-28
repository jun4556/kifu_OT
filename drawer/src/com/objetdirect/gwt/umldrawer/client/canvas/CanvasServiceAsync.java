package com.objetdirect.gwt.umldrawer.client.canvas;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.objetdirect.gwt.umldrawer.client.beans.EditEvent;

//public interface CanvasServiceAsync {
//	@SuppressWarnings("rawtypes")
//	public void saveCanvas(String studentId, int exercisesId, String canvasUrl, AsyncCallback callback);
//	@SuppressWarnings("rawtypes")
//	public void loadCanvas(String studentId, int exercisesId, AsyncCallback callback);
//	@SuppressWarnings("rawtypes")
//	public void undo(String studentId, int exercisesId, AsyncCallback callback);
//	@SuppressWarnings("rawtypes")
//	public void saveCanvasAsAnswer(String studentId, int exercisesId, String canvasUrl, AsyncCallback callback);
//	//@gwt.typeArgs <com.objetdirect.gwt.umlapi.client.artifacts.UMLArtifact[]>
//	public void getAnswer(int exerciseId, AsyncCallback callback);
//
//}

public interface CanvasServiceAsync {

	// --- 既存のメソッドの非同期版 ---
	void saveCanvas(String studentId, int exercisesId, String canvasUrl, AsyncCallback<Void> callback);

	void loadCanvas(String studentId, int exercisesId, AsyncCallback<EditEvent> callback);

	void undo(String studentId, int exercisesId, AsyncCallback<EditEvent> callback);

	void saveCanvasAsAnswer(String studentId, int exercisesId, String canvasUrl, AsyncCallback<Boolean> callback);

	void getAnswer(int exerciseId, AsyncCallback<String> callback);
	
	// --- マージ機能のために追加するメソッドの非同期版 ---
	void mergeCanvas(String myUrl, String opponentUrl, AsyncCallback<String> callback);
	
//	void getRecentSaves(int exerciseId, String currentStudentId, AsyncCallback<List<SaveEventInfo>> callback);
//	
	void saveMergedCanvas(String commonId, int exerciseId, String canvasUrl, AsyncCallback<Void> callback);

}
