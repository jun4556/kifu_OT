# 技術スタックとアーキテクチャ

## 技術構成

### フロントエンド
- **Google Web Toolkit (GWT) 2.8.2**
  - Java→JavaScriptトランスパイル
  - クライアント側UIコンポーネント
  - Dojo Toolkitウィジェット使用

### バックエンド
- **Java 8**
  - サーバー側ビジネスロジック
  - WebSocketエンドポイント
  - DAO層

### データベース
- **MySQL**
  - kifu データベース
  - operation_log テーブル（OT操作ログ）
  - その他のテーブル（kifu2.sql, kifu3.sql等）

### 通信
- **WebSocket (JSR 356)**
  - リアルタイム双方向通信
  - CollaborationWebSocket.java (@ServerEndpoint)
  - エンドポイント: ws://localhost:8080/collaboration

### 外部ライブラリ
1. **diff-match-patch 1.2**
   - テキスト差分計算・パッチ生成
   - Google製ライブラリ
2. **javax.websocket-api 1.1**
   - WebSocket API標準仕様
3. **gson 2.8.9**
   - JSON シリアライゼーション
4. **HikariCP**
   - データベースコネクションプール
   - hikari.properties で設定

## アーキテクチャ

### OT方式データフロー
```
[クライアント1] --editOperation--> [CollaborationWebSocket]
                                           |
                                           v
                                    [OperationManager]
                                    - processOperation()
                                    - transformOperation()
                                    - serverSequence割り当て
                                           |
                                           v
                                    [operation_log] (DB)
                                           |
                                           v
[クライアント1] <--editOperationResponse-- [broadcast]
[クライアント2] <--editOperationResponse--
```

### 主要コンポーネント

#### サーバー側
- **CollaborationWebSocket.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/server/collaboration/
  - 役割: WebSocketエンドポイント、メッセージ受信・配信
  
- **OperationManager.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/server/collaboration/
  - 役割: 操作キュー管理、トランスフォーム処理、シーケンス番号付与

#### クライアント側
- **EditOperation.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/client/beans/
  - 役割: 操作データ構造
  
- **OperationTransformHelper.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/
  - 役割: OT操作送受信ヘルパー
  
- **DiffMatchPatchGwtExtended.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/
  - 役割: パッチ生成・適用ラッパー
  
- **WebSocketMessageHandler.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/client/collaboration/
  - 役割: メッセージハンドラー

- **WebSocketClient.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/
  - 役割: WebSocket接続管理（OT対応）

- **DrawerPanel.java**
  - パス: drawer/src/com/objetdirect/gwt/umldrawer/client/
  - 役割: メイン描画パネル、OTヘルパー統合

## ビルドシステム

### Apache Ant
- **drawer/build.xml**: Drawerアプリケーションビルド
- **api/build.xml**: APIライブラリビルド

### 主要タスク
- `ant clean`: クラスファイル・生成物削除
- `ant javac`: Javaソースコンパイル
- `ant gwtc`: GWT→JavaScriptコンパイル
- `ant build`: フルビルド（javac + gwtc）
- `ant war`: WARファイル生成（KIfU4.war）

## デプロイメント構成
- Apache Tomcat 8以降必須（WebSocket対応）
- WARファイル: KIfU4.war
- デプロイ先: %CATALINA_HOME%\webapps\
