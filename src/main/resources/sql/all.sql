###定义模版函数 queryByCond
#define queryByCond(table,cond,orderBy)
SELECT *FROM #(table)
  #for(x : cond)
    #(for.index == 0 ? "WHERE" : "AND") #(x.key)#para(x.value)
  #end
  #if(orderBy!=null)ORDER BY #(orderBy)#end
#end
#define updateByCond(table,cond,where)
UPDATE #(table) SET
  #for(x : cond)
    #(for.index > 0 ? "," : "") #(x.key)#para(x.value)
  #end
  #if(where!=null)
    #for(x : where)
    #(for.index == 0 ? "WHERE" : "AND") #(x.key)#para(x.value)
    #end
  #end
#end
#namespace("favorites")
  #include("favorites.sql")
#end
#namespace("member")
  #include("member.sql")
#end
#namespace("order")
  #include("order.sql")
#end
#namespace("app_info")
#include("app_info.sql")
#end