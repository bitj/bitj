
// The Genesis Block
INSERT INTO blocks (
  hash,
  prev_hash,
  mrkl_root,
  timestamp,
  compact_target,
  nonce,
  mrkl_tree
) VALUES (
  X'000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f',
  X'0000000000000000000000000000000000000000000000000000000000000000',
  X'4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b',
  DATEADD('SECOND', 1231006505, DATE '1970-01-01'),
  4294901789,  // ffff001d compactTarget
  2083236893,
  X'4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b',
);

-- // The Genesis Block
-- INSERT INTO blocks (
--     hash,
--     prev_hash,
--     mrkl_root,
--     timestamp,
--     target,
--     nonce,
--     mrkl_tree
-- ) VALUES (
--     X'000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f',
--     X'0000000000000000000000000000000000000000000000000000000000000000',
--     X'4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b',
--     DATEADD('SECOND', 1231006505, DATE '1970-01-01'),
--     X'00000000FFFF0000000000000000000000000000000000000000000000000000',
--     2083236893,
--     X'4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b',
-- );
