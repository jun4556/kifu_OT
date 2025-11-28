@echo off
REM ライブラリダウンロードスクリプト（Windows用）
REM 必要なJARファイルをダウンロードしてdrawer/war/WEB-INF/lib/に配置

echo ライブラリをダウンロードしています...

cd /d "%~dp0drawer\war\WEB-INF\lib"

echo 1/3: diff-match-patch をダウンロード中...
curl -L -o diff-match-patch-1.2.jar "https://repo1.maven.org/maven2/org/bitbucket/cowwoc/diff-match-patch/1.2/diff-match-patch-1.2.jar"

echo 2/3: javax.websocket-api をダウンロード中...
curl -L -o javax.websocket-api-1.1.jar "https://repo1.maven.org/maven2/javax/websocket/javax.websocket-api/1.1/javax.websocket-api-1.1.jar"

echo 3/3: gson をダウンロード中...
curl -L -o gson-2.8.9.jar "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar"

echo.
echo ダウンロード完了！
echo.
echo ダウンロードされたファイル:
dir *.jar
echo.
echo 次のステップ: build.xmlを更新してください
pause
