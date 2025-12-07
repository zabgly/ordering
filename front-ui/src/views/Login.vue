<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">{{ isLogin ? '用户登录' : '用户注册' }}</h2>
      <el-form :model="formData" :rules="rules" ref="formRef" class="form">
        <el-form-item prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名">
            <i class="el-icon-user" slot="prefix"></i>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password>
            <i class="el-icon-lock" slot="prefix"></i>
          </el-input>
        </el-form-item>
        <el-form-item v-if="!isLogin" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="确认密码" show-password>
            <i class="el-icon-lock" slot="prefix"></i>
          </el-input>
        </el-form-item>
        <el-button type="primary" class="submit-btn" @click="handleSubmit" :loading="loading">
          {{ isLogin ? '登录' : '注册' }}
        </el-button>
      </el-form>
      <p class="toggle-text" @click="toggleForm">
        {{ isLogin ? '没有账号？点击注册' : '已有账号？点击登录' }}
      </p>
    </el-card>
  </div>
</template>

<script>
import { login, register } from '@/api/ums/user';
import {setToken, setUser} from '@/utils/auth';

export default {
  data() {
    return {
      isLogin: true, // 是否是登录状态
      loading: false, // 加载状态
      formData: {
        username: '',
        password: '',
        confirmPassword: '',
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }],
      },
    };
  },
  methods: {
    toggleForm() {
      this.isLogin = !this.isLogin;
    },
    async handleSubmit() {
      this.loading = true;
      try {
        let res;
        if (this.isLogin) {
          res = await login(this.formData);
          setToken(res.data.token)
          setUser(res.data.user)
          this.$message.success('登录成功！');
          this.$router.push('/').then(() => {
            window.location.reload();
          });
        } else {
          if (this.formData.password !== this.formData.confirmPassword) {
            this.$message.error('两次密码输入不一致');
            return;
          }
          res = await register(this.formData);
          this.$message.success('注册成功，请登录！');
          this.isLogin = true;
        }
      } catch (error) {
        this.$message.error(error.message || '操作失败');
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(to right, #ff7e00, #ffffff);
}
.login-card {
  width: 350px;
  padding: 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.title {
  text-align: center;
  color: #ff7e00;
}
.form {
  margin-top: 20px;
}
.submit-btn {
  width: 100%;
  background-color: #ff7e00;
  border-color: #ff7e00;
}
.submit-btn:hover {
  background-color: #e66b00;
}
.toggle-text {
  text-align: center;
  color: #ff7e00;
  cursor: pointer;
  margin-top: 10px;
}
.toggle-text:hover {
  text-decoration: underline;
}
</style>
