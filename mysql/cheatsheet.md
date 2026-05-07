# Sky MySQL（Docker）常用命令

以下均在**仓库根目录** `sky-take-out` 下执行（路径按你本机调整）。PowerShell 里多条命令请用 `;` 分隔，不要用 `&&`。

## 本地密钥（勿入库）

1. 复制 `mysql/.env.sample` 为 **`mysql/.env`**
2. 填写 **`MYSQL_ROOT_PASSWORD`**（该文件已在仓库根 `.gitignore` 中忽略）

Docker 不推荐把数据库密码写进 `Dockerfile` 的 `ENV`/`ARG`（会进入镜像层与构建历史）。运行时通过 **`-e`** 或 **`--env-file`** 注入即可；生产环境还可配合 Swarm/Kubernetes **Secrets**（官方概述：<https://docs.docker.com/engine/swarm/secrets/>）。

## 构建镜像

```bash
docker build -f mysql/Dockerfile -t sky-mysql:8.0 mysql
```

## 运行容器（容器名 `sky-mysql`，宿主机不绑数据目录，由 Docker 管理存储）

```bash
docker run -d --name sky-mysql -p 3306:3306 --env-file mysql/.env sky-mysql:8.0
```

等价写法（不推荐把密码写进 shell 历史时可改用 env 文件）：

```bash
docker run -d --name sky-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=你的密码 sky-mysql:8.0
```

首次启动且数据目录为空时，会执行镜像内 `docker-entrypoint-initdb.d/` 下的 `01_sky.sql` 初始化库 **`sky_take_out`**。

## 查看日志 / 状态

```bash
docker logs sky-mysql
docker logs -f sky-mysql
docker ps -a --filter name=sky-mysql
```

## 进入容器内用客户端连本机 MySQL

```bash
docker exec -it sky-mysql mysql -uroot -p sky_take_out
```

执行后会提示输入密码，请输入 **`mysql/.env`** 中的 `MYSQL_ROOT_PASSWORD`（不要把你真实密码写进仓库文档或命令行历史）。

## 停止 / 启动 / 删除容器

```bash
docker stop sky-mysql
docker start sky-mysql
docker rm -f sky-mysql
```

## 说明

- **3306 已被占用**：把 `-p 3306:3306` 改成例如 `-p 3307:3306`，应用里 JDBC 端口改为 `3307`。
- **想重建库**：删容器后若仍挂载了旧数据卷，初始化 SQL **不会**再跑；需要清空对应卷或换容器名/换卷后再起。
- **root 密码**：仅来自运行时的 `MYSQL_ROOT_PASSWORD`（如 `mysql/.env`），镜像内**不再**内置默认密码。
