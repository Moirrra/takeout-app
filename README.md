# takeout-app

本项目来源于苍穹外卖

## Study Note
### Day1
- 开发环境搭建
- Nginx
- 登录功能完善
  - 密码加密 MD5
- 接口文档
  - swagger
  

### Day2 
#### 员工管理
##### 新增员工
- 密码加密
  - BeanUtils.copyProperties 对象属性拷贝
  - DigestUtils.md5DigestAsHex() 默认密码常量
- 新增员工
  - 问题1：用户已存在：sql抛出异常
  - 解决办法：GlobalExceptionHandler捕获 SQLIntegrityConstraintViolationException异常，返回Result.error
  - 问题2：新增员工时动态获取创建人&修改人ID
  - 解决办法：jwt保存了用户id, 拦截器里ThreadLocal保存id,使用时取出 (相关方法封装在BaseContext里)

##### 员工分页查询
- 所有的分页查询，统一封装成PageResult对象 Result.success(pageResult)
- pageHelper插件
  - mapper.xml中编写sql (坑：不要加; 因为LIMIT会拼接在后面)
- 返回Page封装成PageResult
- 代码完善
  - 问题：返回的时间为数组格式
  - 解决办法：
    - 方式一：属性加注解，日期格式化
    - 方式二：WebMvcConfiguration中扩展消息转换器，统一对日期类型进行格式化处理

##### 编辑员工
- 因为前端返回json格式数据 所以参数要加@RequestBody

### Day3 
#### 分类管理
#### 公共字段字段填充
AOP切面编程：
- 自定义注解 @AutoFill，用于标识需要进行公共字段自动填充的方法
- 自定义切面类 AutoFillAspect，统一拦截加入了 AutoFill 注解的方法，通过反射为公共字段赋值
- 在 Mapper 的方法上加入 AutoFill 注解
#### 菜品管理
##### 新增菜品
- 文件上传：暂时只实现了本地存储
- 多次sql操作，开启声明式事务管理@Transactional
- insert对象后需要主键id
  - useGeneratedKeys 需要获取自动生成的主键值
  - keyProperty 将主键值返回给对象的id
##### 菜品分页查询
##### 删除菜品 (未完成)
##### 修改菜品 (未完成)
