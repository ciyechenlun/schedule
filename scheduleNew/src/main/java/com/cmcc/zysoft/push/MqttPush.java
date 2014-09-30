package com.cmcc.zysoft.push;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

public class MqttPush {

	private static final MQTT mqtt = new MQTT();
	private static final String MQTT_SERVER = "120.209.139.127";
	private static final int MQTT_PORT = 8083; 
	private static FutureConnection connection = null;
	private static final long TIME_OUT = 10;

	/**
	 * 初始化创建连接.
	 */
	private static final void init() {
		try {
			mqtt.setHost(MQTT_SERVER, MQTT_PORT);
			if (connection == null || !connection.isConnected()) {
				connection = mqtt.futureConnection();
				connection.connect().await(TIME_OUT, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回收资源.
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			if (connection != null || connection.isConnected()) {
				connection.disconnect().await();
				connection = null;
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * 判断是否在线
	 * 
	 * @return
	 */
	private static boolean isConnect() {
		return connection != null && connection.isConnected();
	}

	/**
	 * 给一个主题实时发送多条消息.
	 * 
	 * @param topic
	 *            主题
	 * @param msg
	 *            多条消息
	 */
	public static void sendMultiMsg(String topic, String[] msg) {
		if (!isConnect()) {
			init();
		}
		UTF8Buffer mTopic = new UTF8Buffer(topic);
		final LinkedList<Future<Void>> queue = new LinkedList<Future<Void>>();
		try {
			for (String ms : msg) {
				queue.add(connection.publish(mTopic, new UTF8Buffer(ms),
						QoS.AT_LEAST_ONCE, false));
				if (queue.size() >= 1000) {// 批次提交
					queue.removeFirst().await(TIME_OUT, TimeUnit.SECONDS);
				}
			}
			while (!queue.isEmpty()) {
				queue.removeFirst().await(TIME_OUT, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给多个主题发送同一条消息.
	 * 
	 * @param topics
	 *            多个主题
	 * @param msg
	 *            单条消息
	 */
	public static void sendMultiTopic(String[] topics, String msg) {
		if (!isConnect()) {
			init();
		}
		Buffer mMsg = new UTF8Buffer(msg);
		final LinkedList<Future<Void>> queue = new LinkedList<Future<Void>>();
		try {
			for (String tp : topics) {
				queue.add(connection.publish(new UTF8Buffer(tp), mMsg,
						QoS.AT_MOST_ONCE, false));
				if (queue.size() >= 1000) {// 批次提交
					queue.removeFirst().await(TIME_OUT, TimeUnit.SECONDS);
				}
			}
			while (!queue.isEmpty()) {
				queue.removeFirst().await(TIME_OUT, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批次发送推送消息.
	 * 
	 * @param msgs
	 *            二维数组[[推送主题，推送消息],[推送主题，推送消息]]
	 */
	public static void batchSend(String[][] msgs) {
		if (!isConnect()) {
			init();
		}
		final LinkedList<Future<Void>> queue = new LinkedList<Future<Void>>();
		try {
			for (String[] tp : msgs) {
				if (tp.length < 2) {
					continue;
				}
				UTF8Buffer topic = new UTF8Buffer(tp[0]);
				UTF8Buffer msg = new UTF8Buffer(tp[1]);
				queue.add(connection.publish(topic, msg, QoS.AT_MOST_ONCE,
						false));
				if (queue.size() >= 1000) {// 批次提交
					queue.removeFirst().await(TIME_OUT, TimeUnit.SECONDS);
				}
			}
			while (!queue.isEmpty()) {
				queue.removeFirst().await(TIME_OUT, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		long t0 = System.currentTimeMillis();

		String[] tps = new String[1];
		for (int i = 0; i < tps.length; i++) {
			tps[i] = i + ":nihao" + "1111111";
		}

		sendMultiMsg("aa", tps);
		System.out.println(System.currentTimeMillis() - t0);
	}
}
