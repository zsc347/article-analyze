<template>
  <div class="search-wrap">
    <el-input prefix-icon="el-icon-search" v-model="input" @input="onInput" placeholder="请输入查询">
      <el-button @click="tryLoadHistoryThenOpenDialog" icon="el-icon-tickets" slot="append"></el-button>
    </el-input>

    <div class="content-wrap" v-if="total > 0">
      <div class="hint-wrap">
        <el-alert type="success" v-bind:title="hint" :closable="false"></el-alert>
      </div>
      <el-row type="flex" justify="center">
        <el-col :span="12">
          <el-row type="flex" justify="start">
            <el-button-group>
              <el-button @click="pageDown" icon="el-icon-arrow-left" :disabled="!hasPrev">上一页</el-button>
              <el-button @click="pageUp" icon="el-icon-arrow-right" :disabled="!hasNext">下一页</el-button>
            </el-button-group>
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row type="flex" justify="end">
            <el-tooltip class="item" effect="dark" content="导出" placement="bottom">
              <el-button @click="download" icon="el-icon-download" v-if="total > 0" circle></el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="收藏" placement="bottom">
              <StarButton @starState="addState"></StarButton>
            </el-tooltip>
          </el-row>
        </el-col>
      </el-row>
      <Article
        v-for="article in filteredArticles"
        v-bind:keys="keys"
        v-bind:article="article"
        :key="article.id"
        @removeArticle="recordRemove"
      ></Article>
    </div>
    <HistroyDialog
      :visible="historyDialogVisible"
      @loadState="loadState"
      @removeState="removeState"
      @close="historyDialogVisible = false"
    ></HistroyDialog>
  </div>
</template>

<script>
import Article from "./common/Article.vue";
import StarButton from "./common/StarButton.vue";
import HistroyDialog from "./common/HistoryDialog.vue";
import { mapState } from "vuex";

export default {
  data() {
    return {
      histroyReady: false,
      historyDialogVisible: false,
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
    onInput() {
      this.removed = [];
    },
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
    addState(title) {
      console.log("addState", title);
      this.$store.dispatch("history/addState", {
        title: title,
        query: this.input,
        filtered: this.removed
      });
    },
    loadState(s) {
      console.log("loadState", JSON.stringify(s));
      this.input = s.query;
      this.removed = s.filtered;
      this.historyDialogVisible = false;
    },
    removeState(s) {
      console.log("removeState", s.id);
      this.$store.dispatch("history/delState", s.id);
    },
    fetchState() {
      return this.$store.dispatch("history/fetchState");
    },
    recordRemove(id) {
      this.removed.push(id);
    },
    tryLoadHistoryThenOpenDialog() {
      if (this.histroyReady) {
        this.historyDialogVisible = true;
      } else {
        let self = this;
        this.fetchState().then(() => {
          self.histroyReady = true;
          self.historyDialogVisible = true;
        });
      }
    }
  },
  watch: {
    input: function(val) {
      this.$store.dispatch("articles/pageReset");
      this.$store.dispatch("articles/searchResults", val);
    }
  },
  components: {
    Article,
    StarButton,
    HistroyDialog
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

.empty-background {
}
</style>