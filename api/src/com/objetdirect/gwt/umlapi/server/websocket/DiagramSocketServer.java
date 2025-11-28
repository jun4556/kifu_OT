package com.objetdirect.gwt.umlapi.server.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/diagram/{exerciseId}")
public class DiagramSocketServer {
    private static Map<String, Set<Session>> rooms = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("exerciseId") String exerciseId) {
        System.out.println("新しい接続: " + session.getId() + " が演習 '" + exerciseId + "' に参加しました。");

        // 演習IDをセッションに保存する
        session.getUserProperties().put("exerciseId", exerciseId);

        // その部屋用のセッションSetがなければ新規作成
        rooms.putIfAbsent(exerciseId, new CopyOnWriteArraySet<>());
        
        // 該当する演習IDの「部屋」に、新しい接続者（session）を追加
        rooms.get(exerciseId).add(session);
    }

    /**
     * メッセージを受け取ったら、何も解析せず、
     * そのまま同じ演習室の自分以外全員にブロードキャストする
     */
    @OnMessage
    public void onMessage(String message, Session session) { 
        String exerciseId = (String) session.getUserProperties().get("exerciseId");

        if (exerciseId != null) {
            System.out.println("メッセージ受信 from " + session.getId() + " in 演習 '" + exerciseId + "'");
            broadcast(message, session, exerciseId);
        } else {
            System.err.println("演習IDが不明なセッションからメッセージを受信: " + session.getId());
        }
    }

    @OnClose
    public void onClose(Session session) {
        String exerciseId = (String) session.getUserProperties().get("exerciseId");

        if (exerciseId != null) {
            System.out.println("接続が切れた: " + session.getId() + " が演習 '" + exerciseId + "' から退出しました。");
            Set<Session> room = rooms.get(exerciseId);
            if (room != null) {
                room.remove(session);
                if (room.isEmpty()) {
                    rooms.remove(exerciseId);
                    System.out.println("演習 '" + exerciseId + "' の部屋は空になりました。");
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        String exerciseId = (String) session.getUserProperties().get("exerciseId");
        System.err.println("エラー発生 in 演習 '" + exerciseId + "' (Session: " + session.getId() + ")");
        error.printStackTrace();
    }
    private void broadcast(String message, Session fromSession, String exerciseId) {
        Set<Session> room = rooms.get(exerciseId);
        if (room != null) {
            for (Session s : room) {
                if (s.isOpen() && !s.equals(fromSession)) {
                    try {
                        s.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}