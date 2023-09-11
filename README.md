## 部署

---

> 前置条件: 
> 
> JDK 17+ 
> 
> maven 3.8.1+
> 
> postgresql 15+ 并创建对应的数据库

修改springboot配置 `beatmap-selection` 将运行路径,osu oauthToken 填入

修改springboot配置 `springboot.datasource` 将数据库配置填入

确认路径正确, 执行 `mvn spring-boot:run`