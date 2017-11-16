#sql("find")
select cmi.*,cmc.card_name,cmc.discount from crm_member_info cmi
left join crm_member_cards cmc on cmc.id=cmi.memmbertype where openid=#para(0)
#end
#sql("update")
  #@updateByCond("crm_member_info",cond,where)
#end
#sql("list")
#@queryByCond("crm_member_info",cond,orderBy)
#end