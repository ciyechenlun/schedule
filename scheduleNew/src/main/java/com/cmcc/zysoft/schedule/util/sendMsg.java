// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.util;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import com.chinamobile.openmas.client.Sms;

/**
 * @author 李梦华 <br />
 *         邮箱： li.menghua@ustcinfo.com <br />
 *         描述：sendMsg <br />
 *         版本:1.0.0 <br />
 *         日期： 2013-3-29 下午5:25:21 <br />
 *         CopyRight © 2012 USTC SINOVATE SOFTWARE CO.LTD All Rights Reserved.
 */
public class sendMsg {

	public sendMsg() {
		super();
	}

	/**
	 * 发送短信.
	 * 
	 * @param msg
	 *            信息内容
	 * @param address
	 *            手机号
	 * @return 返回类型：String
	 */
	public static String sendMMM(String msg, String address) {
		Sms sms;
		String gateWayid = "";
		try {
			sms = new Sms("http://211.138.183.2:9080/openmasservice");
			String[] destinationAddresses = new String[] { address };
			String extendCode = ""; // 自定义扩展代码（模块）
			String applicationID = "DefaultApplicationTXL";
			String password = "yCsGnaPEYSTXL";
			// 发送短信
			gateWayid = sms.SendMessage(destinationAddresses, msg, extendCode,
					applicationID, password);
			return gateWayid;
		} catch (AxisFault e) {
			e.printStackTrace();
			return "";
		} catch (RemoteException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 发送短信.(mas接口，批量)
	 * 
	 * @param msg
	 *            信息内容
	 * @param address
	 *            手机号
	 * @return 返回类型：String
	 */
	public static String sendMMAll(String msg, String address) {
		Sms sms;
		try {

			String[] phones = address.split("[,]");

			sms = new Sms("http://211.138.183.2:9080/openmasservice");
			String extendCode = ""; // 自定义扩展代码（模块）
			String applicationID = "DefaultApplicationTXL";
			String password = "yCsGnaPEYSTXL";
			// 发送短信
			sms.SendMessage(phones, msg, extendCode, applicationID, password);
			return "SUCCESS";
		} catch (AxisFault e) {
			e.printStackTrace();
			return "";
		} catch (RemoteException e) {
			e.printStackTrace();
			return "";
		}
	}

}
