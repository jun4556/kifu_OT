package com.objetdirect.gwt.umldrawer.server.collaboration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.objetdirect.gwt.umldrawer.client.beans.EditOperation;

/**
 * サーバー側で編集操作を管理し、Operational Transformation (OT) による
 * 同時編集の競合解決を行うマネージャークラス。
 */
public class OperationManager {
    
    /** エクササイズごとの操作履歴を管理するマップ */
    private final Map<Integer, List<EditOperation>> operationHistory;
    
    /** エクササイズごとのグローバルシーケンス番号カウンター */
    private final Map<Integer, AtomicInteger> sequenceCounters;
    
    /** エクササイズごとの現在のテキスト状態を管理 (elementId:partId -> currentText) */
    private final Map<Integer, Map<String, String>> currentStates;
    
    /** シングルトンインスタンス */
    private static OperationManager instance;
    
    private OperationManager() {
        this.operationHistory = new ConcurrentHashMap<>();
        this.sequenceCounters = new ConcurrentHashMap<>();
        this.currentStates = new ConcurrentHashMap<>();
    }
    
    /**
     * シングルトンインスタンスを取得
     */
    public static synchronized OperationManager getInstance() {
        if (instance == null) {
            instance = new OperationManager();
        }
        return instance;
    }
    
    /**
     * 移動操作を処理
     * 
     * @param operation クライアントから送信された移動操作
     * @return サーバーで処理された操作（他のクライアントに配信すべきもの）
     */
    public synchronized EditOperation transformMoveOperation(EditOperation operation) {
        int exerciseId = operation.getExerciseId();
        
        // 初期化処理
        initializeExercise(exerciseId);
        
        // グローバルシーケンス番号を割り当て
        int serverSeq = sequenceCounters.get(exerciseId).incrementAndGet();
        operation.setServerSequence(serverSeq);
        
        // 操作履歴を取得
        List<EditOperation> history = operationHistory.get(exerciseId);
        
        // OT方式: deltaは合成しない
        // 各操作のdeltaをそのまま保持し、クライアント側で順次適用する
        // これにより、すべてのユーザーの移動が正しく反映される
        
        // 操作履歴に追加
        history.add(operation);
        
        return operation;
    }
    
    /**
     * 同じ要素に対する同時移動操作を取得
     */
    private List<EditOperation> getConcurrentMoveOperations(
        List<EditOperation> history,
        String elementId,
        int basedOnSeq
    ) {
        List<EditOperation> concurrent = new ArrayList<>();
        for (EditOperation op : history) {
            if (op.getServerSequence() > basedOnSeq &&
                "move_delta".equals(op.getOperationType()) &&
                elementId.equals(op.getElementId())) {
                concurrent.add(op);
            }
        }
        return concurrent;
    }
    
    /**
     * 新しい編集操作を受信し、適切に処理する
     * 
     * @param operation クライアントから送信された編集操作
     * @return サーバーで処理された操作（他のクライアントに配信すべきもの）
     */
    public synchronized EditOperation processOperation(EditOperation operation) {
        int exerciseId = operation.getExerciseId();
        
        // 初期化処理
        initializeExercise(exerciseId);
        
        // グローバルシーケンス番号を割り当て
        int serverSeq = sequenceCounters.get(exerciseId).incrementAndGet();
        operation.setServerSequence(serverSeq);
        
        // 操作履歴から、この操作が基づいているシーケンス以降の操作を取得
        List<EditOperation> history = operationHistory.get(exerciseId);
        List<EditOperation> concurrentOps = getConcurrentOperations(
            history, 
            operation.getBasedOnServerSequence()
        );
        
        // 同時に発生した操作がある場合、トランスフォームを適用
        EditOperation transformedOp = operation;
        if (!concurrentOps.isEmpty()) {
            transformedOp = transformOperation(operation, concurrentOps);
        }
        
        // 現在の状態を更新
        String stateKey = createStateKey(operation.getElementId(), operation.getPartId());
        Map<String, String> states = currentStates.get(exerciseId);
        
        String currentText = states.getOrDefault(stateKey, "");
        String newText = applyPatch(currentText, transformedOp.getPatchText());
        states.put(stateKey, newText);
        
        // トランスフォーム後のテキストを設定
        transformedOp.setAfterText(newText);
        
        // 操作履歴に追加
        history.add(transformedOp);
        
        return transformedOp;
    }
    
    /**
     * エクササイズの初期化
     */
    private void initializeExercise(int exerciseId) {
        operationHistory.putIfAbsent(exerciseId, Collections.synchronizedList(new ArrayList<>()));
        sequenceCounters.putIfAbsent(exerciseId, new AtomicInteger(0));
        currentStates.putIfAbsent(exerciseId, new ConcurrentHashMap<>());
    }
    
    /**
     * 指定したシーケンス番号以降の操作を取得
     */
    private List<EditOperation> getConcurrentOperations(
            List<EditOperation> history, 
            int basedOnSequence) {
        List<EditOperation> concurrent = new ArrayList<>();
        for (EditOperation op : history) {
            if (op.getServerSequence() > basedOnSequence) {
                concurrent.add(op);
            }
        }
        return concurrent;
    }
    
    /**
     * 操作をトランスフォームする
     * 
     * @param newOp 新しく受信した操作
     * @param concurrentOps 同時に発生していた操作のリスト
     * @return トランスフォーム後の操作
     */
    private EditOperation transformOperation(
            EditOperation newOp, 
            List<EditOperation> concurrentOps) {
        
        EditOperation transformed = cloneOperation(newOp);
        
        // 同じ要素・同じ部分に対する操作のみをトランスフォーム対象とする
        for (EditOperation concurrentOp : concurrentOps) {
            if (isSameTarget(newOp, concurrentOp)) {
                // パッチをトランスフォーム
                // 注: 本格的なOTでは、操作の種類に応じて複雑なトランスフォームが必要
                // ここでは簡易版として、パッチを再計算する
                transformed = recomputePatch(transformed, concurrentOp);
            }
        }
        
        return transformed;
    }
    
    /**
     * 同じ編集対象かどうかをチェック
     */
    private boolean isSameTarget(EditOperation op1, EditOperation op2) {
        return op1.getElementId().equals(op2.getElementId()) &&
               op1.getPartId().equals(op2.getPartId());
    }
    
    /**
     * 操作をクローン
     */
    private EditOperation cloneOperation(EditOperation op) {
        EditOperation cloned = new EditOperation();
        cloned.setClientSequence(op.getClientSequence());
        cloned.setUserId(op.getUserId());
        cloned.setSessionId(op.getSessionId());
        cloned.setElementId(op.getElementId());
        cloned.setPartId(op.getPartId());
        cloned.setOperationType(op.getOperationType());
        cloned.setPatchText(op.getPatchText());
        cloned.setBeforeText(op.getBeforeText());
        cloned.setAfterText(op.getAfterText());
        cloned.setTimestamp(op.getTimestamp());
        cloned.setBasedOnServerSequence(op.getBasedOnServerSequence());
        cloned.setExerciseId(op.getExerciseId());
        return cloned;
    }
    
    /**
     * 先行する操作を考慮してパッチを再計算
     */
    private EditOperation recomputePatch(EditOperation newOp, EditOperation priorOp) {
        // 先行操作適用後の状態を基準に、新しいパッチを計算
        String baseText = priorOp.getAfterText();
        String targetText = newOp.getAfterText();
        
        // TODO: diff-match-patchライブラリを使用した実装
        // 実装例はOperationManager_patch_implementation.txtを参照
        
        EditOperation recomputed = cloneOperation(newOp);
        
        // 簡易実装: 先行操作の結果を基準テキストとして、
        // 新しいパッチを再生成する必要がある
        // 現状は元のパッチをそのまま使用（要改善）
        
        return recomputed;
    }
    
    /**
     * パッチを適用してテキストを更新
     * 注: この実装は簡易版。実際にはdiff-match-patchライブラリが必要
     */
    private String applyPatch(String currentText, String patchText) {
        // TODO: サーバー側でdiff-match-patchを使用してパッチを適用
        // 現状は簡易実装として、パッチテキストをそのまま返す
        return currentText;
    }
    
    /**
     * 状態管理用のキーを生成
     */
    private String createStateKey(String elementId, String partId) {
        return elementId + ":" + partId;
    }
    
    /**
     * 特定のエクササイズの操作履歴を取得
     */
    public List<EditOperation> getHistory(int exerciseId) {
        return new ArrayList<>(operationHistory.getOrDefault(
            exerciseId, 
            Collections.emptyList()
        ));
    }
    
    /**
     * 特定の要素の現在のテキストを取得
     */
    public String getCurrentText(int exerciseId, String elementId, String partId) {
        Map<String, String> states = currentStates.get(exerciseId);
        if (states == null) {
            return "";
        }
        String stateKey = createStateKey(elementId, partId);
        return states.getOrDefault(stateKey, "");
    }
    
    /**
     * エクササイズの履歴をクリア（テスト用）
     */
    public void clearHistory(int exerciseId) {
        operationHistory.remove(exerciseId);
        sequenceCounters.remove(exerciseId);
        currentStates.remove(exerciseId);
    }
}
