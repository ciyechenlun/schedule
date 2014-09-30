// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cmcc.zysoft.schedule.model.SystemUser;
import com.starit.common.dao.hibernate.HibernateBaseDaoImpl;

/**
 * @author zhangjun
 */
@Repository
public class SystemUserPCDao extends HibernateBaseDaoImpl<SystemUser, String> {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 获取所有用户，按照disOrder排序
	 */
	@SuppressWarnings("unchecked")
	public List<SystemUser> loadAll(){
		//查找用户列表
		String hql = "from SystemUser as u where u.delFlag!='2' and role.roleId !='1' order by u.disOrder asc";
		return getHibernateTemplate().find(hql);
	}
	
	/**
	 * 添加用户时，将顺序大于等于disOrder的都加1
	 * @param disOrder
	 */
	public void updateDisOrderIfAdd(int disOrder){
		String sql="update tb_c_system_user set dis_order= dis_order+1 where del_flag!='2' and dis_order>=?";
		this.jdbcTemplate.update(sql, disOrder);
	}
	/**
	 * 修改用户时，调整其他数据的disOrder
	 * @param disOrder
	 * @param preOrder
	 */
	public void updateDisOrderIfUpdate(int disOrder,int preOrder){
		String sql="";
		if(disOrder>preOrder){//后移
			sql="update tb_c_system_user set dis_order= dis_order-1 where del_flag!='2' and dis_order>? and dis_order<=?";
			this.jdbcTemplate.update(sql, preOrder,disOrder);
		}else if(disOrder<preOrder){//前移
			sql="update tb_c_system_user set dis_order= dis_order+1 where del_flag!='2' and dis_order>=? and dis_order<?";
			this.jdbcTemplate.update(sql, disOrder,preOrder);
		}else{
			//do nothing
		}
	}
	/**
	 * 删除用户时，调整其他数据的disOrder
	 * @param disOrder
	 */
	public void updateDisOrderIfDel(int disOrder){
		String sql="update tb_c_system_user set dis_order= dis_order-1 where del_flag!='2' and dis_order>? ";
		this.jdbcTemplate.update(sql, disOrder); 
	}
}
