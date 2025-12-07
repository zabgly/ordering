<template>
  <div class="user-edit">
    <el-form :model="form" label-width="120px" v-loading="loading" class="modern-form">
      <!-- 头像上传 -->
      <el-form-item label="用户头像">
        <div class="avatar-container">
          <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
          >
            <img v-if="form.userAvatar" :src="form.userAvatar" class="avatar" alt="用户头像" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="user-balance">余额: {{ form.userBalance }}</div>
        </div>
      </el-form-item>

      <!-- 用户名 -->
      <el-form-item label="用户名">
        <el-input v-model="form.userName" class="modern-input" />
      </el-form-item>

      <!-- 联系方式 -->
      <el-form-item label="联系方式">
        <el-input v-model="form.userPhone" class="modern-input" />
      </el-form-item>

      <!-- 余额 -->
      <el-form-item label="余额">
        <el-input v-model="form.userBalance" class="modern-input" />
      </el-form-item>
      <!-- 提交按钮 -->
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" class="submit-btn">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getById, updatePersonalData } from "@/api/ums/user";
import { fileUpload } from "@/api/publish/file";

export default {
  data() {
    return {
      form: {
        userId: "",
        userName: "",
        userAvatar: "",
        userBalance: 0,
      },
      loading: false, // 添加 loading 状态
    };
  },
  created() {
    this.fetchUserData();
  },
  methods: {
    // 获取用户数据
    async fetchUserData() {
      this.loading = true; // 开始 loading
      try {
        const response = await getById();
        this.form = {
          ...response.data,
        };
      } catch (error) {
        console.error("获取用户数据失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 头像上传前的校验
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith("image/");
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error("只能上传图片文件！");
      }
      if (!isLt2M) {
        this.$message.error("图片大小不能超过 2MB！");
      }

      return isImage && isLt2M;
    },
    // 自定义头像上传
    async handleAvatarUpload({ file }) {
      const formData = new FormData();
      formData.append("file", file); // 确保字段名与后端接口一致
      try {
        const response = await fileUpload(formData);
        this.form.userAvatar = response.data.url;
        this.$message.success("头像上传成功！");
      } catch (error) {
        this.$message.error("头像上传失败！");
        console.error("上传失败:", error);
      }
    },
    // 提交表单
    async handleSubmit() {
      try {
        await updatePersonalData(this.form);
        this.$message.success("用户信息更新成功！");
        this.fetchUserData(); // 重新获取数据，确保展示最新信息
      } catch (error) {
        this.$message.error("用户信息更新失败！");
      }
    },
  },
};
</script>

<style scoped>
.user-edit {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.modern-form {
  border-radius: 8px;
  overflow: hidden;
}

.avatar-container {
  width: 250px;
  height: 320px;
  text-align: center; /* 使头像和余额居中 */
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 50%; /* 使头像变为圆形 */
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  border-radius: 50%; /* 使头像变为圆形 */
}

.user-balance {
  margin-top: 10px; /* 余额与头像之间的间距 */
  font-size: 20px;
  color: #606266;
}

.modern-input {
  border-radius: 4px;
}

.submit-btn {
  background: #409eff;
  color: #ffffff;
  border: none;
}
</style>
