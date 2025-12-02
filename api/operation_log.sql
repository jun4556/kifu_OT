-- Operational Transformation用の操作ログテーブル
-- 全ての編集操作を記録し、リプレイや監査に使用

CREATE TABLE IF NOT EXISTS operation_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    exercise_id INT NOT NULL,
    element_id VARCHAR(255),
    part_id VARCHAR(255),
    operation_type VARCHAR(50) NOT NULL,
    -- テキスト編集用カラム
    patch_text TEXT,
    before_text TEXT,
    after_text TEXT,
    -- 移動操作用カラム
    old_x INT,
    old_y INT,
    delta_x INT,
    delta_y INT,
    -- シーケンス管理
    client_sequence INT,
    server_sequence INT NOT NULL,
    based_on_sequence INT,
    timestamp BIGINT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_exercise_seq (exercise_id, server_sequence),
    INDEX idx_user_exercise (user_id, exercise_id),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- exerciseテーブルへの外部キー制約（既存のexerciseテーブルがある場合）
-- ALTER TABLE operation_log ADD CONSTRAINT fk_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE;
