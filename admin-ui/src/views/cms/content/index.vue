<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="轮播图" prop="imageUrl">
        <image-upload v-model="form.imageUrl" />
      </el-form-item>
      <el-form-item label="内容描述富文本">
        <editor v-model="form.description" :min-height="192" />
      </el-form-item>
    </el-form>

    <div class="footer">
      <el-button type="primary" @click="submitForm">保存</el-button>
    </div>
  </div>
</template>

<script>
import { getContent, updateContent } from "@/api/cms/content";

export default {
  name: "ContentEditor",
  data() {
    return {
      // 表单数据
      form: {
        id: null,
        imageUrl: null,
        description: null,
      },
      // 表单校验
      rules: {
        imageUrl: [
          { required: true, message: "轮播图不能为空", trigger: "blur" },
        ],
        description: [
          { required: true, message: "内容描述富文本不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    /** 加载数据 */
    loadData() {
      getContent().then((response) => {
        this.form = response.data;
      });
    },
    /** 提交表单 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          updateContent(this.form).then(() => {
            this.$modal.msgSuccess("保存成功");
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.footer {
  margin-top: 20px;
  text-align: center;
}
</style>
