package com.cmcc.zysoft.schedule.model;
// Generated 2013-2-28 14:16:39 by Hibernate Tools 3.2.2.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

/**
 * Role generated by hbm2java
 */
@Entity
@Table(name="tb_c_role")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"}) 
public class Role  implements java.io.Serializable {


	 private static final long serialVersionUID = 6381443392042172317L;
	 private int roleId;
     private String roleName;
   
    public Role() {
    }

    public Role(int roleId) {
		this.roleId = roleId;
	}
	public Role(int roleId, String roleName) {
		this.roleId = roleId;
		this.roleName = roleName;
	}


	@GenericGenerator(name = "paymentableGenerator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
    @Column(name="role_id", unique=true, nullable=false)
    public int getRoleId() {
        return this.roleId;
    }
    
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    
    
    @Column(name="role_name", length=32)
    public String getRoleName() {
        return this.roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
   

}


