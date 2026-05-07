# 基于官方 MySQL 8.0，首次启动会执行 /docker-entrypoint-initdb.d/ 下的 SQL。
#
# 敏感信息勿写入镜像层：ENV/ARG 中的密码会留在镜像元数据与历史中。
# 参见 Docker Build checks：https://docs.docker.com/reference/build-checks/secrets-used-in-arg-or-env/
# MySQL 官方镜像要求在「运行时」提供 root 密码等，见：https://hub.docker.com/_/mysql
#
# 构建（仓库根目录）:
#   docker build -f mysql/Dockerfile -t sky-mysql:8.0 mysql
# 运行（用 gitignore 的 mysql/.env，勿提交）:
#   docker run -d --name sky-mysql -p 3306:3306 --env-file mysql/.env sky-mysql:8.0

FROM mysql:8.0

ENV MYSQL_DATABASE=sky_take_out
ENV LANG=C.UTF-8

COPY 01_sky.sql /docker-entrypoint-initdb.d/01-sky.sql

EXPOSE 3306
