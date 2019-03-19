<template>
  <div class="search-wrap">
    <el-input prefix-icon="el-icon-search" v-model="input" placeholder="请输入查询"></el-input>

    <div class="content-wrap" v-if="total > 0">
      <div class="hint-wrap">
        <el-alert type="success" v-bind:title="hint" :closable="false"></el-alert>
      </div>
      <el-row type="flex" justify="center">
        <el-button-group>
          <el-button @click="pageDown" icon="el-icon-arrow-left" v-if="hasPrev">上一页</el-button>
          <el-button @click="pageUp" icon="el-icon-arrow-right" v-if="hasNext">下一页</el-button>
          <el-button @click="download" icon="el-icon-download" v-if="total > 0">下载全部</el-button>
        </el-button-group>
      </el-row>
      <Article
        v-for="article in filteredArticles"
        v-bind:keys="keys"
        v-bind:article="article"
        :key="article.id"
        @removeArticle="recordRemove"
      ></Article>
    </div>
  </div>
</template>

<script>
import Article from "./common/Article.vue";
import { mapState } from "vuex";

export default {
  data() {
    return {
      input: "",
      removed: []
    };
  },
  computed: {
    hint() {
      let removeStr =
        this.removed.length > 0 ? `(排除 ${this.removed.length} 条结果)` : "";

      return `共搜索到 ${this.total -
        this.removed.length} 条结果 ${removeStr}，当前显示 ${this.start}-${
        this.end
      }`;
    },
    filteredArticles() {
      return this.articles.filter(a => this.removed.indexOf(a.id) === -1);
    },
    ...mapState({
      articles: state => state.articles.results,
      size: state => state.articles.size,
      keys: state => state.articles.keys,
      total: state => state.articles.total,
      hasPrev: state => state.articles.from > 0,
      hasNext: state =>
        state.articles.from + state.articles.size < state.articles.total,
      start: state => state.articles.from + 1
    }),
    end() {
      return this.start + this.articles.length - 1;
    }
  },
  methods: {
    pageUp() {
      this.$store.dispatch("articles/pageUp");
      this.$store.dispatch("articles/searchResults", this.input);
    },
    pageDown() {
      this.$store.dispatch("articles/pageDown");
      this.$store.dispatch("articles/searchResults", this.input);
    },
    download() {
      this.$store.dispatch("articles/downloadResults", {
        query: this.input,
        filtered: this.removed
      });
    },
    recordRemove(id) {
      this.removed.push(id);
    }
  },
  watch: {
    input: function(val) {
      this.removed = [];
      this.$store.dispatch("articles/pageReset");
      this.$store.dispatch("articles/searchResults", val);
    }
  },
  components: {
    Article
  }
};
</script>

<style scoped>
.search-wrap {
  padding: 2rem;
  width: 80%;
  margin: auto;
}

.hint-wrap {
  margin: 2rem auto;
}
</style>