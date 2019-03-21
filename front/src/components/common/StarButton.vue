<template>
  <el-button icon="el-icon-star-off" circle @click="open"></el-button>
</template>

<script>
export default {
  methods: {
    open() {
      this.$prompt("请输入标记", "保存当前状态", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputErrorMessage: "标题不能为空，且不能包含特殊字符"
      })
        .then(({ value }) => {
          this.$emit("starState", value);
        })
        .catch(err => {
          console.log(err);
        });
    }
  }
};
</script>