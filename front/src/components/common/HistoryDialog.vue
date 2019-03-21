<template>
  <el-dialog title="历史查询" :visible.sync="dialogVisible">
    <el-table :data="history">
      <el-table-column property="title" label="标记" width="250"></el-table-column>
      <el-table-column property="query" label="查询"></el-table-column>
      <el-table-column fixed="right" label="操作" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleLoad(scope.row)">加载</el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
import { mapState } from "vuex";

export default {
  props: ["visible"],
  data() {
    return {};
  },
  computed: {
    ...mapState({
      history: state => state.history.states
    }),
    dialogVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit("close");
      }
    }
  },
  methods: {
    handleLoad(row) {
      this.$emit("loadState", row);
    },
    handleDelete(row) {
      this.$emit("removeState", row);
    }
  },
  watch: {
    dialogVisible: function(val) {
      console.log(val);
    }
  }
};
</script>