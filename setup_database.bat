@echo off
REM データベースセットアップスクリプト
REM operation_logテーブルを作成

echo データベースに接続しています...
echo.
echo MySQLのrootパスワードを入力してください:
echo.

REM MySQLコマンドを実行
mysql -u root -p < "%~dp0api\operation_log.sql"

if %errorlevel% == 0 (
    echo.
    echo operation_logテーブルの作成に成功しました！
    echo.
    echo テーブルの確認:
    mysql -u root -p -e "USE kifu; DESCRIBE operation_log;"
) else (
    echo.
    echo エラー: テーブルの作成に失敗しました
    echo MySQLが起動しているか、パスワードが正しいか確認してください
)

echo.
pause
