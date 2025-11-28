# OT実装 統合チェックリスト

このチェックリストは、Operational Transformation (OT)実装を手動で統合するためのステップバイステップガイドです。

## Phase 1: ライブラリの準備

### ステップ1: ライブラリのダウンロード
- [ ] `download_libraries.bat`を実行
- [ ] `drawer/war/WEB-INF/lib/`に以下のJARファイルが存在することを確認:
  - `diff-match-patch-1.2.jar`
  - `javax.websocket-api-1.1.jar`
  - `gson-2.8.9.jar`

**手動ダウンロードが必要な場合:**
```bash
cd drawer\war\WEB-INF\lib
# 以下のURLからダウンロード
# diff-match-patch: https://repo1.maven.org/maven2/org/bitbucket/cowwoc/diff-match-patch/1.2/diff-match-patch-1.2.jar
# javax.websocket-api: https://repo1.maven.org/maven2/javax/websocket/javax.websocket-api/1.1/javax.websocket-api-1.1.jar
# gson: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar
```

### ステップ2: build.xmlの確認
- [ ] `drawer/build.xml`の`<path id="project.class.path">`セクションに以下が含まれることを確認:
```xml
<!-- OT方式の実装に必要なライブラリ -->
<fileset dir="war/WEB-INF/lib" includes="diff-match-patch*.jar"/>
<fileset dir="war/WEB-INF/lib" includes="javax.websocket-api*.jar"/>
<fileset dir="war/WEB-INF/lib" includes="gson*.jar"/>
```

## Phase 2: コード統合

### ステップ3: WebSocketClient.javaの修正
- [ ] `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/WebSocketClient.java`を開く
- [ ] `onMessage`メソッドの最後の`else if ("applyPatch"...)`ブロックの**後**に、以下のコードを追加:

```java
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
```

- [ ] `isOpen()`メソッドが追加されていることを確認（既に追加済み）

### ステップ4: DrawerPanel.javaの修正

#### 4-1: importステートメントの追加
- [ ] `drawer/src/com/objetdirect/gwt/umldrawer/client/DrawerPanel.java`を開く
- [ ] import文のセクション（約42行目）に以下を追加:
```java
import com.objetdirect.gwt.umldrawer.client.helpers.OperationTransformHelper;
import com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient;
```

#### 4-2: フィールドの追加
- [ ] クラスのフィールド宣言部（約55行目）で、以下の行を確認:
```java
private OperationTransformHelper otHelper; // OT操作送受信ヘルパー
```
（既に追加済みの場合はスキップ）

#### 4-3: メソッドの追加
- [ ] クラスの最後の`}`（約502行目）の**直前**に、`DrawerPanel_OT_methods_v2.txt`の内容を追加:
  - `initializeOTHelper()`
  - `sendTextChangeWithOT()`
  - `applyOTOperation()`
  - `getOTHelper()`
  - `getWebSocketClient()`

**注意:** WebSocketClientの取得方法は、プロジェクトの実装に合わせて調整が必要な場合があります。

### ステップ5: DrawerBase.javaの確認（必要に応じて）
- [ ] `DrawerBase.java`に`getWebSocketClient()`メソッドが存在するか確認
- [ ] 存在しない場合は、WebSocketClientのインスタンスを返すメソッドを追加

## Phase 3: データベースセットアップ

### ステップ6: operation_logテーブルの作成
- [ ] MySQLサーバーが起動していることを確認
- [ ] `setup_database.bat`を実行、またはMySQLクライアントで直接実行:
```sql
mysql -u root -p < api/operation_log.sql
```

- [ ] テーブルが作成されたことを確認:
```sql
USE kifu;  -- または使用しているDB名
DESCRIBE operation_log;
```

期待される構造:
```
+---------------------+--------------+
| Field               | Type         |
+---------------------+--------------+
| id                  | int          |
| user_id             | varchar(255) |
| exercise_id         | int          |
| element_id          | varchar(255) |
| part_id             | varchar(255) |
| operation_type      | varchar(50)  |
| patch_text          | text         |
| before_text         | text         |
| after_text          | text         |
| client_sequence     | int          |
| server_sequence     | int          |
| based_on_sequence   | int          |
| timestamp           | bigint       |
| date                | datetime     |
+---------------------+--------------+
```

## Phase 4: ビルドとデプロイ

### ステップ7: プロジェクトのビルド
```bash
cd drawer
ant clean
ant build
```

- [ ] ビルドが成功することを確認
- [ ] エラーが出る場合は、以下を確認:
  - JARファイルが正しく配置されているか
  - import文が正しいか
  - メソッドの追加位置が正しいか

### ステップ8: WARファイルの作成
```bash
ant war
```

- [ ] `KIfU4.war`が生成されることを確認

### ステップ9: Tomcatへのデプロイ
- [ ] Tomcat 8以降がインストールされていることを確認
- [ ] WARファイルをTomcatのwebappsディレクトリにコピー:
```bash
copy KIfU4.war %CATALINA_HOME%\webapps\
```

- [ ] Tomcatを起動:
```bash
%CATALINA_HOME%\bin\startup.bat
```

## Phase 5: 動作確認

### ステップ10: WebSocket接続の確認
- [ ] ブラウザで`http://localhost:8080/KIfU4/`を開く
- [ ] ブラウザのDevToolsコンソールを開く
- [ ] 以下のメッセージを確認:
```
WebSocket connection opened.
```

### ステップ11: OT機能の初期化
- [ ] クラス図を開く
- [ ] DrawerPanelで`initializeOTHelper(userId, exerciseId)`が呼び出されているか確認
  - 必要に応じて、適切な場所（WebSocket接続後）で手動呼び出しを追加

### ステップ12: 同時編集テスト
1. [ ] 2つのブラウザウィンドウで同じ演習課題を開く
2. [ ] ウィンドウ1でクラス名を変更（例: "Class1" → "ClassA"）
3. [ ] **ほぼ同時に**ウィンドウ2でも変更（例: "Class1" → "ClassB"）
4. [ ] 両方のウィンドウで変更が反映されることを確認
5. [ ] データベースを確認:
```sql
SELECT * FROM operation_log WHERE exercise_id = [演習ID] ORDER BY server_sequence;
```
6. [ ] 2つの操作が記録されていることを確認

### ステップ13: ログの確認
- [ ] Tomcatのログ（catalina.out）を確認:
```
INFO: WebSocket接続が確立されました。セッションID: xxx
INFO: 操作をDBに保存しました。ServerSeq: 1
INFO: 操作を全クライアントに配信しました。ServerSeq: 1
```

## トラブルシューティング

### 問題: ビルドエラー
**症状:** `ant build`でエラー  
**解決:**
- [ ] JARファイルが正しい場所にあるか確認
- [ ] build.xmlのクラスパス設定を確認
- [ ] import文が正しいか確認

### 問題: WebSocket接続エラー
**症状:** "WebSocket connection failed"  
**解決:**
- [ ] Tomcat 8以降を使用しているか確認
- [ ] `javax.websocket-api-1.1.jar`がクラスパスにあるか確認
- [ ] URLが正しいか確認（ws://localhost:8080/collaboration）

### 問題: 操作がDBに保存されない
**症状:** operation_logテーブルにデータが入らない  
**解決:**
- [ ] operation_logテーブルが存在するか確認
- [ ] DB接続情報（hikari.properties）が正しいか確認
- [ ] Tomcatログでエラーメッセージを確認

### 問題: 他のクライアントに変更が反映されない
**症状:** 同時編集しても片方しか更新されない  
**解決:**
- [ ] WebSocket接続が確立されているか確認
- [ ] ブラウザコンソールで`editOperationResponse`が受信されているか確認
- [ ] `applyOTOperation()`メソッドが正しく呼び出されているか確認

## 完了チェック

すべてのステップが完了したら、以下を確認:

- [ ] ライブラリが正しくインストールされている
- [ ] コードの統合が完了している
- [ ] データベーステーブルが作成されている
- [ ] ビルドが成功している
- [ ] Tomcatにデプロイされている
- [ ] WebSocket接続が確立されている
- [ ] 同時編集で両方の変更が保存されている

**おめでとうございます！OT実装の統合が完了しました。**

## 次のステップ

- [ ] パフォーマンステスト（3人以上の同時編集）
- [ ] diff-match-patchの詳細実装（OperationManager_patch_implementation.txtを参照）
- [ ] エラーハンドリングの強化
- [ ] ユーザーフィードバック機能の追加（衝突通知など）

## 参考ドキュメント

- OT_Implementation_Guide.md - 総合実装ガイド
- DiffMatchPatch_Installation.md - diff-match-patchライブラリ導入手順
- WebSocket_Deployment.md - WebSocketデプロイ手順
- OperationManager_patch_implementation.txt - パッチトランスフォーム詳細実装
