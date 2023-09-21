## 部署

---

> 前置条件: 
> 
> JDK 17+ 
> 
> maven 3.8.1+
> 
> postgresql 15+ 并创建对应的数据库
>
> ```
> // 创建**新的**数据库用户
> create user <数据库用户名> with password '<数据库密码>';
> create database <数据库名> owner <数据库用户名>;
> grant all privileges on database <数据库名> to <数据库用户名>;
> \q //退出
> ```

修改springboot配置 `beatmap-selection` 将运行路径,osu oauthToken 填入

```
beatmap-selection:
  file-path: #your run path
  ssl: false
  local-url: localhost:16113
  osu:
    callback-url: /callback
    oauth:
      id: #yourID
      token: #yourToken
```

修改springboot配置 `springboot.datasource` 将数据库配置填入

```
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://127.0.0.1:5432/map_pool
    username: root
    password: root
    hikari:
      maximum-pool-size: 10  # 数据库连接池最大连接数
      minimum-idle: 5  # 数据库连接池最小空闲连接数
      connection-test-query: SELECT 1  # 数据库连接测试语句
```

确认路径正确, 执行 `mvn spring-boot:run`
