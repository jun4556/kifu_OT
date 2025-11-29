# 推奨コマンド一覧

## プロジェクトセットアップ

### 1. ライブラリダウンロード
```cmd
download_libraries.bat
```
必要なJARファイル（diff-match-patch, javax.websocket-api, gson）をダウンロード

### 2. データベースセットアップ
```cmd
setup_database.bat
```
operation_logテーブルを作成

または手動で:
```cmd
mysql -u root -p < api\operation_log.sql
```

## ビルドコマンド

### クリーンビルド（drawer）
```cmd
cd drawer
ant clean
ant build
```

### WARファイル生成
```cmd
cd drawer
ant war
```
出力: KIfU4.war

### APIライブラリビルド
```cmd
cd api
ant clean
ant javac
ant jar
```
出力: build/dist/gwt-umlapi.jar

## デプロイ

### Tomcatへのデプロイ
```cmd
copy drawer\KIfU4.war %CATALINA_HOME%\webapps\
```

### Tomcat起動
```cmd
%CATALINA_HOME%\bin\startup.bat
```

### Tomcat停止
```cmd
%CATALINA_HOME%\bin\shutdown.bat
```

## データベース操作

### MySQLログイン
```cmd
mysql -u root -p
```

### データベース確認
```sql
USE kifu;
SHOW TABLES;
DESCRIBE operation_log;
```

### operation_logの内容確認
```sql
SELECT * FROM operation_log ORDER BY server_sequence DESC LIMIT 10;
```

## Windowsシステムコマンド

### ディレクトリ一覧
```cmd
dir
dir /s  :: サブディレクトリも含む
```

### ディレクトリ移動
```cmd
cd <directory>
cd ..  :: 親ディレクトリ
cd \   :: ルート
```

### ファイル検索
```cmd
dir /s /b *.java  :: すべてのJavaファイルを検索
```

### ファイル内容検索
```cmd
findstr /s /i "pattern" *.java  :: 大文字小文字区別なし
```

### ファイルコピー
```cmd
copy <source> <destination>
xcopy /s /e <source_dir> <destination_dir>  :: ディレクトリごとコピー
```

### ファイル削除
```cmd
del <file>
rmdir /s /q <directory>  :: ディレクトリごと削除
```

## Git操作

### ステータス確認
```cmd
git status
```

### 変更のコミット
```cmd
git add .
git commit -m "commit message"
```

### プッシュ
```cmd
git push origin main
```

### ブランチ確認
```cmd
git branch
```

## ログ確認

### Tomcatログ
```cmd
type %CATALINA_HOME%\logs\catalina.out
:: またはテキストエディタで開く
```

### 最新のログ表示（PowerShellの場合）
```powershell
Get-Content %CATALINA_HOME%\logs\catalina.out -Tail 50
```

## テスト・デバッグ

### ブラウザでアプリケーション起動
```
http://localhost:8080/KIfU4/
```

### WebSocket接続確認
ブラウザDevToolsのConsoleで:
```javascript
// WebSocket connection opened. が表示されることを確認
```

### データベース接続テスト
```sql
-- hikari.propertiesの設定を確認
-- 接続できるかテスト
SELECT 1;
```

## 便利なユーティリティコマンド

### JARファイルの内容確認
```cmd
jar tf <jarfile>
```

### クラスパス確認（Javaコンパイル時）
```cmd
echo %CLASSPATH%
```

### Javaバージョン確認
```cmd
java -version
javac -version
```

### 環境変数表示
```cmd
set  :: すべての環境変数
set JAVA_HOME  :: 特定の環境変数
```

## トラブルシューティング

### ポート使用状況確認
```cmd
netstat -ano | findstr :8080
```

### プロセス終了
```cmd
taskkill /F /PID <process_id>
```

### ビルドエラー時のクリーンアップ
```cmd
cd drawer
ant clean
del /s /q war\WEB-INF\classes\*
del /s /q war\gwtumlapi\*
del /s /q war\umldrawer\*
```
