-- 表【会员信息】相关功能SQL语句
-- 导入数据库前替换以下参数：
-- {moduleName}：模块名称
-- {moduleIds}：模块ids

-- 列表 list
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('c1141c964f484f4784362ec1748d2d6c', '1', '0', '/bs/wxapi/memberInfo/list', 0, '{moduleName}', 0, '列表', '{moduleIds}', '1');

-- 新增 add
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('4fdf7f5ac6484fe6a1529b96bc0757a8', '1', '0', '/bs/wxapi/memberInfo/add', 0, '{moduleName}', 0, '新增', '{moduleIds}', '0');

-- 保存
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('2792d371eba145758447531cffaf0b5d', '1', '0', '/bs/wxapi/memberInfo/save', 0, '{moduleName}', 0, '保存', '{moduleIds}', '0');

-- 编辑 edit
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('db50857b21024c3eaf9e444fd74bb785', '1', '0', '/bs/wxapi/memberInfo/edit', 0, '{moduleName}', 0, '编辑', '{moduleIds}', '0');

-- 修改 update
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('c85282a133ef4bf794718d4dabe1e384', '1', '0', '/bs/wxapi/memberInfo/update', 0, '{moduleName}', 0, '修改', '{moduleIds}', '0');

-- 查看 view
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('5960e20628a24b19ab5503ca242e65d7', '1', '0', '/bs/wxapi/memberInfo/view', 0, '{moduleName}', 0, '查看', '{moduleIds}', '0');

-- 删除 delete
insert into pt_operator(ids,privilegess, rowfilter, url,version, modulenames, formtoken, names, moduleids, splitpage)
values('73a35a518f424e04ae988f8a6cd08d9b', '1', '0', '/bs/wxapi/memberInfo/delete', 0, '{moduleName}', 0, '删除', '{moduleIds}', '0');


