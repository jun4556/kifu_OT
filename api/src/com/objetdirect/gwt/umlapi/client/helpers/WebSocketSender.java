package com.objetdirect.gwt.umlapi.client.helpers;

/**
 * サーバーにメッセージを送る、という責務だけを定義した"契約書"だ。
 * drawerプロジェクトが、この契約を実装することになる。
 */
public interface WebSocketSender {
    void send(String message);
    
    /**
     * OT方式でテキスト変更を送信
     * @param elementId 要素ID
     * @param partId パートID
     * @param beforeText 変更前テキスト
     * @param afterText 変更後テキスト
     */
    void sendTextChangeWithOT(String elementId, String partId, String beforeText, String afterText);
    
    /**
     * OT方式で移動を送信
     * @param elementId 要素ID
     * @param oldX 移動前X座標
     * @param oldY 移動前Y座標
     * @param deltaX X方向移動量
     * @param deltaY Y方向移動量
     */
    void sendMoveWithOT(String elementId, int oldX, int oldY, int deltaX, int deltaY);
}