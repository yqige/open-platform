#sql("getOrders")
select b_order.order_type,b_order.create_time,actual_amt,b_order.id,b_tenant.name from b_order left join b_tenant on
b_order.tenant_id=b_tenant.id where 1=1 and ((b_order.payment_status=2  and b_order.order_status=40 )
or (b_order.payment_status=4 and b_order.order_status=50 )) and member_id=#para(0) order by b_order.create_time desc
#end
#sql("getOrderInfo")
select cmi.intergral_chg as member_points,b_order.order_type,b_order.id,order_no,b_order.create_time,actual_amt,b_order.order_status,b_tenant.name
from b_order left join b_tenant on
b_order.tenant_id=b_tenant.id left join crm_member_intergral cmi on cmi.order_number=order_no where 1=1 and b_order.id=#para(0)
#end
#sql("getGoodsList")
select goods_name,goods_qty,vg.bar_code,vg.brand,vg.color,vg.size,
goods_price,CAST(goods_discount AS DECIMAL(8,2))as goods_discount,
goods_actual_amt from b_order_detail bod left join vcc_goods vg on bod.goods_id=vg.id where order_id=#para(0)
#end
#sql("payInfo")
SELECT payment_type,payment_amt FROM b_order_payment_callback where (payment_status='2' or payment_status='4') and order_id=#para(0)
#end