#! usr/bin/python
# coding:utf-8
# filename:pickling.py

def jsonArray(xx):
    print xx
    list = [];
    dict = {};
    for i in range(0, len(xx)):
        b = xx[i]  # 转换成字典
        b = "(" + b + ")"  # 加上（）
        b = str(b)
        python_object = b;
        # python_object = json.loads(python_object)
        list.append(python_object)

    return list  # 返回的是一个列表
