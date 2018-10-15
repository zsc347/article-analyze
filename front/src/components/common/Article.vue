<template>
    <el-card class="box-card article-card">
        <h2 class="title">{{article.title}}</h2>
        <p class="author">{{article.author}}</p>
        <div v-html="formattedContent">
        </div>
    </el-card>
</template>

<script>
import { trim } from "../../lib/util";

export default {
  props: ["article"],
  computed: {
    formattedContent() {
      return this.article.content
        .split("。")
        .filter(a => a && a.length !== 0)
        .map(line => trim(line) + "。")
        .join("<br>");
    }
  }
};
</script>

<style scoped>
.title {
  margin: 0.3rem auto;
}
.author {
  margin: 0.4rem auto;
}
.article-card {
  margin: 1rem auto;
}
</style>