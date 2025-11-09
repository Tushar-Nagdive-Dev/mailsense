-- users table
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(512) NOT NULL UNIQUE,
  display_name VARCHAR(255),
  password_hash VARCHAR(1024), -- nullable if OAuth-only user
  roles VARCHAR(255) DEFAULT 'USER',
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  enabled BOOLEAN DEFAULT true
);

-- user_mailboxes table: one user can have many mailboxes (for future multi-mailbox support)
CREATE TABLE user_mailboxes (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  email_address VARCHAR(512) NOT NULL,
  provider VARCHAR(50) NOT NULL, -- e.g., GMAIL, OUTLOOK, IMAP
  provider_user_id VARCHAR(512), -- sub/id returned by provider (optional)
  oauth_access_token TEXT,       -- SHOULD BE ENCRYPTED AT REST
  oauth_refresh_token TEXT,      -- SHOULD BE ENCRYPTED AT REST
  oauth_scope TEXT,
  token_expiry TIMESTAMP WITH TIME ZONE,
  is_primary BOOLEAN DEFAULT false,
  connected_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  status VARCHAR(50) DEFAULT 'CONNECTED' -- CONNECTED / DISCONNECTED / REVOKED
);

CREATE INDEX idx_user_mailboxes_user ON user_mailboxes(user_id);
CREATE UNIQUE INDEX uq_user_primary_mailbox ON user_mailboxes(user_id, email_address);

-- refresh tokens for app sessions (optional, for JWT refresh flow)
CREATE TABLE user_sessions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  refresh_token_hash VARCHAR(1024),
  ip_address VARCHAR(128),
  user_agent TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  expires_at TIMESTAMP WITH TIME ZONE
);