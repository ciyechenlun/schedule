// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.RoleDao;
import com.cmcc.zysoft.schedule.model.Role;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class RoleService extends BaseServiceImpl<Role, Integer> {

	/**
	 * 属性名称：roleDao 类型：roleDao
	 */
	@Resource
	private RoleDao roleDao;
	
	@Override
	public HibernateBaseDao<Role, Integer> getHibernateBaseDao() {
		return roleDao;
	}

}
