# article-analyze

### 功能说明

简单的文献分析工具。

包含三部分功能

**1. 爬取**

定制功能
用于爬取特定网站的文献

**2. 提取分析**

分析特定词组的出现频率以及其组合出现的频率

**3. 可视化**

以多种视图展现分析结果

后台使用vertx，无数据库。前台使用Vue。

### 编译

```
git clone git@github.com:zsc347/article-analyze.git
cd article-analyze/front
npm install
npm run build
cd ../main
mvn clean package
```

### 运行
```
java -jar article-analyze-fat.jar
```
