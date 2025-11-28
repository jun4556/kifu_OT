package com.objetdirect.gwt.umldrawer.client.beans;

import java.io.Serializable;

/**
 * クライアントからサーバーへ送信される編集操作を表すクラス。
 * Operational Transformation (OT) 方式で同時編集の競合を解決するために使用。
 */
public class EditOperation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** クライアント側のシーケンス番号 */
    private int clientSequence;
    
    /** サーバー側で割り当てられるグローバルシーケンス番号 */
    private int serverSequence;
    
    /** 操作を行ったユーザーのID */
    private String userId;
    
    /** 操作を行ったセッションID */
    private String sessionId;
    
    /** 編集対象の要素ID (例: "element-123") */
    private String elementId;
    
    /** 編集対象の部分ID (例: "ClassPartNameArtifact-456") */
    private String partId;
    
    /** 操作の種類 ("patch", "insert", "delete" など) */
    private String operationType;
    
    /** diff-match-patchで生成されたパッチテキスト */
    private String patchText;
    
    /** 操作前のテキスト（検証用） */
    private String beforeText;
    
    /** 操作後のテキスト（検証用） */
    private String afterText;
    
    /** 操作のタイムスタンプ */
    private long timestamp;
    
    /** この操作が依存する直前のサーバーシーケンス番号 */
    private int basedOnServerSequence;
    
    /** エクササイズID（課題ID） */
    private int exerciseId;
    
    /** 移動操作用: 移動前X座標 */
    private int oldX;
    
    /** 移動操作用: 移動前Y座標 */
    private int oldY;
    
    /** 移動操作用: X方向移動量 */
    private int deltaX;
    
    /** 移動操作用: Y方向移動量 */
    private int deltaY;
    
    public EditOperation() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    
    public int getClientSequence() {
        return clientSequence;
    }
    
    public void setClientSequence(int clientSequence) {
        this.clientSequence = clientSequence;
    }
    
    public int getServerSequence() {
        return serverSequence;
    }
    
    public void setServerSequence(int serverSequence) {
        this.serverSequence = serverSequence;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getElementId() {
        return elementId;
    }
    
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
    
    public String getPartId() {
        return partId;
    }
    
    public void setPartId(String partId) {
        this.partId = partId;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public String getPatchText() {
        return patchText;
    }
    
    public void setPatchText(String patchText) {
        this.patchText = patchText;
    }
    
    public String getBeforeText() {
        return beforeText;
    }
    
    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }
    
    public String getAfterText() {
        return afterText;
    }
    
    public void setAfterText(String afterText) {
        this.afterText = afterText;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getBasedOnServerSequence() {
        return basedOnServerSequence;
    }
    
    public void setBasedOnServerSequence(int basedOnServerSequence) {
        this.basedOnServerSequence = basedOnServerSequence;
    }
    
    public int getExerciseId() {
        return exerciseId;
    }
    
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
    
    public int getOldX() {
        return oldX;
    }
    
    public void setOldX(int oldX) {
        this.oldX = oldX;
    }
    
    public int getOldY() {
        return oldY;
    }
    
    public void setOldY(int oldY) {
        this.oldY = oldY;
    }
    
    public int getDeltaX() {
        return deltaX;
    }
    
    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }
    
    public int getDeltaY() {
        return deltaY;
    }
    
    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }
    
    @Override
    public String toString() {
        return "EditOperation{" +
                "clientSeq=" + clientSequence +
                ", serverSeq=" + serverSequence +
                ", userId='" + userId + '\'' +
                ", elementId='" + elementId + '\'' +
                ", partId='" + partId + '\'' +
                ", type='" + operationType + '\'' +
                ", basedOn=" + basedOnServerSequence +
                ", timestamp=" + timestamp +
                '}';
    }
}
