
CREATE TABLE IF NOT EXISTS blocks (
    hash BINARY(32),
    prev_hash BINARY(32),
    mrkl_root BINARY(32),
    timestamp TIMESTAMP,
    target BINARY(32),
    nonce BIGINT,             // positive integers from 0 to 2^32-1
    mrkl_tree BINARY(2097152) // 65536 * 32
);
