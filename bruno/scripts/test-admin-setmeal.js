import { spawnSync } from 'node:child_process';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { dropDatabase, resetDatabase } from './db.js';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');

async function main() {
  let exitCode = 1;

  try {
    await resetDatabase();
    console.log('[test:admin-setmeal] running bruno...');

    const result = spawnSync(
      'bru',
      ['run', 'auth/employee-login.bru', 'admin-setmeal', '-r', '--env', 'local'],
      { cwd: root, stdio: 'inherit', shell: true },
    );

    exitCode = result.status ?? 1;
  } catch (err) {
    console.error('[test:admin-setmeal] failed:', err.message);
  } finally {
    await dropDatabase();
    console.log('[test:admin-setmeal] database dropped');
  }

  process.exit(exitCode);
}

main();
