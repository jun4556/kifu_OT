package com.objetdirect.gwt.umldrawer.client.helpers;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * diff-match-patchライブラリの拡張ラッパー
 * OT方式のためのパッチ生成・適用機能を追加
 */
public class DiffMatchPatchGwtExtended extends DiffMatchPatchGwt {
    
    /**
     * 2つのテキスト間の差分からパッチテキストを生成
     * 
     * @param text1 変更前のテキスト
     * @param text2 変更後のテキスト
     * @return パッチテキスト
     */
    public native String makePatchText(String text1, String text2) /*-{
        var dmp = this.@com.objetdirect.gwt.umldrawer.client.helpers.DiffMatchPatchGwt::dmp;
        var patches = dmp.patch_make(text1, text2);
        return dmp.patch_toText(patches);
    }-*/;
    
    /**
     * パッチオブジェクトをテキストに変換
     * 
     * @param patches パッチオブジェクト
     * @return パッチテキスト
     */
    public native String patchToText(JavaScriptObject patches) /*-{
        var dmp = this.@com.objetdirect.gwt.umldrawer.client.helpers.DiffMatchPatchGwt::dmp;
        return dmp.patch_toText(patches);
    }-*/;
    
    /**
     * パッチオブジェクトを生成
     * 
     * @param text1 変更前のテキスト
     * @param text2 変更後のテキスト
     * @return パッチオブジェクト
     */
    public native JavaScriptObject createPatches(String text1, String text2) /*-{
        var dmp = this.@com.objetdirect.gwt.umldrawer.client.helpers.DiffMatchPatchGwt::dmp;
        return dmp.patch_make(text1, text2);
    }-*/;
}
