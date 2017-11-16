package test;

import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

public class JavaExecutePython {
    public PythonInterpreter interpreter;

    public JavaExecutePython(){
        interpreter = new PythonInterpreter();
        Properties props = new Properties();
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site","false");
        Properties preprops = System.getProperties();
        PythonInterpreter.initialize(preprops, props, new String[0]);
        PySystemState sys = Py.getSystemState();
        sys.path.add("/Users/ruieliu/develop/jython2.7.0/Lib");
        sys.path.add("/Users/ruieliu/develop/jython2.7.0/Lib/site-packages");
        interpreter.exec("import sys");
    };

    //测试jython是否调用成功
    public void test() {
        interpreter.exec("days=('mod','Tue','Wed','Thu','Fri','Sat','Sun'); ");
        interpreter.exec("print days[1];");
    }

    //java 向Python传递JSONArray数据
    public net.sf.json.JSONArray executJson(String url, JSONArray array){
        List<String> list = new ArrayList<>();
        PyList pyList = new PyList();
        for (int i=0;i<array.size();i++){
            PyObject obj = new PyString(((HashMap)array.get(i)).get("name").toString());
            pyList.append(obj);
        }
        interpreter.execfile(url+"saveModule.py");//加载Py文件
        PyFunction func = (PyFunction) interpreter.get("jsonArray",PyFunction.class);//加载Py的方法
        PyList pylist = (PyList) func.__call__(pyList);
        for (int i=0;i<pylist.size();i++){
            String value = pylist.get(i).toString();
            list.add(value);
        }
        net.sf.json.JSONArray jsonarray = net.sf.json.JSONArray.fromObject(list);
        return jsonarray;
    }

    // java 向python 传递HashMap
    public HashMap<String, String> executMap(String url,
                                             HashMap<String, String> map) {
        interpreter.execfile(url + "my_utils2.py");
        PyFunction func = (PyFunction) interpreter.get("adder",
                PyFunction.class);
        PyDictionary pyDict = new PyDictionary();
        for (String key : map.keySet()) {
            PyString key1 = new PyString("" + key + "");
            pyDict.__setitem__(key1, new PyString("" + map.get(key) + ""));
        }
        PyDictionary pyobj = (PyDictionary) func.__call__(pyDict);
        ConcurrentMap<PyObject, PyObject> map2 = pyobj.getMap();
        for (PyObject key : map2.keySet()) {
            String value = map2.get(key).toString();
            try {
                map.put(key.toString(), new String(
                        value.getBytes("ISO-8859-1"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static void main(String[] args) {
        JavaExecutePython jython = new JavaExecutePython();
        List list = new ArrayList();
        for (int i=0;i<10;i++){
            HashMap hashMap=new HashMap();
            hashMap.put("name","这里什么都没有");
            list.add(hashMap);
        }
        net.sf.json.JSONArray executJson = jython.executJson("src/test/", new JSONArray().fluentAddAll(list));
        for(int i=0;i<executJson.size();i++){
            JSONObject obj = JSONObject.fromObject(executJson.get(i));
        }
        System.out.println("修改后的arrary"+executJson);
    }
}