# WebSocketエンドポイントのデプロイ手順

## 概要
CollaborationWebSocket.javaは、Java EE 7のWebSocket API（@ServerEndpoint）を使用しています。

## 前提条件

### 1. サーバー要件
以下のいずれかのサーバーが必要です：
- Apache Tomcat 8.0以降（WebSocket対応）
- GlassFish 4以降
- WildFly 8以降
- Jetty 9.1以降（javax.websocket-api対応）

### 2. 必要なライブラリ
```xml
<!-- web.xmlのバージョンを3.1以上に更新 -->
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
```

### 3. javax.websocket-api の追加
drawer/war/WEB-INF/lib/ に以下を配置：
- javax.websocket-api-1.1.jar（または最新版）
- gson-2.8.x.jar（JSON処理用）

## デプロイ手順

### ステップ1: ライブラリの準備
```bash
cd drawer/war/WEB-INF/lib

# javax.websocket-apiをダウンロード（Maven Central等から）
# https://mvnrepository.com/artifact/javax.websocket/javax.websocket-api

# Gsonをダウンロード
# https://mvnrepository.com/artifact/com.google.code.gson/gson
```

### ステップ2: build.xmlの更新
```xml
<path id="project.class.path">
    <!-- 既存設定 -->
    <pathelement location="war/WEB-INF/classes"/>
    <pathelement location="${gwt.sdk}/gwt-user.jar"/>
    <fileset dir="${gwt.sdk}" includes="gwt-dev*.jar"/>
    
    <!-- WebSocket APIとGsonを追加 -->
    <fileset dir="war/WEB-INF/lib" includes="javax.websocket-api*.jar"/>
    <fileset dir="war/WEB-INF/lib" includes="gson*.jar"/>
    
    <!-- 既存のlib -->
    <fileset dir="war/WEB-INF/lib" includes="**/*.jar"/>
</path>
```

### ステップ3: ビルド
```bash
cd drawer
ant clean
ant build
```

### ステップ4: WebSocketエンドポイントの確認
エンドポイントURL: `ws://localhost:8080/collaboration`

クライアント側で接続:
```javascript
var ws = new WebSocket("ws://localhost:8080/collaboration");
```

## クライアント側の接続コード（DrawerPanel.java等で使用）

```java
// WebSocket接続を確立
String wsUrl = "ws://" + Window.Location.getHost() + "/collaboration";
webSocketClient = new WebSocketClient(this);
webSocketClient.connect(wsUrl);

// OTヘルパーを初期化
initializeOTHelper(currentUserId, currentExerciseId);
```

## トラブルシューティング

### エラー: ClassNotFoundException: javax.websocket.server.ServerEndpoint
**原因**: javax.websocket-apiがクラスパスにない  
**解決**: war/WEB-INF/lib/ にjavax.websocket-api-1.1.jarを配置

### エラー: WebSocket connection failed
**原因**: サーバーがWebSocketをサポートしていない  
**解決**: Tomcat 8以降を使用、またはJetty 9.1以降に切り替え

### エラー: Gson not found
**原因**: Gsonライブラリがクラスパスにない  
**解決**: war/WEB-INF/lib/ にgson-2.8.x.jarを配置

## セキュリティ考慮事項

1. **認証**: WebSocketエンドポイントに認証を追加
```java
@ServerEndpoint(value = "/collaboration", configurator = WebSocketConfigurator.class)
```

2. **CORS設定**: 必要に応じてCORS設定を追加

3. **メッセージサイズ制限**: 大きなメッセージへの対策
```java
@OnMessage(maxMessageSize = 1024 * 1024) // 1MB
public void onMessage(String message, Session session) { ... }
```

## 動作確認

### 1. サーバーログを確認
```
INFO: WebSocket接続が確立されました。セッションID: xxx
INFO: 操作をDBに保存しました。ServerSeq: 1
INFO: 操作を全クライアントに配信しました。ServerSeq: 1
```

### 2. クライアント側のコンソールを確認
```
WebSocket connection opened.
Received: {"action":"editOperationResponse","serverSequence":1,...}
```

## 参考資料
- Java WebSocket API: https://www.oracle.com/technical-resources/articles/java/jsr356.html
- Tomcat WebSocket: https://tomcat.apache.org/tomcat-8.5-doc/web-socket-howto.html
