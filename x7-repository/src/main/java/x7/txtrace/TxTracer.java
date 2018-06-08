package x7.txtrace;

import org.apache.log4j.Logger;

import x7.core.tx.TxTraceManager;
import x7.core.tx.TxTraceable;
import x7.core.tx.TxTraceable.TxKeyed;
import x7.core.util.HttpClientUtil;
import x7.core.util.JsonX;
import x7.core.web.ViewEntity;
import x7.repository.dao.Tx;
import x7.repository.redis.JedisConnector_Persistence;

public class TxTracer {

	private final static Logger logger = Logger.getLogger(TxTracer.class);

	public static String getKeyPrefix() {
		return "TX.";
	}

	public static void onProduce(TxTraceable.TxKeyed txKeyed, TxTraceable obj) {
		obj.setTxKeyed(txKeyed);
		String key = getKeyPrefix() + obj.getTxKeyed().key();
		String value = JsonX.toJson(obj);
		JedisConnector_Persistence.getInstance().set(key, value, 120);// 2小时,足够的时间让JOB去检查
	}

	public static void onConsume(String domain, TxTraceable obj) {
		TxKeyed txKeyed = obj.getTxKeyed();
		txKeyed.setCallback(txKeyed.getDomain());// 保存上一服务的回调domain
		txKeyed.setDomain(domain);// 更新当期的domain
		txKeyed.setStep(txKeyed.getStep() + 1);
		TxTraceManager.add(obj);
		String xKey = txKeyed.key();
		Tx.X.begin(xKey);// 开始本服务的事务
	}

	public static void onTryAgain(TxTraceable obj) {
		TxKeyed txKeyed = obj.getTxKeyed();
		txKeyed.setTrys(txKeyed.getTrys() + 1);
	}

	public static boolean ok(String key) {
		String prefix = getKeyPrefix();
		if (!key.startsWith(prefix)) {
			key = prefix + key;
		}
		String str = null;
		try {
			str = JedisConnector_Persistence.getInstance().get(key);
			/*
			 * FIXME
			 */

			JedisConnector_Persistence.getInstance().delete(key);
			return true;
		} catch (Exception e) {
			logger.info("current tx OK fail, txKeyed: " + str);
			return false;
		}
	}

	public static boolean fail(String key) {
		String prefix = getKeyPrefix();
		if (!key.startsWith(prefix)) {
			key = prefix + key;
		}
		String str = null;
		try {
			str = JedisConnector_Persistence.getInstance().get(key);
			/*
			 * FIXME, 持久化参数到数据库, 重新请求, 补偿一次
			 */

			JedisConnector_Persistence.getInstance().delete(key);
			return true;
		} catch (Exception e) {
			logger.info("current tx OK fail, txKeyed: " + str);
			return false;
		}
	}

	public static boolean confirm(String xKey) {

		TxTraceable obj = TxTraceManager.remove(xKey);
		TxKeyed tx = obj.getTxKeyed();

		long step = tx.getStep();

		if (step == 0)
			return true;

		return commit(obj);

	}

	public static boolean cancel(String xKey) {

		TxTraceable obj = TxTraceManager.remove(xKey);
		TxKeyed tx = obj.getTxKeyed();

		long step = tx.getStep();

		if (step == 0)
			return true;

		try {
			Tx.X.rollback(xKey);
		} catch (Exception e) {
			logger.info("current tx ROLLBACK fail, txKeyed: " + tx);
			return false;// 提交阶段出现失败,暂时不做自动补偿, 可尝试手动补单
		}

		if (step == 1) {// 最后一次事务
			/*
			 * 通知producer
			 */
			String url = tx.getCallback() + TxTraceable.ROOT + TxTraceable.FAIL + xKey;

			try {
				String str = HttpClientUtil.getUrl(url);
				ViewEntity ve = JsonX.toObject(str, ViewEntity.class);
				if (Boolean.valueOf(ve.getBody().toString()))
					return true;
				else {
					logger.info("next tx FAIL fail, txKeyed: " + tx);
					return false;
				}
			} catch (Exception e) {
				logger.info("next tx FAIL fail, txKeyed: " + tx);
				return false;
			}
		}

		String url = tx.getCallback() + TxTraceable.ROOT + TxTraceable.CANCEL + xKey;

		try {
			String str = HttpClientUtil.getUrl(url);
			ViewEntity ve = JsonX.toObject(str, ViewEntity.class);
			if (Boolean.valueOf(ve.getBody().toString()))
				return true;
			else {
				logger.info("next tx cancel fail, txKeyed: " + tx);
				return false;
			}
		} catch (Exception e) {
			logger.info("next tx cancel fail, txKeyed: " + tx);
			return false;
		}

	}

	private static boolean commit(TxTraceable obj) {
		TxKeyed tx = obj.getTxKeyed();
		String xKey = tx.key();
		try {
			Tx.X.commit(xKey);
		} catch (Exception e) {
			logger.info("current tx COMMIT fail, txKeyed: " + tx);
			long step = tx.getStep();
			if (step == 1) {// 最后一次事务
				/*
				 * 通知producer
				 */
				String url = tx.getCallback() + TxTraceable.ROOT + TxTraceable.FAIL + xKey;

				try {
					String str = HttpClientUtil.getUrl(url);
					ViewEntity ve = JsonX.toObject(str, ViewEntity.class);
					if (Boolean.valueOf(ve.getBody().toString()))
						return false;
					else {
						logger.info("next tx FAIL fail, txKeyed: " + tx);
						return false;
					}
				} catch (Exception ee) {
					logger.info("next tx FAIL fail, txKeyed: " + tx);
					return false;
				}
			}

			String url = tx.getCallback() + TxTraceable.ROOT + TxTraceable.CANCEL + xKey;

			try {
				String str = HttpClientUtil.getUrl(url);
				ViewEntity ve = JsonX.toObject(str, ViewEntity.class);
				if (Boolean.valueOf(ve.getBody().toString()))
					return false;
				else {
					logger.info("next tx cancel fail, txKeyed: " + tx);
					return false;
				}
			} catch (Exception ee) {
				logger.info("next tx cancel fail, txKeyed: " + tx);
				return false;
			}
			
		}
		
		
		long step = tx.getStep();
		if (step == 1) {// 最后一次事务
			/*
			 * 通知producer
			 */
			String url = tx.getCallback() + TxTraceable.ROOT + TxTraceable.OK + xKey;

			try {
				String str = HttpClientUtil.getUrl(url);
				ViewEntity ve = JsonX.toObject(str, ViewEntity.class);
				if (Boolean.valueOf(ve.getBody().toString()))
					return true;
				else {
					logger.info("next tx OK fail, txKeyed: " + tx);
					return false;
				}
			} catch (Exception e) {
				logger.info("next tx OK fail, txKeyed: " + tx);
				return false;
			}
		}

		String url = tx.getCallback() + TxTraceable.ROOT + TxTraceable.CONFIRM + xKey;

		try {
			String str = HttpClientUtil.getUrl(url);
			ViewEntity ve = JsonX.toObject(str, ViewEntity.class);
			if (Boolean.valueOf(ve.getBody().toString()))
				return true;
			else {
				logger.info("next tx confirm fail, txKeyed: " + tx);
				return false;
			}
		} catch (Exception e) {
			logger.info("next tx confirm fail, txKeyed: " + tx);
			return false;
		}

	}

	
}
