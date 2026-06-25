-- ChatApp Database Init Script
-- Runs automatically when Docker container starts fresh

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone        VARCHAR(20) UNIQUE NOT NULL,
    name         VARCHAR(100),
    avatar_url   TEXT,
    fcm_token    TEXT,
    last_seen    TIMESTAMP,
    is_online    BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

-- Chats table (DIRECT or GROUP)
CREATE TABLE IF NOT EXISTS chats (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type         VARCHAR(10) NOT NULL CHECK (type IN ('DIRECT', 'GROUP')),
    group_name   VARCHAR(100),
    group_avatar TEXT,
    created_by   UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at   TIMESTAMP DEFAULT NOW()
);

-- Chat members
CREATE TABLE IF NOT EXISTS chat_members (
    chat_id    UUID REFERENCES chats(id) ON DELETE CASCADE,
    user_id    UUID REFERENCES users(id) ON DELETE CASCADE,
    role       VARCHAR(10) DEFAULT 'MEMBER' CHECK (role IN ('ADMIN', 'MEMBER')),
    joined_at  TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (chat_id, user_id)
);

-- Messages
CREATE TABLE IF NOT EXISTS messages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_id     UUID REFERENCES chats(id) ON DELETE CASCADE,
    sender_id   UUID REFERENCES users(id) ON DELETE SET NULL,
    content     TEXT,
    type        VARCHAR(10) DEFAULT 'TEXT' CHECK (type IN ('TEXT', 'IMAGE')),
    media_url   TEXT,
    status      VARCHAR(10) DEFAULT 'SENT' CHECK (status IN ('SENT', 'DELIVERED', 'READ')),
    created_at  TIMESTAMP DEFAULT NOW()
);

-- Message read receipts (per user, for group read tracking)
CREATE TABLE IF NOT EXISTS message_reads (
    message_id  UUID REFERENCES messages(id) ON DELETE CASCADE,
    user_id     UUID REFERENCES users(id) ON DELETE CASCADE,
    read_at     TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (message_id, user_id)
);

-- Performance indexes
CREATE INDEX IF NOT EXISTS idx_messages_chat_created ON messages(chat_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_chat_members_user     ON chat_members(user_id);
CREATE INDEX IF NOT EXISTS idx_users_phone           ON users(phone);
CREATE INDEX IF NOT EXISTS idx_messages_sender       ON messages(sender_id);
