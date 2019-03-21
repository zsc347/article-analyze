<template>
  <el-card
    class="box-card article-card paper-bg"
    @mouseenter.native="mouseEnter"
    @mouseleave.native="mouseLeave"
  >
    <el-row>
      <el-col :span="18">
        <h2 class="title" v-html="formattedTitle"></h2>
      </el-col>
      <el-col :span="6" type="flex" justify="end">
        <div class="tools-container" v-if="showTools">
          <el-tooltip class="item" effect="dark" content="删除" placement="top">
            <el-button icon="el-icon-delete" circle size="mini" @click="remove"></el-button>
          </el-tooltip>
        </div>
      </el-col>
    </el-row>

    <p class="author">{{article.author}}</p>
    <div v-html="formattedContent"></div>
    <el-row>
      <el-button type="text" v-for="k in matched" v-bind:article="article" :key="k">{{k}}</el-button>
    </el-row>
  </el-card>
</template>

<script>
import { trim } from "../../lib/util";

function uniq(keys) {
  return keys.filter(k => {
    return !keys.some(v => {
      return v != k && v.indexOf(k) >= 0;
    });
  });
}

function highlight(content, keys) {
  let s = "";

  while (content !== "") {
    let mi = content.length;
    let mx = content.length;

    keys.forEach(key => {
      let i = content.indexOf(key);
      if (i >= 0) {
        mi = Math.min(mi, i);
        mx = mi + key.length;
      }
    });

    if (mi != content.length) {
      s += content.substring(0, mi);
      s += '<span class="highlight">';
      s += content.substring(mi, mx);
      s += "</span>";
    } else {
      s += content;
    }
    content = content.substring(mx);
  }
  return s;
}

export default {
  props: ["article", "keys"],
  data() {
    return {
      showTools: false
    };
  },
  computed: {
    matched() {
      let uniqed = uniq(this.keys);
      return uniqed.filter(key => {
        return (
          this.article.title.indexOf(key) >= 0 ||
          this.article.content.indexOf(key) >= 0
        );
      });
    },
    formattedTitle() {
      return highlight(this.article.title, this.matched);
    },
    formattedContent() {
      return this.article.content
        .split("。")
        .filter(a => a && a.length !== 0)
        .map(line => highlight(trim(line), this.matched) + "。")
        .join("<br>");
    }
  },
  methods: {
    mouseEnter() {
      this.showTools = true;
    },
    mouseLeave() {
      this.showTools = false;
    },
    remove() {
      this.$emit("removeArticle", this.article.id);
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

.tools-container {
  display: flex;
  justify-content: flex-end;
}
</style>