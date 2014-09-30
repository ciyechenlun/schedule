// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcc.zysoft.schedule.dao.SystemUserPCDao;
import com.cmcc.zysoft.schedule.model.SystemUser;
import com.starit.common.dao.hibernate.HibernateBaseDao;
import com.starit.common.dao.service.BaseServiceImpl;

/**
 * @author zhangjun
 */
@Service
public class SystemUserPCService extends BaseServiceImpl<SystemUser, String> {

	/**
	 * 属性名称：systemUserPCDao 类型：SystemUserPCDao
	 */
	@Resource
	private SystemUserPCDao systemUserPCDao;
	
	@Override
	public HibernateBaseDao<SystemUser, String> getHibernateBaseDao() {
		return systemUserPCDao;
	}
	public List<SystemUser> loadAll(){
		return this.systemUserPCDao.loadAll();
	}
	/**
	 * 添加用户时，将顺序大于等于disOrder的都加1
	 * @param disOrder
	 */
	public void updateDisOrderIfAdd(int disOrder){
		this.systemUserPCDao.updateDisOrderIfAdd(disOrder);
	}
	/**
	 * 修改用户时，调整其他数据的disOrder
	 * @param disOrder
	 * @param preOrder
	 */
	public void updateDisOrderIfUpdate(int disOrder,int preOrder){
		this.systemUserPCDao.updateDisOrderIfUpdate(disOrder, preOrder);
	} 
	/**
	 * 删除用户时，调整其他数据的disOrder
	 * @param disOrder
	 */
	public void updateDisOrderIfDel(int disOrder){
		this.systemUserPCDao.updateDisOrderIfDel(disOrder);
	} 
}
