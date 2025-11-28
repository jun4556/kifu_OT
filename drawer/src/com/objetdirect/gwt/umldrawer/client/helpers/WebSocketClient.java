package com.objetdirect.gwt.umldrawer.client.helpers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.objetdirect.gwt.umldrawer.client.DrawerPanel;
import com.objetdirect.gwt.umlapi.client.helpers.WebSocketSender;


public class WebSocketClient implements WebSocketSender {

    private JavaScriptObject ws;
    private DrawerPanel drawerPanel;

    public WebSocketClient(DrawerPanel panel) {
        this.drawerPanel = panel;
    }

    public native void connect(String url) /*-{
        var self = this;
        var ws = new WebSocket(url);

        ws.onopen = function() {
            console.log("WebSocket connection opened.");
        };

        ws.onmessage = function(event) {
            // Javaの onMessage メソッドを呼び出す
            self.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::onMessage(Ljava/lang/String;)(event.data);
        };

        ws.onclose = function() {
            console.log("WebSocket connection closed.");
        };

        ws.onerror = function(error) {
            console.error("WebSocket Error: ", error);
        };

        this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws = ws;
    }-*/;

    public native void send(String message) /*-{
        if (this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws &&
            this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws.readyState === WebSocket.OPEN) {
            this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws.send(message);
        } else {
            console.error("WebSocket is not connected.");
        }
    }-*/;
    public native void disconnect() /*-{
        if (this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws) {
            this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws.close();
            this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws = null;
        }
    }-*/;
    
    public native boolean isOpen() /*-{
        return this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws &&
               this.@com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient::ws.readyState === WebSocket.OPEN;
    }-*/;
 // WebSocketClient.java の onMessage メソッドをこれに置き換える

    public void onMessage(String message) {
        try {
            JSONValue jsonValue = JSONParser.parseStrict(message);
            JSONObject jsonObject = jsonValue.isObject();

            if (jsonObject != null && jsonObject.containsKey("action")) {
                String action = jsonObject.get("action").isString().stringValue();

                if ("sync".equals(action)) {
                    String url = jsonObject.get("url").isString().stringValue();
                    // DrawerPanelからDrawerBaseへの参照を取得し、syncCanvasFromServerを呼び出す！
                    if (drawerPanel != null && drawerPanel.getDrawerBaseInstance()  != null) {
                         drawerPanel.getDrawerBaseInstance() .syncCanvasFromServer(url);
                    }
                }
                else if ("textUpdate".equals(action)) {
                    // "textUpdate"の荷物が届いたら、中身を取り出す
                    String elementId = jsonObject.get("elementId").isString().stringValue();
                    String partId = jsonObject.get("partId").isString().stringValue();
                    String newText = jsonObject.get("newText").isString().stringValue();

                    // DrawerPanelに、テキストを更新する新しい命令を出す！
                    if (drawerPanel != null) {
                        drawerPanel.updateArtifactText(elementId, partId, newText);
                    }
                }
                else if ("applyPatch".equals(action)) {
                    String elementId = jsonObject.get("elementId").isString().stringValue();
                    String partId = jsonObject.get("partId").isString().stringValue();
                    String patchText = jsonObject.get("patch").isString().stringValue();

                    if (drawerPanel != null) {
                        // DrawerPanelに、パッチを適用する新しい命令を出す！
                        drawerPanel.applyPatchToArtifactText(elementId, partId, patchText);
                    }
                }
                else if ("editOperationResponse".equals(action)) {
                    // OT方式の編集操作レスポンス
                    int serverSequence = (int) jsonObject.get("serverSequence").isNumber().doubleValue();
                    String elementId = jsonObject.get("elementId").isString().stringValue();
                    String partId = jsonObject.get("partId").isString().stringValue();
                    String afterText = jsonObject.get("afterText").isString().stringValue();
                    String userId = jsonObject.get("userId").isString().stringValue();
                    
                    // 自分の操作かどうかを判定
                    boolean isOwnOperation = jsonObject.containsKey("isOwnOperation") && 
                                            jsonObject.get("isOwnOperation").isBoolean().booleanValue();
                    
                    if (drawerPanel != null) {
                        // DrawerPanelのOTヘルパーを使用して操作を適用
                        drawerPanel.applyOTOperation(serverSequence, elementId, partId, afterText, userId, isOwnOperation);
                    }
                }
                else if ("moveOperationResponse".equals(action)) {
                    // OT方式の移動操作レスポンス
                    int serverSequence = (int) jsonObject.get("serverSequence").isNumber().doubleValue();
                    String elementId = jsonObject.get("elementId").isString().stringValue();
                    int oldX = (int) jsonObject.get("oldX").isNumber().doubleValue();
                    int oldY = (int) jsonObject.get("oldY").isNumber().doubleValue();
                    int deltaX = (int) jsonObject.get("deltaX").isNumber().doubleValue();
                    int deltaY = (int) jsonObject.get("deltaY").isNumber().doubleValue();
                    String userId = jsonObject.get("userId").isString().stringValue();
                    
                    // 自分の操作かどうかを判定
                    boolean isOwnOperation = jsonObject.containsKey("isOwnOperation") && 
                                            jsonObject.get("isOwnOperation").isBoolean().booleanValue();
                    
                    if (drawerPanel != null) {
                        // DrawerPanelのOTヘルパーを使用して移動を適用
                        drawerPanel.applyMoveOTOperation(serverSequence, elementId, oldX, oldY, deltaX, deltaY, userId, isOwnOperation);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("受信したメッセージの解析に失敗: " + message);
        }
    }
    
    /**
     * OT方式でテキスト変更を送信
     * WebSocketSenderインターフェースの実装
     */
    @Override
    public void sendTextChangeWithOT(String elementId, String partId, String beforeText, String afterText) {
        if (drawerPanel != null && drawerPanel.getOTHelper() != null) {
            drawerPanel.sendTextChangeWithOT(elementId, partId, beforeText, afterText);
        }
    }
    
    /**
     * OT方式で移動を送信
     * WebSocketSenderインターフェースの実装
     */
    @Override
    public void sendMoveWithOT(String elementId, int oldX, int oldY, int deltaX, int deltaY) {
        if (drawerPanel != null && drawerPanel.getOTHelper() != null) {
            drawerPanel.sendMoveWithOT(elementId, oldX, oldY, deltaX, deltaY);
        }
    }
}