const { defineConfig } = require('@vue/cli-service');

const port = process.env.port || process.env.npm_config_port || 82 // 端口

// vue.config.js 配置说明
//官方vue.config.js 参考文档 https://cli.vuejs.org/zh/config/#css-loaderoptions
// 这里只列一部分，具体配置参考文档
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false, // 禁用 ESLint
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src'), // 设置路径别名
      },
      extensions: ['.js', '.vue', '.json'], // 添加需要解析的扩展名
    },
  },
  publicPath: process.env.NODE_ENV === "production" ? "/" : "/",
  outputDir: 'dist',
  assetsDir: 'static',
  productionSourceMap: false,
  devServer: {
    host: '0.0.0.0',
    port: port,
    open: true,
    proxy: {
      [process.env.VUE_APP_BASE_API]: {
        target: `http://localhost:8080`,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: ''
        }
      }
    }
  },
});
