import dotenv from 'dotenv';
import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import mysql from 'mysql2/promise';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const sqlFile = path.join(root, 'fixtures', '01_sky.sql');

dotenv.config({ path: path.join(root, '.env') });

const connect = () => mysql.createConnection({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT),
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  multipleStatements: true,
});

export async function dropDatabase() {
  const conn = await connect();
  await conn.query(`DROP DATABASE IF EXISTS \`${process.env.DB_NAME}\``);
  await conn.end();
}

export async function importSchema() {
  const conn = await connect();
  await conn.query(await fs.readFile(sqlFile, 'utf8'));
  await conn.end();
}

export async function resetDatabase() {
  await dropDatabase();
  await importSchema();
}

if (import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href) {
  const actions = { drop: dropDatabase, import: importSchema, reset: resetDatabase };
  actions[process.argv[2]]?.().catch((err) => {
    console.error(err.message);
    process.exit(1);
  }) ?? process.exit(1);
}
