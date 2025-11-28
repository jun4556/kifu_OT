# Operational Transformation (OT) Implementation

## Purpose
リアルタイム共同編集時の競合問題を解決。複数ユーザーが同時にUML図を編集しても、すべての変更を保持し矛盾なく統合する。

## Problem Solved
**Before**: 2人が同時編集すると、最初の変更のみがDB保存され、2番目の変更は破棄される（last-write-wins）
**After**: 各操作にシーケンス番号を付与し、サーバー側でトランスフォームして全変更を保持

## Architecture

### Data Flow
```
Client → sendTextChangeWithOT() 
       → WebSocket 
       → CollaborationWebSocket.onMessage()
       → OperationManager.processOperation()
       → operation_log (DB)
       → broadcast to all clients
       → DrawerPanel.applyOTOperation()
```

### Key Components

#### Server-Side
1. **CollaborationWebSocket.java** (`drawer/src/.../server/collaboration/`)
   - WebSocket endpoint (`@ServerEndpoint("/collaboration")`)
   - Receives editOperation messages
   - Calls OperationManager for processing
   - Broadcasts to all connected clients

2. **OperationManager.java** (`drawer/src/.../server/collaboration/`)
   - Singleton pattern
   - Manages operation queue per exercise
   - Assigns server sequence numbers
   - Transforms concurrent operations
   - Methods:
     - `processOperation()` - Main processing logic
     - `transformOperation()` - OT algorithm
     - `recomputePatch()` - Patch transformation (needs diff-match-patch)

#### Client-Side
1. **EditOperation.java** (`drawer/src/.../client/beans/`)
   - Data structure for operations
   - Fields: clientSequence, serverSequence, userId, elementId, partId, operationType, patchText, beforeText, afterText, timestamp, basedOnServerSequence

2. **OperationTransformHelper.java** (`drawer/src/.../client/helpers/`)
   - Manages client/server sequence numbers
   - `sendTextChangeWithOT()` - Send operation to server
   - `applyServerOperation()` - Apply received operation

3. **DiffMatchPatchGwtExtended.java** (`drawer/src/.../client/helpers/`)
   - Extends DiffMatchPatchGwt
   - JSNI methods: `createPatches()`, `patchToText()`, `makePatchText()`

4. **WebSocketMessageHandler.java** (`drawer/src/.../client/collaboration/`)
   - Routes WebSocket messages
   - Handles: editOperationResponse, sync, textUpdate, applyPatch

#### Database
**operation_log table** (`api/operation_log.sql`)
```sql
CREATE TABLE operation_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    exercise_id INT,
    element_id VARCHAR(255),
    part_id VARCHAR(255),
    operation_type VARCHAR(50),
    patch_text TEXT,
    before_text TEXT,
    after_text TEXT,
    client_sequence INT,
    server_sequence INT,
    based_on_sequence INT,
    timestamp BIGINT,
    date DATETIME
);
```

## Integration Points

### Modified Files
1. **WebSocketClient.java** - Added `editOperationResponse` handler in `onMessage()`
2. **DrawerPanel.java** - Added `otHelper` field and OT methods
3. **build.xml** - Added OT library classpaths

### Required Libraries
- `diff-match-patch-1.2.jar` - Text diff/patch operations
- `javax.websocket-api-1.1.jar` - WebSocket support
- `gson-2.8.9.jar` - JSON processing

Download via: `download_libraries.bat`

## Setup Steps
1. Download libraries: `download_libraries.bat`
2. Create database table: `setup_database.bat` or `mysql -u root -p < api\operation_log.sql`
3. Integrate code snippets (see `OT_Integration_Checklist.md`)
4. Build: `cd drawer && ant clean && ant build`
5. Deploy to Tomcat 8+

## Testing
1. Open 2 browsers with same exercise
2. Edit same class name simultaneously
3. Verify both changes are saved in operation_log
4. Check browser console for `editOperationResponse` messages
5. Check Tomcat logs for operation processing

## Known Limitations
- `recomputePatch()` in OperationManager.java is simplified (needs full diff-match-patch integration)
- Requires manual code integration (see `DrawerPanel_OT_methods_v2.txt`, `WebSocketClient_OT_addition.txt`)
- Requires Tomcat 8+ for WebSocket support

## Documentation Files
- `OT_Implementation_Summary.md` - Complete implementation report
- `OT_Integration_Checklist.md` - Step-by-step integration guide
- `OT_Implementation_Guide.md` - Comprehensive setup and usage guide
- `DiffMatchPatch_Installation.md` - Library installation details
- `WebSocket_Deployment.md` - WebSocket deployment guide
- `OperationManager_patch_implementation.txt` - Patch transform examples

## Future Enhancements
- Full diff-match-patch integration in `OperationManager.recomputePatch()`
- Conflict visualization in UI
- Offline support with local storage
- Undo/Redo based on operation history
- Performance optimization (operation queue cleanup, DB indexing)
