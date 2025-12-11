CREATE TABLE IF NOT EXISTS "face_data"
(
    "id"             INTEGER NOT NULL,
    "user_id"        TEXT,
    "image_url"      TEXT,
    "feature_vector" TEXT,
    "create_time"    INTEGER,
    "update_time"    INTEGER,
    "del_flag"       INTEGER,
    PRIMARY KEY ("id")
);