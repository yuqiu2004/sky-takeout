# 概述
- 学习项目
- 资料来源于黑马程序员|苍穹外卖
- 暂定
  - 先敲一遍后端代码
  - 对部分需要改进的部分进行魔改
  - 再魔改前端界面
  - 最后测试上线

# git提交规范
- **格式**：type : subject
- type：
  - feat : 新功能
  - fix : 修复bug
  - docs : 文档改变
  - style : 代码格式改变
  - refactor : 某个已有功能重构
  - perf : 性能优化
  - test : 增加测试
  - build : 改变了build工具 如 grunt换成了 npm
  - revert : 撤销上一次的 commit
  - chore : 构建过程或辅助工具的变动
- subject：简短描述、不超过50个字符

# TODO LIST:
- [ ] 接口`/admin/setmeal/**` -> `/admin/setMeal/**`
- [x] 连带删除
- [x] 删除权限

# 魔改部分
1. 文件存储
   - 原版本使用oss服务进行文件存储 但是我已经有esc服务器了 不想再买oss 所以直接搭了一个minio服务
   - 通过minio来进行文件存储
2. 对象删除
   - 在进行套餐、菜品删除的时候连带删除服务器上的对象