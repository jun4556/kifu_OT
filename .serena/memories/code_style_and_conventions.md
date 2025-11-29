# コードスタイルと規約

## 言語とバージョン
- Java 8
- GWT 2.8.2

## ファイル編成

### パッケージ構成

#### API側（api/src/）
```
com.objetdirect.gwt.umlapi.
├── client/           # GWTクライアント側コード
├── server/           # サーバー側コード
│   └── dao/          # Data Access Object
└── shared/           # クライアント・サーバー共有コード
```

#### Drawer側（drawer/src/）
```
com.objetdirect.gwt.umldrawer.
├── client/           # GWTクライアント側コード
│   ├── beans/        # データ構造
│   ├── helpers/      # ヘルパークラス
│   ├── collaboration/ # 共同編集機能
│   └── <その他UI関連>
├── server/           # サーバー側コード
│   └── collaboration/ # WebSocketエンドポイント等
└── public/           # 静的リソース（CSS, JS, 画像等）
```

## 命名規則

### クラス名
- PascalCase
- 例: `EditOperation`, `OperationManager`, `DrawerPanel`
- インターフェース: I接頭辞なし（一般的なJavaスタイル）

### メソッド名
- camelCase
- 例: `processOperation()`, `sendTextChangeWithOT()`, `applyOTOperation()`

### フィールド名
- camelCase
- private フィールドが多い
- 例: `otHelper`, `serverSequence`, `clientSequence`

### 定数
- UPPER_SNAKE_CASE（推定）
- static final で宣言

### パッケージ名
- 小文字のみ
- 例: `com.objetdirect.gwt.umldrawer.client.beans`

## コーディング規約

### インデント
- タブまたはスペース（プロジェクト内で統一）
- build.xmlではタブ使用

### エンコーディング
- **UTF-8**
- build.xmlで明示的に指定: `encoding="utf-8"`

### ドキュメンテーション
- Javadoc形式のコメント使用（一部のファイルで確認）
- クラスレベル、メソッドレベルでドキュメント記述推奨

### アノテーション
- `@ServerEndpoint`: WebSocketエンドポイント
- `@OnOpen`, `@OnMessage`, `@OnClose`, `@OnError`: WebSocketイベントハンドラー
- GWT関連: クライアント側で特別な制約あり（Java標準ライブラリの一部使用不可）

## GWT固有の制約

### クライアント側コード
1. **使用可能なJavaライブラリが制限される**
   - GWTがサポートするJREエミュレーションのみ
   - java.util.*, java.lang.*の一部のみ
   
2. **GWT専用ライブラリ使用**
   - diff-match-patch: GWT互換バージョン必須
   
3. **シリアライゼーション**
   - `implements Serializable` または `IsSerializable`
   - GWT-RPCで使用されるオブジェクトに必要

4. **ネイティブJavaScript**
   - JSNIまたはJsInteropで記述可能

### サーバー側コード
- 通常のJavaコード
- javax.websocket-api使用可能
- DBアクセス、ファイルI/O等制限なし

## プロジェクト固有のパターン

### OT実装パターン
```java
// 1. 操作データ構造
public class EditOperation {
    private String userId;
    private int exerciseId;
    private String elementId;
    private String partId;
    private String beforeText;
    private String afterText;
    private int clientSequence;
    // ... getters/setters
}

// 2. サーバー側処理
public synchronized void processOperation(EditOperation op, Session session) {
    // serverSequence付与
    // トランスフォーム実行
    // DB保存
    // ブロードキャスト
}

// 3. クライアント側送信
public void sendTextChangeWithOT(String elementId, String partId, 
                                  String beforeText, String afterText) {
    // EditOperation作成
    // WebSocket送信
}

// 4. クライアント側受信・適用
public void applyOTOperation(int serverSequence, String elementId, 
                             String partId, String afterText, 
                             String userId, boolean isOwnOperation) {
    // UI更新
    // ローカルキュー更新
}
```

### WebSocket通信パターン
```java
// JSONメッセージ形式
{
    "action": "editOperation",
    "data": {
        "userId": "user123",
        "exerciseId": 1,
        "elementId": "class1",
        "partId": "name",
        "beforeText": "Class1",
        "afterText": "ClassA",
        "clientSequence": 5
    }
}
```

## ビルド関連

### build.xml構成
- `project.class.path`: クラスパス定義
- source/target: Java 8 (`source="8" target="8"`)
- `nowarn="true"`: 警告抑制
- デバッグ情報含む: `debug="true" debuglevel="lines,vars,source"`

### 依存関係
1. GWT SDKパス: `C:\gwt-2.8.2-custom`
2. API jar参照: `../api/src` をソースパスに含む
3. ライブラリ: `war/WEB-INF/lib/*.jar`

## 注意事項

### import文
- 必要なものだけimport
- wildcard (`*`) 使用可能だが、具体的なクラス名推奨

### エラーハンドリング
- try-catch使用
- ログ出力（System.out, ロガー使用）
- WebSocketエラーハンドラー: `@OnError`

### スレッドセーフティ
- `synchronized`メソッド使用（OperationManager等）
- 共有リソースへのアクセスに注意

### データベースアクセス
- HikariCP使用
- プリペアドステートメント推奨
- リソースクローズ（try-with-resources推奨）

## ドキュメント作成
- Markdown形式（.md）
- 日本語使用可
- コードブロック: \`\`\`java ... \`\`\`
- 図表: テキストベース、Mermaid等
