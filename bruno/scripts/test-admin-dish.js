import { spawnSync } from 'node:child_process';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { resetDatabase } from './db.js';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');

async function main() {
  let exitCode = 1;

  try {
    await resetDatabase();
    console.log('[test:admin-dish] running bruno...');

    const result = spawnSync(
      'bru',
      ['run', 'auth/employee-login.bru', 'admin-dish', '-r', '--env', 'local'],
      { cwd: root, stdio: 'inherit', shell: true },
    );

    exitCode = result.status ?? 1;
  } catch (err) {
    console.error('[test:admin-dish] failed:', err.message);
  } finally {
    await resetDatabase();
    console.log('[test:admin-dish] database reset');
  }

  process.exit(exitCode);
}

main();
