import request from '@/utils/request'; // 假设你使用了 axios 封装

// 文件上传
export function fileUpload(file) {
    return request({
        url: '/file/upload',
        method: 'post',
        data: file, // 使用 data 传递 FormData
        headers: {
            'Content-Type': 'multipart/form-data', // 设置正确的 Content-Type
        },
    });
}
