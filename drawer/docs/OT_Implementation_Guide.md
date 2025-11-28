# Operational Transformation (OT) 実装ガイド

## 概要
このドキュメントは、KIfU_margeプロジェクトにOperational Transformation (OT)方式を実装し、リアルタイム共同編集時の競合問題を解決するための統合ガイドです。

## 問題の説明
**現状の問題**:
- 2人が同時にクラス図を編集すると、最初に変更を送信した方のみがDBに記録される
- 2番目のユーザーの変更は破棄される（last-write-wins）
- データ損失が発生する

**OT方式による解決**:
- 各操作にシーケンス番号を付与
- サーバー側で操作をトランスフォーム（変換）
- 全ユーザーの変更を保持し、矛盾なく統合

## アーキテクチャ

```
クライアント1                サーバー                  クライアント2
    |                          |                          |
    | editOperation(seq:1)     |                          |
    |------------------------->|                          |
    |                          | processOperation()       |
    |                          | serverSeq=1を割り当て     |
    |                          |------------------------->|
    |<-------------------------|  editOperationResponse   |
    |                          |                          |
    |                          |<-------------------------|
    |                          | editOperation(seq:1)     |
    |                          | transform()実行          |
    |                          | serverSeq=2を割り当て     |
    |<-------------------------|------------------------->|
    | editOperationResponse    |  editOperationResponse   |
```

## 実装ファイル一覧

### 1. データ構造
- **EditOperation.java**: 編集操作のデータ構造
  - 場所: `drawer/src/com/objetdirect/gwt/umldrawer/client/beans/`
  - 役割: クライアント/サーバー間で操作データを転送

### 2. サーバー側
- **OperationManager.java**: 操作の管理とトランスフォーム
  - 場所: `drawer/src/com/objetdirect/gwt/umldrawer/server/collaboration/`
  - 役割: 操作キュー管理、シーケンス番号付与、トランスフォーム実行

- **CollaborationWebSocket.java**: WebSocketエンドポイント
  - 場所: `drawer/src/com/objetdirect/gwt/umldrawer/server/collaboration/`
  - 役割: クライアントからの操作受信、全クライアントへの配信

### 3. クライアント側
- **DiffMatchPatchGwtExtended.java**: diff-match-patchの拡張ラッパー
  - 場所: `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/`
  - 役割: パッチ生成・適用

- **OperationTransformHelper.java**: OT操作の送受信ヘルパー
  - 場所: `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/`
  - 役割: 操作の送信、サーバー応答の処理

- **WebSocketMessageHandler.java**: メッセージハンドラー
  - 場所: `drawer/src/com/objetdirect/gwt/umldrawer/client/collaboration/`
  - 役割: WebSocketメッセージのルーティング

### 4. データベース
- **operation_log.sql**: 操作ログテーブル
  - 場所: `api/operation_log.sql`
  - 役割: 全操作履歴の永続化

### 5. ドキュメント
- **DiffMatchPatch_Installation.md**: diff-match-patchライブラリの導入手順
- **WebSocket_Deployment.md**: WebSocketのデプロイ手順

## セットアップ手順

### ステップ1: データベースのセットアップ
```sql
mysql -u root -p < api/operation_log.sql
```

### ステップ2: 必要なライブラリの追加

#### 2-1. diff-match-patch（サーバー側）
詳細は `drawer/docs/DiffMatchPatch_Installation.md` を参照

```bash
# JARファイルをダウンロード
wget https://github.com/google/diff-match-patch/releases/download/v1.2/diff-match-patch-1.2.jar

# libディレクトリに配置
cp diff-match-patch-1.2.jar drawer/war/WEB-INF/lib/
```

#### 2-2. javax.websocket-api
```bash
# Maven Centralからダウンロード
wget https://repo1.maven.org/maven2/javax/websocket/javax.websocket-api/1.1/javax.websocket-api-1.1.jar

# libディレクトリに配置
cp javax.websocket-api-1.1.jar drawer/war/WEB-INF/lib/
```

#### 2-3. Gson
```bash
wget https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar
cp gson-2.8.9.jar drawer/war/WEB-INF/lib/
```

### ステップ3: build.xmlの更新
`drawer/build.xml`のクラスパスに以下を追加:

```xml
<path id="project.class.path">
    <!-- 既存設定 -->
    <pathelement location="war/WEB-INF/classes"/>
    <pathelement location="${gwt.sdk}/gwt-user.jar"/>
    <fileset dir="${gwt.sdk}" includes="gwt-dev*.jar"/>
    
    <!-- 新規追加 -->
    <fileset dir="war/WEB-INF/lib" includes="diff-match-patch*.jar"/>
    <fileset dir="war/WEB-INF/lib" includes="javax.websocket-api*.jar"/>
    <fileset dir="war/WEB-INF/lib" includes="gson*.jar"/>
    
    <!-- 既存lib -->
    <fileset dir="war/WEB-INF/lib" includes="**/*.jar"/>
</path>
```

### ステップ4: WebSocketClient.javaの修正
`drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/WebSocketClient.java`の`onMessage`メソッドに以下を追加:

```java
// WebSocketClient_OT_addition.txtの内容をコピー
```
詳細は `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/WebSocketClient_OT_addition.txt` を参照

### ステップ5: DrawerPanel.javaの修正
`drawer/src/com/objetdirect/gwt/umldrawer/client/DrawerPanel.java`に以下を追加:

```java
// DrawerPanel_OT_methods.txtの内容をコピー
```
詳細は `drawer/src/com/objetdirect/gwt/umldrawer/client/DrawerPanel_OT_methods.txt` を参照

### ステップ6: ビルド
```bash
cd drawer
ant clean
ant build
```

### ステップ7: デプロイ
```bash
# Tomcat 8以降にデプロイ
cp drawer/war/*.war $CATALINA_HOME/webapps/
```

## 使用方法

### クライアント側の初期化
```java
// DrawerPanel.javaまたは適切な初期化箇所で

// WebSocket接続
String wsUrl = "ws://" + Window.Location.getHost() + "/collaboration";
webSocketClient = new WebSocketClient(this);
webSocketClient.connect(wsUrl);

// OTヘルパー初期化
initializeOTHelper(currentUserId, currentExerciseId);
```

### テキスト変更の送信
```java
// 従来のsendTextUpdate()の代わりに
sendTextChangeWithOT(elementId, partId, beforeText, afterText);
```

## テスト手順

### 1. 環境準備
- ブラウザを2つ開く（Chrome/Firefoxなど）
- 両方で同じ演習課題を開く
- WebSocket接続を確立

### 2. 同時編集テスト
1. ブラウザ1でクラス名を「Class1」→「ClassA」に変更
2. ほぼ同時にブラウザ2で同じクラス名を「Class1」→「ClassB」に変更
3. 期待結果: 両方の変更が記録され、最終的に「ClassB」（または適切にマージされた値）が表示される

### 3. ログ確認
```sql
-- DBで操作ログを確認
SELECT * FROM operation_log 
WHERE exercise_id = [your_exercise_id] 
ORDER BY server_sequence;

-- 両方の操作が記録されていることを確認
```

### 4. サーバーログ確認
```
INFO: WebSocket接続が確立されました。セッションID: xxx
INFO: 操作をDBに保存しました。ServerSeq: 1
INFO: 操作を全クライアントに配信しました。ServerSeq: 1
INFO: 操作をDBに保存しました。ServerSeq: 2
INFO: 操作を全クライアントに配信しました。ServerSeq: 2
```

## トラブルシューティング

### 問題: WebSocket接続エラー
**解決**:
- Tomcat 8以降を使用しているか確認
- `javax.websocket-api-1.1.jar`がクラスパスにあるか確認
- ブラウザのコンソールでエラーメッセージを確認

### 問題: 操作がDBに保存されない
**解決**:
- `operation_log`テーブルが存在するか確認
- DB接続情報（hikari.properties）が正しいか確認
- サーバーログでSQL実行エラーを確認

### 問題: 変更が他のクライアントに反映されない
**解決**:
- WebSocket接続が確立されているか確認
- ブラウザのコンソールで`editOperationResponse`が受信されているか確認
- `DrawerPanel.applyOTOperation()`が正しく実装されているか確認

## パフォーマンス考慮事項

### 操作キューのクリーンアップ
OperationManager.javaに定期的なクリーンアップを追加:

```java
// 古い操作を削除（例: 1時間以上前の操作）
private void cleanupOldOperations() {
    long cutoffTime = System.currentTimeMillis() - (60 * 60 * 1000);
    // キューから削除
}
```

### DBインデックスの最適化
```sql
-- exercise_id + server_sequenceでの検索を高速化
CREATE INDEX idx_exercise_seq ON operation_log(exercise_id, server_sequence);

-- ユーザーごとの操作履歴検索を高速化
CREATE INDEX idx_user_exercise ON operation_log(user_id, exercise_id);
```

## 今後の拡張

### 1. オフライン対応
- ローカルストレージに未送信操作を保存
- 再接続時に自動送信

### 2. 操作の取り消し/やり直し
- 操作履歴を使用したUndo/Redo機能

### 3. 衝突の可視化
- 他ユーザーの編集箇所をハイライト表示
- 衝突発生時の通知

## 参考資料
- [Google diff-match-patch](https://github.com/google/diff-match-patch)
- [Operational Transformation概要](https://en.wikipedia.org/wiki/Operational_transformation)
- [Java WebSocket API](https://www.oracle.com/technical-resources/articles/java/jsr356.html)
