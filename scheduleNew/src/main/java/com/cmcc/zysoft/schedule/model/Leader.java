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
 * Leader generated by hbm2java
 */
@Entity
@Table(name="tb_c_leader")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"}) 
public class Leader  implements java.io.Serializable {


	 private static final long serialVersionUID = 6381443392042172316L;
	 private int leaderId;
     private String leaderName;
     private String mobile;
   
    public Leader() {
    }

    public Leader(int leaderId) {
		this.leaderId = leaderId;
	}
	public Leader(int leaderId, String leaderName,String mobile) {
		this.leaderId = leaderId;
		this.leaderName = leaderName;
		this.mobile=mobile;
	}


	@GenericGenerator(name = "paymentableGenerator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
    @Column(name="leader_id", unique=true, nullable=false)
    public int getLeaderId() {
        return this.leaderId;
    }
    
    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }
    
    
    @Column(name="leader_name", length=32)
    public String getLeaderName() {
        return this.leaderName;
    }
    
    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    @Column(name="mobile", length=11)
	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
   

}


