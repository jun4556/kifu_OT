# KIfU_marge Project Overview

## Purpose
KIfU_margeは、GWT (Google Web Toolkit) を使用したUML図作成ツールです。教育用途に特化しており、学生がUML図を作成・編集し、操作ログを記録して分析することができます。

## Main Features
- UMLクラス図、ユースケース図、シーケンス図などの作成
- セキュリティユースケース図、ミスユースケース図のサポート
- 学生の操作ログ記録と分析機能
- 課題管理システム
- リフレクション(振り返り)機能
- デザインパターンの保存と活用

## Project Structure
プロジェクトは2つの主要モジュールで構成:

### 1. api Module
- Location: `api/`
- Purpose: UML図描画のためのAPIライブラリ
- Output: `gwt-umlapi.jar`
- Main packages:
  - `com.objetdirect.gwt.umlapi.client.artifacts` - UML要素のアーティファクト
  - `com.objetdirect.gwt.umlapi.client.umlcomponents` - UMLコンポーネント
  - `com.objetdirect.gwt.umlapi.client.gfx` - グラフィックス処理
  - `com.objetdirect.gwt.umlapi.client.helpers` - ヘルパークラス
  - `com.objetdirect.gwt.umlapi.client.editors` - フィールドエディタ
  - `com.objetdirect.gwt.umlapi.server` - サーバーサイド

### 2. drawer Module
- Location: `drawer/`
- Purpose: UML図作成ツールのメインアプリケーション
- Output: `KIfU4.war`
- Main packages:
  - `com.objetdirect.gwt.umldrawer.client` - クライアントサイド
  - `com.objetdirect.gwt.umldrawer.server` - サーバーサイド
  - `com.objetdirect.gwt.umldrawer.server.collaboration` - OT実装(WebSocket)
  - `com.objetdirect.gwt.umldrawer.client.collaboration` - クライアント側OT

## Database
- MySQL database
- Key tables:
  - operation_log - 操作ログ(OT実装で拡張)
  - exercise - 課題情報
  - answer - 学生の回答
  - comment - コメント

## Recent Enhancements (2024-2025)
### Operational Transformation (OT) Implementation
同時編集時の競合問題を解決するためのOT方式実装

#### Server Components
- `CollaborationWebSocket.java` - WebSocketエンドポイント
- `OperationManager.java` - 操作キュー管理とトランスフォーム処理

#### Client Components
- `EditOperation.java` - 編集操作のデータ構造
- `OperationTransformHelper.java` - OT操作の送受信ヘルパー
- `DiffMatchPatchGwtExtended.java` - パッチ生成・適用ラッパー
- `WebSocketMessageHandler.java` - WebSocketメッセージハンドラー

#### Required Libraries
- diff-match-patch-1.2.jar
- javax.websocket-api-1.1.jar
- gson-2.8.9.jar

## License
MIT License (Copyright 2017 Ken Akagi, Hiroaki Hashiura)