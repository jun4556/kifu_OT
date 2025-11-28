package com.objetdirect.gwt.umldrawer.client.helpers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.objetdirect.gwt.umldrawer.client.beans.EditOperation;

/**
 * Operational Transformation操作を送信するヘルパークラス
 */
public class OperationTransformHelper {
    
    private WebSocketClient webSocketClient;
    private int clientSequence = 0;
    private int lastServerSequence = 0;
    private String userId;
    private int exerciseId;
    
    public OperationTransformHelper(WebSocketClient webSocketClient, String userId, int exerciseId) {
        this.webSocketClient = webSocketClient;
        this.userId = userId;
        this.exerciseId = exerciseId;
    }
    
    /**
     * テキスト変更をOT方式で送信
     * * @param elementId 要素ID
     * @param partId パートID（name, stereotype等）
     * @param beforeText 変更前テキスト
     * @param afterText 変更後テキスト
     */
    public void sendTextChangeWithOT(String elementId, String partId, String beforeText, String afterText) {
        clientSequence++;
        
        // --- 修正箇所: パッチ生成プロセス ---
        DiffMatchPatchGwt dmp = new DiffMatchPatchGwt();
        
        // 1. パッチオブジェクト(JavaScriptObject)を生成
        JavaScriptObject patches = dmp.patch_make(beforeText, afterText);
        
        // 2. パッチオブジェクトを文字列(String)に変換
        String patchText = dmp.patch_toText(patches);
        // --------------------------------
        
        // JSONメッセージを構築
        JSONObject message = new JSONObject();
        message.put("action", new JSONString("editOperation"));
        message.put("clientSequence", new JSONNumber(clientSequence));
        message.put("basedOnServerSequence", new JSONNumber(lastServerSequence));
        message.put("userId", new JSONString(userId));
        message.put("elementId", new JSONString(elementId));
        message.put("partId", new JSONString(partId != null ? partId : ""));
        message.put("operationType", new JSONString("text_update"));
        message.put("patchText", new JSONString(patchText));
        message.put("beforeText", new JSONString(beforeText != null ? beforeText : ""));
        message.put("afterText", new JSONString(afterText != null ? afterText : ""));
        message.put("exerciseId", new JSONNumber(exerciseId));
        message.put("timestamp", new JSONNumber(System.currentTimeMillis()));
        
        // WebSocket経由で送信
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message.toString());
        }
    }
    
    /**
     * 移動をOT方式で送信
     * @param elementId 要素ID
     * @param oldX 移動前X座標
     * @param oldY 移動前Y座標
     * @param deltaX X方向移動量
     * @param deltaY Y方向移動量
     */
    public void sendMoveOperation(String elementId, int oldX, int oldY, int deltaX, int deltaY) {
        clientSequence++;
        
        // JSONメッセージを構築
        JSONObject message = new JSONObject();
        message.put("action", new JSONString("editOperation"));
        message.put("clientSequence", new JSONNumber(clientSequence));
        message.put("basedOnServerSequence", new JSONNumber(lastServerSequence));
        message.put("userId", new JSONString(userId));
        message.put("elementId", new JSONString(elementId));
        message.put("operationType", new JSONString("move_delta"));
        message.put("oldX", new JSONNumber(oldX));
        message.put("oldY", new JSONNumber(oldY));
        message.put("deltaX", new JSONNumber(deltaX));
        message.put("deltaY", new JSONNumber(deltaY));
        message.put("exerciseId", new JSONNumber(exerciseId));
        message.put("timestamp", new JSONNumber(System.currentTimeMillis()));
        
        // WebSocket経由で送信
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message.toString());
        }
    }
    
    /**
     * サーバーから受信した操作を適用
     * * @param operation サーバーから受信した操作
     * @return 適用後のテキスト
     */
    public String applyServerOperation(EditOperation operation) {
        // サーバーシーケンスを更新
        if (operation.getServerSequence() > lastServerSequence) {
            lastServerSequence = operation.getServerSequence();
        }
        
        // 自分の操作の場合は、既に適用済みなので何もしない
        if (userId != null && userId.equals(operation.getUserId())) {
            return operation.getAfterText();
        }
        
        // 他ユーザーの操作の場合は、パッチを適用
        return operation.getAfterText();
    }
    
    /**
     * クライアントシーケンス番号を取得
     */
    public int getClientSequence() {
        return clientSequence;
    }
    
    /**
     * 最新のサーバーシーケンス番号を取得
     */
    public int getLastServerSequence() {
        return lastServerSequence;
    }
    
    /**
     * サーバーシーケンス番号を設定（同期時に使用）
     */
    public void setLastServerSequence(int serverSequence) {
        this.lastServerSequence = serverSequence;
    }
    
    /**
     * クライアントシーケンスをリセット
     */
    public void resetClientSequence() {
        this.clientSequence = 0;
    }
}