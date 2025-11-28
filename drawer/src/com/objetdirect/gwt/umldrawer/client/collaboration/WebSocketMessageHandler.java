package com.objetdirect.gwt.umldrawer.client.collaboration;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.objetdirect.gwt.umldrawer.client.DrawerPanel;

/**
 * WebSocketメッセージを処理するハンドラー
 * OT方式の編集操作メッセージを含む、様々なアクションを処理
 */
public class WebSocketMessageHandler {
    
    private DrawerPanel drawerPanel;
    
    public WebSocketMessageHandler(DrawerPanel drawerPanel) {
        this.drawerPanel = drawerPanel;
    }
    
    /**
     * 受信したメッセージを解析して適切なアクションを実行
     * * @param message JSONフォーマットのメッセージ
     */
    public void handleMessage(String message) {
        try {
            JSONValue jsonValue = JSONParser.parseStrict(message);
            JSONObject jsonObject = jsonValue.isObject();
            
            if (jsonObject != null && jsonObject.containsKey("action")) {
                String action = jsonObject.get("action").isString().stringValue();
                
                if ("editOperationResponse".equals(action)) {
                    handleEditOperationResponse(jsonObject);
                }
                else if ("sync".equals(action)) {
                    handleSync(jsonObject);
                }
                else if ("textUpdate".equals(action)) {
                    handleTextUpdate(jsonObject);
                }
                else if ("applyPatch".equals(action)) {
                    handleApplyPatch(jsonObject);
                }
            }
        } catch (Exception e) {
            System.err.println("メッセージの解析に失敗: " + message);
            e.printStackTrace();
        }
    }
    
    /**
     * OT方式で処理された編集操作の応答を処理
     */
    private void handleEditOperationResponse(JSONObject json) {
        try {
            int serverSequence = (int) json.get("serverSequence").isNumber().doubleValue();
            String elementId = json.get("elementId").isString().stringValue();
            String partId = json.get("partId").isString().stringValue();
            String afterText = json.get("afterText").isString().stringValue();
            String userId = json.get("userId").isString().stringValue();
            
            // 他のユーザーの操作の場合のみ適用
            // （自分の操作は既にローカルで楽観的に適用済み）
            boolean isOwnOperation = json.containsKey("isOwnOperation") && 
                                     json.get("isOwnOperation").isBoolean().booleanValue();
            
            if (drawerPanel != null) {
                // 修正箇所: DrawerPanelのメソッド名と引数に合わせて修正
                drawerPanel.applyOTOperation(serverSequence, elementId, partId, afterText, userId, isOwnOperation);
            }
            
        } catch (Exception e) {
            System.err.println("editOperationResponseの処理に失敗");
            e.printStackTrace();
        }
    }
    
    /**
     * Canvas全体の同期リクエストを処理
     */
    private void handleSync(JSONObject json) {
        try {
            String url = json.get("url").isString().stringValue();
            if (drawerPanel != null && drawerPanel.getDrawerBaseInstance() != null) {
                // DrawerBaseにこのメソッドが存在することを確認してください
                drawerPanel.getDrawerBaseInstance().syncCanvasFromServer(url);
            }
        } catch (Exception e) {
            System.err.println("syncの処理に失敗");
            e.printStackTrace();
        }
    }
    
    /**
     * テキスト更新リクエストを処理（旧方式）
     */
    private void handleTextUpdate(JSONObject json) {
        try {
            String elementId = json.get("elementId").isString().stringValue();
            String partId = json.get("partId").isString().stringValue();
            String newText = json.get("newText").isString().stringValue();
            
            if (drawerPanel != null) {
                drawerPanel.updateArtifactText(elementId, partId, newText);
            }
        } catch (Exception e) {
            System.err.println("textUpdateの処理に失敗");
            e.printStackTrace();
        }
    }
    
    /**
     * パッチ適用リクエストを処理（旧方式）
     */
    private void handleApplyPatch(JSONObject json) {
        try {
            String elementId = json.get("elementId").isString().stringValue();
            String partId = json.get("partId").isString().stringValue();
            String patchText = json.get("patch").isString().stringValue();
            
            if (drawerPanel != null) {
                drawerPanel.applyPatchToArtifactText(elementId, partId, patchText);
            }
        } catch (Exception e) {
            System.err.println("applyPatchの処理に失敗");
            e.printStackTrace();
        }
    }
}