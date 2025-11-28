# Java diff-match-patch ライブラリの追加手順

## 概要
Operational Transformation (OT) 実装のため、サーバー側でもdiff-match-patchライブラリが必要です。

## ダウンロード
1. Google Code Archiveから入手:
   https://code.google.com/archive/p/google-diff-match-patch/downloads

2. または、GitHubミラーから入手:
   https://github.com/google/diff-match-patch

## インストール手順

### 方法1: JARファイルを直接追加
```bash
# JARファイルをlibディレクトリに配置
# 例: diff-match-patch-1.2.jar
cd drawer
mkdir -p war/WEB-INF/lib
cp /path/to/diff-match-patch.jar war/WEB-INF/lib/
```

### 方法2: Mavenを使用する場合（プロジェクトがMaven対応している場合）
```xml
<dependency>
    <groupId>org.bitbucket.cowwoc</groupId>
    <artifactId>diff-match-patch</artifactId>
    <version>1.2</version>
</dependency>
```

### 方法3: ソースコードを直接追加
```bash
# diff_match_patch.javaファイルを直接プロジェクトに追加
# src/com/google/diff_match_patch/ に配置
```

## build.xmlの修正

drawer/build.xmlファイルのクラスパスに追加:

```xml
<path id="project.class.path">
    <!-- 既存のクラスパス設定 -->
    <pathelement location="war/WEB-INF/classes"/>
    <pathelement location="${gwt.sdk}/gwt-user.jar"/>
    <fileset dir="${gwt.sdk}" includes="gwt-dev*.jar"/>
    
    <!-- diff-match-patchライブラリを追加 -->
    <fileset dir="war/WEB-INF/lib" includes="diff-match-patch*.jar"/>
    
    <!-- 既存のlib設定 -->
    <fileset dir="war/WEB-INF/lib" includes="**/*.jar"/>
</path>
```

## OperationManager.javaの修正

ライブラリ追加後、OperationManager.javaの以下の部分を実装します:

```java
import org.bitbucket.cowwoc.diff_match_patch.DiffMatchPatch;
import org.bitbucket.cowwoc.diff_match_patch.Diff;
import org.bitbucket.cowwoc.diff_match_patch.Patch;

// recomputePatchメソッドの実装
private String recomputePatch(String originalPatch, String concurrentOp) {
    DiffMatchPatch dmp = new DiffMatchPatch();
    
    // パッチ文字列をPatchオブジェクトに変換
    LinkedList<Patch> patches = (LinkedList<Patch>) dmp.patch_fromText(originalPatch);
    
    // TODO: 実際のトランスフォームロジック
    // 簡易版では、パッチをそのまま返す
    return dmp.patch_toText(patches);
}
```

## 確認方法

```bash
# ビルドして確認
cd drawer
ant clean
ant build

# クラスパスにライブラリが含まれているか確認
jar tf war/WEB-INF/lib/diff-match-patch*.jar | grep DiffMatchPatch
```

## 参考資料
- Google diff-match-patch Wiki: https://github.com/google/diff-match-patch/wiki
- API Documentation: https://github.com/google/diff-match-patch/wiki/API
- OT Algorithm: https://github.com/google/diff-match-patch/wiki/Unidiff
