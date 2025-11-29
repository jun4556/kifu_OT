package com.objetdirect.gwt.umldrawer.server.collaboration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch;

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
     * OTアルゴリズムにより、同時移動操作の競合を解決
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
        
        // 同じ要素に対する同時移動操作を取得
        List<EditOperation> concurrentOps = getConcurrentMoveOperations(
            history, 
            operation.getElementId(), 
            operation.getBasedOnServerSequence()
        );
        
        // OT変換: 同時移動操作がある場合、oldX/oldYを調整
        if (!concurrentOps.isEmpty()) {
            // 最新の移動操作の最終座標を新しい基準座標とする
            EditOperation lastOp = concurrentOps.get(concurrentOps.size() - 1);
            int adjustedOldX = lastOp.getOldX() + lastOp.getDeltaX();
            int adjustedOldY = lastOp.getOldY() + lastOp.getDeltaY();
            
            // 基準座標を更新（deltaはそのまま）
            operation.setOldX(adjustedOldX);
            operation.setOldY(adjustedOldY);
            
            System.out.println("OT変換適用: " + operation.getElementId() + 
                             " 元基準(" + lastOp.getOldX() + "," + lastOp.getOldY() + 
                             ") → 新基準(" + adjustedOldX + "," + adjustedOldY + ")");
        }
        
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
        
        // 中間状態を累積管理: 各先行操作を順次適用
        // 最初はnewOpの基準テキストから開始
        String accumulatedBaseText = newOp.getBeforeText();
        
        // 同じ要素・同じ部分に対する操作のみをトランスフォーム対象とする
        for (EditOperation concurrentOp : concurrentOps) {
            if (isSameTarget(newOp, concurrentOp)) {
                // パッチをトランスフォーム
                // recomputePatch()は先行操作の結果に新操作のパッチを適用
                transformed = recomputePatch(transformed, concurrentOp);
                
                // 次のループのために累積的な基準テキストを更新
                // transformedのafterTextは、priorOpの結果にtransformedのパッチを適用した結果
                if (transformed.getAfterText() != null) {
                    accumulatedBaseText = transformed.getAfterText();
                    transformed.setBeforeText(accumulatedBaseText);
                }
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
     * diff-match-patchライブラリを使用したOT実装
     */
    private EditOperation recomputePatch(EditOperation newOp, EditOperation priorOp) {
        try {
            DiffMatchPatch dmp = new DiffMatchPatch();
            
            // 先行操作適用後の状態を取得
            String baseText = priorOp.getAfterText();
            
            // newOpのパッチをbaseTextに適用してtargetTextを計算
            // これがOTの基本原理: 先行操作の結果に新操作のパッチを適用
            String targetText = applyPatch(baseText, newOp.getPatchText());
            
            // パッチ適用失敗時のフォールバック
            if (targetText == null) {
                System.err.println("recomputePatch: パッチ適用失敗。元の操作を返します。");
                System.err.println("  priorOp.afterText=" + baseText);
                System.err.println("  newOp.patchText=" + newOp.getPatchText());
                return cloneOperation(newOp);
            }
            
            // baseTextからtargetTextへの新しいパッチを生成
            LinkedList<Diff> diffs = dmp.diffMain(baseText, targetText);
            dmp.diffCleanupSemantic(diffs);
            LinkedList<Patch> patches = dmp.patchMake(baseText, diffs);
            String newPatchText = dmp.patchToText(patches);
            
            EditOperation recomputed = cloneOperation(newOp);
            recomputed.setPatchText(newPatchText);
            recomputed.setBeforeText(baseText);
            recomputed.setAfterText(targetText);
            
            return recomputed;
        } catch (Exception e) {
            // エラー時は元の操作を返す
            System.err.println("recomputePatch error: " + e.getMessage());
            return cloneOperation(newOp);
        }
    }
    
    /**
     * パッチを適用してテキストを更新
     * diff-match-patchライブラリを使用した完全実装
     */
    private String applyPatch(String currentText, String patchText) {
        if (patchText == null || patchText.isEmpty()) {
            return currentText;
        }
        
        try {
            DiffMatchPatch dmp = new DiffMatchPatch();
            LinkedList<Patch> patches = (LinkedList<Patch>) dmp.patchFromText(patchText);
            
            Object[] results = dmp.patchApply(patches, currentText);
            String patchedText = (String) results[0];
            boolean[] successFlags = (boolean[]) results[1];
            
            // すべてのパッチが成功したか確認
            boolean allSuccess = true;
            for (boolean success : successFlags) {
                if (!success) {
                    allSuccess = false;
                    break;
                }
            }
            
            if (allSuccess) {
                return patchedText;
            } else {
                System.err.println("Some patches failed to apply. Using original text.");
                return currentText;
            }
        } catch (Exception e) {
            System.err.println("applyPatch error: " + e.getMessage());
            return currentText;
        }
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
