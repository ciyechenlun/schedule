// ~ CopyRight © 2012 USTC SINOVATE  SOFTWARE CO.LTD All Rights Reserved.
package com.cmcc.zysoft.schedule.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 李梦华 <br />
 *         邮箱： li.menghua@ustcinfo.com <br />
 *         描述：Token 生成令牌及令牌识别 <br />
 *         版本:1.0.0 <br />
 *         日期： 2013-3-5 下午5:45:56 <br />
 *         CopyRight © 2012 USTC SINOVATE SOFTWARE CO.LTD All Rights Reserved.
 */
public class Tokens {

	public Tokens() {
		super();
	}

	/**
	 * 生成令牌.
	 * 
	 * @param imei
	 * @param nowDate
	 *            当前时间
	 * @param salt
	 *            令牌盐
	 * @return 返回类型：String
	 */
	public static String generate(String imei, Date nowDate, String salt) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String nowTime = sd.format(nowDate);
		String token = MD5Tools.encodePassword(imei + nowTime, salt);
		return token;
	}

	/**
	 * 令牌校验.
	 * 
	 * @param imei
	 * @param tokenValue
	 *            令牌值
	 * @param tokenSalt
	 *            令牌盐
	 * @param tokenDate
	 *            令牌有效期
	 * @param createTime
	 *            令牌生成时间
	 * @return
	 * @throws ParseException
	 *             返回类型：boolean
	 */
	public static boolean validate(String imei, String tokenValue,
			String tokenSalt, String tokenDate, String createTime)
			throws ParseException {
		Date nowDate = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long nowTime = nowDate.getTime();
		long valueTime = sd.parse(tokenDate).getTime();
		if (nowTime > valueTime) {
			// 令牌已过期
			return false;
		} else {
			Date createDate = sd.parse(createTime);
			String newToken = generate(imei, createDate, tokenSalt);
			return newToken.equals(tokenValue);
		}
	}

}
