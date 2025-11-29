# コードベース構造

## プロジェクトルート構造

```
kifu_OT/
├── .git/                           # Gitリポジトリ
├── .serena/                        # Serenaプロジェクト設定
├── .vscode/                        # VS Code設定
├── api/                            # GWT UML API
├── drawer/                         # GWT UML Drawer（メインアプリ）
├── download_libraries.bat          # ライブラリダウンロードスクリプト
├── setup_database.bat              # データベースセットアップスクリプト
├── OT_Implementation_Summary.md    # OT実装完了報告
└── OT_Integration_Checklist.md     # OT統合チェックリスト
```

## api/ ディレクトリ

```
api/
├── .gwt/                           # GWT内部ファイル
├── .settings/                      # Eclipse設定
├── build/                          # ビルド出力
│   └── dist/
│       └── gwt-umlapi.jar          # API JARファイル
├── CVS/                            # CVSバージョン管理（レガシー）
├── doc/                            # Javadoc出力
│   ├── index.html
│   └── com/...
├── lib_provided/                   # 提供ライブラリ
├── src/                            # ソースコード
│   ├── hikari.properties           # DB接続設定
│   └── com/
│       ├── google/                 # Google関連
│       └── objetdirect/
│           └── gwt/
│               └── umlapi/
│                   ├── client/     # クライアント側
│                   ├── server/     # サーバー側
│                   │   └── dao/    # Data Access Object
│                   └── shared/     # 共有コード
├── war/                            # Webアプリケーションルート
│   └── WEB-INF/
│       ├── classes/                # コンパイル済みクラス
│       ├── lib/                    # ライブラリJAR
│       └── web.xml                 # Web設定
├── .classpath                      # Eclipse クラスパス
├── .project                        # Eclipse プロジェクト
├── build.xml                       # Ant ビルドスクリプト
├── 20150903kifu3.sql               # データベーススキーマ
├── 20161019kifu5.sql               # データベーススキーマ
├── kifu2.sql                       # データベーススキーマ
├── kifu3.sql                       # データベーススキーマ
└── operation_log.sql               # OT操作ログテーブル
```

## drawer/ ディレクトリ

```
drawer/
├── .gwt/                           # GWT内部ファイル
├── .settings/                      # Eclipse設定
├── docs/                           # プロジェクトドキュメント
│   ├── OT_Implementation_Guide.md  # OT実装ガイド
│   ├── DiffMatchPatch_Installation.md  # ライブラリ導入手順
│   └── WebSocket_Deployment.md     # WebSocketデプロイ手順
├── excel/                          # Excel関連ファイル
├── src/                            # ソースコード
│   └── com/
│       ├── google/                 # Google関連
│       └── objetdirect/
│           └── gwt/
│               └── umldrawer/
│                   ├── client/     # クライアント側
│                   │   ├── beans/  # データ構造
│                   │   │   └── EditOperation.java
│                   │   ├── helpers/  # ヘルパークラス
│                   │   │   ├── OperationTransformHelper.java
│                   │   │   ├── DiffMatchPatchGwtExtended.java
│                   │   │   └── WebSocketClient.java
│                   │   ├── collaboration/  # 共同編集機能
│                   │   │   └── WebSocketMessageHandler.java
│                   │   ├── DrawerPanel.java  # メイン描画パネル
│                   │   └── ...     # その他UIコンポーネント
│                   ├── server/     # サーバー側
│                   │   └── collaboration/  # 共同編集サーバー
│                   │       ├── CollaborationWebSocket.java
│                   │       └── OperationManager.java
│                   ├── public/     # 静的リソース
│                   │   ├── dijit/  # Dijitウィジェット
│                   │   ├── dojo/   # Dojo Toolkit
│                   │   └── dojox/  # Dojo拡張
│                   └── yamnazaki/  # カスタムコンポーネント
├── war/                            # Webアプリケーションルート
│   ├── gwtumlapi/                  # GWTコンパイル出力（API）
│   ├── umldrawer/                  # GWTコンパイル出力（Drawer）
│   ├── WEB-INF/
│   │   ├── classes/                # コンパイル済みクラス
│   │   ├── lib/                    # ライブラリJAR
│   │   │   ├── diff-match-patch-1.2.jar
│   │   │   ├── javax.websocket-api-1.1.jar
│   │   │   ├── gson-2.8.9.jar
│   │   │   └── ...
│   │   └── web.xml                 # Web設定
│   ├── GWTUMLDrawer.html           # メインHTML
│   ├── GWTUMLDrawer.css            # スタイルシート
│   └── ...
├── .classpath                      # Eclipse クラスパス
├── .project                        # Eclipse プロジェクト
├── .tomcatplugin                   # Tomcatプラグイン設定
├── build.xml                       # Ant ビルドスクリプト
├── GWTUMLDrawer.gwt.xml            # GWT モジュール定義
├── KIfU4.war                       # WARファイル（ビルド出力）
├── kifu6.sql                       # データベーススキーマ
├── kifu6_akagidp.sql               # データベーススキーマ
└── ...                             # その他のファイル
```

## 主要ソースファイル

### OT実装関連ファイル

#### データ構造
- `drawer/src/com/objetdirect/gwt/umldrawer/client/beans/EditOperation.java`
  - 編集操作のデータ構造

#### クライアント側
- `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/OperationTransformHelper.java`
  - OT操作送受信ヘルパー
- `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/DiffMatchPatchGwtExtended.java`
  - パッチ生成・適用ラッパー
- `drawer/src/com/objetdirect/gwt/umldrawer/client/helpers/WebSocketClient.java`
  - WebSocket接続管理（OT対応）
- `drawer/src/com/objetdirect/gwt/umldrawer/client/collaboration/WebSocketMessageHandler.java`
  - メッセージハンドラー
- `drawer/src/com/objetdirect/gwt/umldrawer/client/DrawerPanel.java`
  - メイン描画パネル、OTヘルパー統合

#### サーバー側
- `drawer/src/com/objetdirect/gwt/umldrawer/server/collaboration/CollaborationWebSocket.java`
  - WebSocketエンドポイント (@ServerEndpoint)
- `drawer/src/com/objetdirect/gwt/umldrawer/server/collaboration/OperationManager.java`
  - 操作管理とトランスフォーム処理

### データベース
- `api/operation_log.sql`
  - OT操作ログテーブル定義

### ビルドスクリプト
- `drawer/build.xml` - Drawerビルド
- `api/build.xml` - APIビルド

### 設定ファイル
- `api/src/hikari.properties` - DB接続設定
- `drawer/GWTUMLDrawer.gwt.xml` - GWTモジュール定義
- `war/WEB-INF/web.xml` - Web アプリケーション設定

## ビルド成果物

### コンパイル出力
- `drawer/war/WEB-INF/classes/` - Javaクラスファイル
- `drawer/war/gwtumlapi/` - GWTコンパイル済みJavaScript（API）
- `drawer/war/umldrawer/` - GWTコンパイル済みJavaScript（Drawer）

### パッケージング
- `drawer/KIfU4.war` - デプロイ可能なWARファイル
- `api/build/dist/gwt-umlapi.jar` - API JARファイル

## 依存関係の流れ

```
drawer (GWT UML Drawer)
    ↓ depends on
api (GWT UML API - JARとして、またはソースとして)
    ↓ uses
外部ライブラリ:
  - GWT 2.8.2
  - diff-match-patch 1.2
  - javax.websocket-api 1.1
  - gson 2.8.9
  - HikariCP
    ↓ connects to
MySQL データベース (kifu)
```

## 重要な注意点

1. **api/src は drawer/build.xml で参照される**
   - `<src path="../api/src"/>` でソースとして含まれる

2. **GWTコンパイル出力は war/ 内に生成される**
   - gitignoreで除外推奨

3. **クラスパスに OT関連ライブラリが追加済み**
   - build.xmlで明示的に指定

4. **WebSocketエンドポイントは Tomcat 8以降が必要**
   - javax.websocket-api 1.1 サポート必須
