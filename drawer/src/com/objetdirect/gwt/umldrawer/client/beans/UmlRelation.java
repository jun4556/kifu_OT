// com.objetdirect.gwt.umldrawer.client.beans.UmlRelation.java (新規作成)
package com.objetdirect.gwt.umldrawer.client.beans;

public class UmlRelation {
    public String id;
    public String sourceId;
    public String targetId;
    public String type;
    public String sourceMultiplicity;
    public String targetMultiplicity;
    // 必要に応じてコンストラクタやメソッドを追加
 // ▼▼▼ 追加したフィールド ▼▼▼
    // Python側の line_style に対応
    public String lineStyle;
    // Python側の source_arrow に対応
    public String sourceArrow;
    // Python側の target_arrow に対応
    public String targetArrow;
    // ▲▲▲ 追加ここまで ▲▲▲
}