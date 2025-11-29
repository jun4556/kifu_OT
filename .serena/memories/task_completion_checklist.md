# タスク完了時のチェックリスト

タスク完了時には、以下の手順を実行してコードの品質を保証します。

## 1. コンパイル確認

### Javaコンパイル
```cmd
cd drawer
ant javac
```
または
```cmd
cd api
ant javac
```

**確認項目:**
- [ ] コンパイルエラーがない
- [ ] 警告を確認（必要に応じて修正）

### GWTコンパイル（Drawer側のみ）
```cmd
cd drawer
ant gwtc
```

**確認項目:**
- [ ] GWT→JavaScriptトランスパイル成功
- [ ] war/gwtumlapi/ または war/umldrawer/ に出力ファイル生成

## 2. ビルド

### フルビルド
```cmd
cd drawer
ant clean
ant build
```

**確認項目:**
- [ ] クリーンビルド成功
- [ ] war/WEB-INF/classes/ にクラスファイル生成

## 3. WARファイル生成

```cmd
cd drawer
ant war
```

**確認項目:**
- [ ] KIfU4.war が生成される
- [ ] ファイルサイズが適切（破損していない）

## 4. データベース整合性確認

### テーブル存在確認
```sql
USE kifu;
SHOW TABLES;
```

**確認項目:**
- [ ] operation_log テーブルが存在
- [ ] その他必要なテーブルが存在

### スキーマ確認
```sql
DESCRIBE operation_log;
```

**確認項目:**
- [ ] 必要なカラムがすべて存在
- [ ] データ型が正しい

## 5. デプロイ

### Tomcatデプロイ
```cmd
copy drawer\KIfU4.war %CATALINA_HOME%\webapps\
%CATALINA_HOME%\bin\startup.bat
```

**確認項目:**
- [ ] WARファイルがwebappsにコピーされた
- [ ] Tomcatが正常起動
- [ ] 自動デプロイ完了（KIfU4/ディレクトリ作成）

### ログ確認
```cmd
type %CATALINA_HOME%\logs\catalina.out
```

**確認項目:**
- [ ] 起動エラーがない
- [ ] WebSocket初期化成功
- [ ] データベース接続成功

## 6. 機能テスト

### 基本動作確認
1. [ ] ブラウザで http://localhost:8080/KIfU4/ にアクセス
2. [ ] ログイン可能
3. [ ] クラス図が表示される
4. [ ] 基本的な編集操作が動作

### OT機能確認（該当する場合）
1. [ ] 2つのブラウザで同じ演習を開く
2. [ ] 同時編集実行
3. [ ] 両方のブラウザで変更が反映される
4. [ ] データベースに操作が記録される

```sql
SELECT * FROM operation_log 
WHERE exercise_id = <演習ID> 
ORDER BY server_sequence DESC 
LIMIT 10;
```

5. [ ] 操作ログが正しく記録されている

### WebSocket接続確認
- [ ] ブラウザDevToolsコンソールで "WebSocket connection opened." を確認
- [ ] 切断時のエラーハンドリング確認

## 7. コード品質チェック

### コードレビュー
- [ ] 適切なエラーハンドリング
- [ ] リソースクローズ（DB接続、ファイル等）
- [ ] null チェック
- [ ] スレッドセーフティ（必要な箇所で synchronized）

### ドキュメント
- [ ] 新規クラス/メソッドにJavadocコメント追加
- [ ] 複雑なロジックにコメント追加
- [ ] 必要に応じて README やドキュメント更新

### import文整理
- [ ] 未使用のimport削除
- [ ] 適切なimport順序

## 8. バージョン管理

### Git操作
```cmd
git status
git add .
git commit -m "適切なコミットメッセージ"
```

**確認項目:**
- [ ] 意図したファイルのみコミット
- [ ] 生成ファイル（.class, war等）は除外
- [ ] コミットメッセージが明確

### .gitignore確認
以下が除外されていることを確認:
- [ ] war/WEB-INF/classes/
- [ ] war/gwtumlapi/
- [ ] war/umldrawer/
- [ ] build/
- [ ] *.class
- [ ] KIfU4.war

## 9. 実行時テスト（該当する場合）

### 単体テスト
```cmd
:: JUnitテストがある場合
cd api
ant test
```

### 統合テスト
- [ ] エンドツーエンドシナリオ実行
- [ ] エラーケース確認
- [ ] パフォーマンス確認（必要に応じて）

## 10. クリーンアップ

### 不要ファイル削除
```cmd
cd drawer
ant clean
```

### 一時ファイル確認
- [ ] *.swp, *.tmp ファイルが残っていない
- [ ] ログファイルが肥大化していない

## チェックリスト完了確認

すべての項目をチェックしたら、タスク完了とします。

**最終確認:**
- [ ] ビルド成功
- [ ] デプロイ成功
- [ ] 主要機能動作確認
- [ ] ログにエラーなし
- [ ] コードコミット完了

## トラブルシューティング参照

問題が発生した場合は以下のドキュメントを参照:
- OT_Integration_Checklist.md - 統合時のトラブルシューティング
- OT_Implementation_Guide.md - 実装ガイド
- Tomcatログ: %CATALINA_HOME%\logs\catalina.out
