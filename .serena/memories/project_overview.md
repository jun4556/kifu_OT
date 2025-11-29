# プロジェクト概要

## プロジェクト名
KIfU_marge (kifu_OT)

## 目的
リアルタイム共同編集機能を持つUMLクラス図描画ツール。Operational Transformation (OT)方式を実装し、複数ユーザーが同時にクラス図を編集する際の競合問題を解決する。

## 問題と解決策
**問題**: 2人が同時にクラス図を編集すると、最初に変更を送信した方のみがDBに記録され、2番目のユーザーの変更は破棄される（last-write-wins）

**解決**: OT方式により、各操作にシーケンス番号を付与し、サーバー側で操作をトランスフォーム（変換）することで、全ユーザーの変更を保持し矛盾なく統合

## プロジェクト構成

### 1. api/ ディレクトリ
- GWT UML API
- データベース関連SQL
- サーバー側のDAO・ビジネスロジック
- operation_log.sql: OT操作ログテーブル定義

### 2. drawer/ ディレクトリ
- GWT UMLDrawer - メインのクライアント/サーバーアプリケーション
- OT実装コンポーネント
  - サーバー側: CollaborationWebSocket.java, OperationManager.java
  - クライアント側: EditOperation.java, OperationTransformHelper.java等
- ドキュメント: docs/
  - OT_Implementation_Guide.md
  - DiffMatchPatch_Installation.md
  - WebSocket_Deployment.md

## 技術スタック
- **言語**: Java (JDK 8)
- **フレームワーク**: Google Web Toolkit (GWT) 2.8.2
- **ビルドツール**: Apache Ant
- **データベース**: MySQL
- **コネクションプール**: HikariCP
- **WebSocket**: Java WebSocket API (JSR 356) 1.1
- **OTライブラリ**: diff-match-patch 1.2
- **JSONライブラリ**: Gson 2.8.9
- **アプリケーションサーバー**: Apache Tomcat 8以降

## 主要機能
1. UMLクラス図の描画・編集
2. リアルタイム共同編集（WebSocket使用）
3. Operational Transformationによる競合解決
4. 操作ログのDB保存
5. 複数ユーザー間での変更同期

## 開発環境
- OS: Windows
- IDE: Eclipse（推定、.project/.classpathファイルあり）
- シェル: cmd.exe
