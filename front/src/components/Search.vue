<template>
    <div class="search-wrap">
        <el-input prefix-icon="el-icon-search" 
            v-model="input"
            placeholder="请输入查询"></el-input>

            <div class="content-wrap" v-if="total > 0">
              <div class="hint-wrap">
                <el-alert type="success" v-bind:title="hint" :closable="false"></el-alert>
              </div>
              <Article v-for="article in articles" v-bind:article="article" :key=article.id></Article>
        </div>
    </div>
</template>

<script>
import Article from "./common/Article.vue";
import { mapState } from "vuex";

export default {
  data() {
    return {
      input: ""
    };
  },
  computed: {
    hint() {
      return `共搜索到 ${this.total} 条结果`
    },
    ...mapState({
      articles: state => state.articles.results,
      total: state => state.articles.total
    })
  },
  watch: {
    input: function(val) {
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
}

.hint-wrap {
  margin: 2rem auto;
}
</style>