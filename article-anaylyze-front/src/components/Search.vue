<template>
    <div class="search-wrap">
        <el-input prefix-icon="el-icon-search" 
            v-model="input"
            placeholder="请输入查询"></el-input>

        <div class="content-wrap">
            <Article v-for="article in articles"
             v-bind:article="article" :key=article.title></Article>            
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
    ...mapState({
      articles: state => state.articles.results
    })
  },
  watch: {
    input: function(val) {
      this.$store.dispatch("articles/searchResults");
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
</style>