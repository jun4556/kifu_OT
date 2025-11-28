package com.objetdirect.gwt.umldrawer.server.collaboration;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.objetdirect.gwt.umldrawer.client.beans.EditOperation;
import com.objetdirect.gwt.umldrawer.server.dao.Dao;

/**
 * WebSocketエンドポイント
 * クライアントからの編集操作を受信し、OT方式で処理して他のクライアントに配信
 */
@ServerEndpoint("/collaboration")
public class CollaborationWebSocket {
    
    private static final Logger logger = Logger.getLogger(CollaborationWebSocket.class.getName());
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final Gson gson = new Gson();
    private static final OperationManager operationManager = OperationManager.getInstance();
    
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        logger.info("WebSocket接続が確立されました。セッションID: " + session.getId());
    }
    
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        logger.info("WebSocket接続が切断されました。セッションID: " + session.getId());
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.severe("WebSocketエラー: " + throwable.getMessage());
        throwable.printStackTrace();
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(message).getAsJsonObject();
            
            String action = json.get("action").getAsString();
            
            if ("editOperation".equals(action)) {
                handleEditOperation(json, session);
            }
            else if ("sync".equals(action)) {
                // キャンバス全体の同期: 他のクライアントにブロードキャスト
                broadcastToOthers(message, session);
                logger.info("syncメッセージをブロードキャストしました");
            }
            else if ("textUpdate".equals(action)) {
                // テキスト更新: 他のクライアントにブロードキャスト
                broadcastToOthers(message, session);
                logger.info("textUpdateメッセージをブロードキャストしました");
            }
            else if ("applyPatch".equals(action)) {
                // パッチ適用: 他のクライアントにブロードキャスト
                broadcastToOthers(message, session);
                logger.info("applyPatchメッセージをブロードキャストしました");
            }
            else {
                logger.warning("不明なアクション: " + action);
            }
            
        } catch (Exception e) {
            logger.severe("メッセージ処理エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 送信者以外の全クライアントにメッセージをブロードキャスト
     * sync, textUpdate, applyPatch メッセージの配信に使用
     */
    private void broadcastToOthers(String message, Session senderSession) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen() && !session.equals(senderSession)) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        logger.warning("メッセージ送信エラー (セッション: " + session.getId() + "): " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * 編集操作を処理
     */
    private void handleEditOperation(JsonObject json, Session senderSession) {
        try {
            // 操作タイプを確認
            String operationType = json.has("operationType") ? json.get("operationType").getAsString() : "text_update";
            
            if ("move_delta".equals(operationType)) {
                handleMoveOperation(json, senderSession);
            } else {
                // テキスト編集操作
                EditOperation operation = parseEditOperation(json);
                EditOperation processedOp = operationManager.processOperation(operation);
                saveOperationToDatabase(processedOp);
                broadcastOperation(processedOp, senderSession);
            }
            
        } catch (Exception e) {
            logger.severe("編集操作の処理エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 移動操作を処理
     */
    private void handleMoveOperation(JsonObject json, Session senderSession) {
        try {
            // JSONから移動操作を構築
            EditOperation operation = parseMoveOperation(json);
            
            // OperationManagerで処理（delta合成）
            EditOperation processedOp = operationManager.transformMoveOperation(operation);
            
            // DBに保存
            saveMoveOperationToDatabase(processedOp);
            
            // 全クライアントに配信
            broadcastMoveOperation(processedOp, senderSession);
            
        } catch (Exception e) {
            logger.severe("移動操作の処理エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * JSONから移動操作を構築
     */
    private EditOperation parseMoveOperation(JsonObject json) {
        EditOperation op = new EditOperation();
        
        if (json.has("clientSequence")) {
            op.setClientSequence(json.get("clientSequence").getAsInt());
        }
        if (json.has("basedOnServerSequence")) {
            op.setBasedOnServerSequence(json.get("basedOnServerSequence").getAsInt());
        }
        if (json.has("userId")) {
            op.setUserId(json.get("userId").getAsString());
        }
        if (json.has("elementId")) {
            op.setElementId(json.get("elementId").getAsString());
        }
        op.setOperationType("move_delta");
        
        if (json.has("oldX")) {
            op.setOldX(json.get("oldX").getAsInt());
        }
        if (json.has("oldY")) {
            op.setOldY(json.get("oldY").getAsInt());
        }
        if (json.has("deltaX")) {
            op.setDeltaX(json.get("deltaX").getAsInt());
        }
        if (json.has("deltaY")) {
            op.setDeltaY(json.get("deltaY").getAsInt());
        }
        if (json.has("exerciseId")) {
            op.setExerciseId(json.get("exerciseId").getAsInt());
        }
        if (json.has("timestamp")) {
            op.setTimestamp(json.get("timestamp").getAsLong());
        }
        
        return op;
    }
    
    /**
     * JSONからEditOperationを構築
     */
    private EditOperation parseEditOperation(JsonObject json) {
        EditOperation op = new EditOperation();
        
        if (json.has("clientSequence")) {
            op.setClientSequence(json.get("clientSequence").getAsInt());
        }
        if (json.has("basedOnServerSequence")) {
            op.setBasedOnServerSequence(json.get("basedOnServerSequence").getAsInt());
        }
        if (json.has("userId")) {
            op.setUserId(json.get("userId").getAsString());
        }
        if (json.has("sessionId")) {
            op.setSessionId(json.get("sessionId").getAsString());
        }
        if (json.has("elementId")) {
            op.setElementId(json.get("elementId").getAsString());
        }
        if (json.has("partId")) {
            op.setPartId(json.get("partId").getAsString());
        }
        if (json.has("operationType")) {
            op.setOperationType(json.get("operationType").getAsString());
        }
        if (json.has("patchText")) {
            op.setPatchText(json.get("patchText").getAsString());
        }
        if (json.has("beforeText")) {
            op.setBeforeText(json.get("beforeText").getAsString());
        }
        if (json.has("afterText")) {
            op.setAfterText(json.get("afterText").getAsString());
        }
        if (json.has("exerciseId")) {
            op.setExerciseId(json.get("exerciseId").getAsInt());
        }
        if (json.has("timestamp")) {
            op.setTimestamp(json.get("timestamp").getAsLong());
        }
        
        return op;
    }
    
    /**
     * 移動操作をDBに保存
     */
    private void saveMoveOperationToDatabase(EditOperation operation) {
        java.sql.Connection connection = null;
        java.sql.PreparedStatement stmt = null;
        
        try {
            Dao dao = new Dao();
            connection = dao.createConnection();
            
            // operation_logテーブルに保存
            String sql = "INSERT INTO operation_log " +
                        "(user_id, exercise_id, element_id, operation_type, " +
                        "old_x, old_y, delta_x, delta_y, " +
                        "client_sequence, server_sequence, based_on_sequence, " +
                        "timestamp, date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, operation.getUserId());
            stmt.setInt(2, operation.getExerciseId());
            stmt.setString(3, operation.getElementId());
            stmt.setString(4, operation.getOperationType());
            stmt.setInt(5, operation.getOldX());
            stmt.setInt(6, operation.getOldY());
            stmt.setInt(7, operation.getDeltaX());
            stmt.setInt(8, operation.getDeltaY());
            stmt.setInt(9, operation.getClientSequence());
            stmt.setInt(10, operation.getServerSequence());
            stmt.setInt(11, operation.getBasedOnServerSequence());
            stmt.setLong(12, operation.getTimestamp());
            
            stmt.executeUpdate();
            
            logger.info("移動操作をDBに保存しました。ServerSeq: " + operation.getServerSequence());
            
        } catch (Exception e) {
            logger.severe("移動操作DB保存エラー: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                logger.warning("リソースクローズ失敗: " + e.getMessage());
            }
        }
    }
    
    /**
     * 全クライアントに移動操作を配信
     */
    private void broadcastMoveOperation(EditOperation operation, Session senderSession) {
        JsonObject response = new JsonObject();
        response.addProperty("action", "moveOperationResponse");
        response.addProperty("serverSequence", operation.getServerSequence());
        response.addProperty("elementId", operation.getElementId());
        response.addProperty("oldX", operation.getOldX());
        response.addProperty("oldY", operation.getOldY());
        response.addProperty("deltaX", operation.getDeltaX());
        response.addProperty("deltaY", operation.getDeltaY());
        response.addProperty("userId", operation.getUserId());
        
        String jsonString = gson.toJson(response);
        
        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    // 送信者には「自分の操作」フラグを付与
                    if (session.equals(senderSession)) {
                        JsonObject selfResponse = new JsonObject();
                        selfResponse.addProperty("action", "moveOperationResponse");
                        selfResponse.addProperty("serverSequence", operation.getServerSequence());
                        selfResponse.addProperty("elementId", operation.getElementId());
                        selfResponse.addProperty("oldX", operation.getOldX());
                        selfResponse.addProperty("oldY", operation.getOldY());
                        selfResponse.addProperty("deltaX", operation.getDeltaX());
                        selfResponse.addProperty("deltaY", operation.getDeltaY());
                        selfResponse.addProperty("userId", operation.getUserId());
                        selfResponse.addProperty("isOwnOperation", true);
                        session.getBasicRemote().sendText(gson.toJson(selfResponse));
                    } else {
                        session.getBasicRemote().sendText(jsonString);
                    }
                } catch (IOException e) {
                    logger.warning("移動操作配信失敗: " + e.getMessage());
                }
            }
        }
        
        logger.info("移動操作を全クライアントに配信しました。ServerSeq: " + operation.getServerSequence());
    }
    
    /**
     * 処理された操作をDBに保存
     */
    private void saveOperationToDatabase(EditOperation operation) {
        java.sql.Connection connection = null;
        java.sql.PreparedStatement stmt = null;
        
        try {
            Dao dao = new Dao();
            connection = dao.createConnection();
            
            // operation_logテーブルに保存
            String sql = "INSERT INTO operation_log " +
                        "(user_id, exercise_id, element_id, part_id, " +
                        "operation_type, patch_text, before_text, after_text, " +
                        "client_sequence, server_sequence, based_on_sequence, " +
                        "timestamp, date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, operation.getUserId());
            stmt.setInt(2, operation.getExerciseId());
            stmt.setString(3, operation.getElementId());
            stmt.setString(4, operation.getPartId());
            stmt.setString(5, operation.getOperationType());
            stmt.setString(6, operation.getPatchText());
            stmt.setString(7, operation.getBeforeText());
            stmt.setString(8, operation.getAfterText());
            stmt.setInt(9, operation.getClientSequence());
            stmt.setInt(10, operation.getServerSequence());
            stmt.setInt(11, operation.getBasedOnServerSequence());
            stmt.setLong(12, operation.getTimestamp());
            
            stmt.executeUpdate();
            
            logger.info("操作をDBに保存しました。ServerSeq: " + operation.getServerSequence());
            
        } catch (Exception e) {
            logger.severe("DB保存エラー: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                logger.warning("リソースクローズ失敗: " + e.getMessage());
            }
        }
    }
    
    /**
     * 全クライアントに操作を配信
     */
    private void broadcastOperation(EditOperation operation, Session senderSession) {
        JsonObject response = new JsonObject();
        response.addProperty("action", "editOperationResponse");
        response.addProperty("serverSequence", operation.getServerSequence());
        response.addProperty("elementId", operation.getElementId());
        response.addProperty("partId", operation.getPartId());
        response.addProperty("afterText", operation.getAfterText());
        response.addProperty("userId", operation.getUserId());
        response.addProperty("patchText", operation.getPatchText());
        
        String jsonString = gson.toJson(response);
        
        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    // 送信者には「自分の操作」フラグを付与
                    if (session.equals(senderSession)) {
                        JsonObject selfResponse = new JsonObject();
                        selfResponse.addProperty("action", "editOperationResponse");
                        selfResponse.addProperty("serverSequence", operation.getServerSequence());
                        selfResponse.addProperty("elementId", operation.getElementId());
                        selfResponse.addProperty("partId", operation.getPartId());
                        selfResponse.addProperty("afterText", operation.getAfterText());
                        selfResponse.addProperty("userId", operation.getUserId());
                        selfResponse.addProperty("patchText", operation.getPatchText());
                        selfResponse.addProperty("isOwnOperation", true);
                        session.getBasicRemote().sendText(gson.toJson(selfResponse));
                    } else {
                        session.getBasicRemote().sendText(jsonString);
                    }
                } catch (IOException e) {
                    logger.warning("メッセージ送信失敗: " + e.getMessage());
                }
            }
        }
        
        logger.info("操作を全クライアントに配信しました。ServerSeq: " + operation.getServerSequence());
    }
}
