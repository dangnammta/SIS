/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.transport;

import FCore.Thrift.ClientFactory;
import FCore.Thrift.TClient;
import crdhn.sis.configuration.ConfigHelper;
import crdhn.common.log.thrift.TFLogService;
import org.apache.thrift.protocol.TCompactProtocol;

/**
 *
 * @author longmd
 */
public class FLogServiceClient {

	private static final FLogServiceClient instance = new FLogServiceClient();
	private static final String HOST;
	private static final int PORT;
	private static final String SYSTEMLOG;
	private static final boolean ENABLE;

	static {
		HOST = ConfigHelper.getParamString("flog", "host", "");
		PORT = ConfigHelper.getParamInt("flog", "port");
		SYSTEMLOG = ConfigHelper.getParamString("flog", "systemlog", "SystemLog");
		ENABLE = ConfigHelper.getParamInt("flog", "enable") > 0;
	}

	public static FLogServiceClient getInstance() {
		return instance;
	}

	public TClient getClientWrapper() {
		TClient clientWrapper = ClientFactory.getClient(HOST, PORT, 7200, TFLogService.Client.class, TCompactProtocol.class);
		if (clientWrapper != null) {
			if (!clientWrapper.sureOpen()) {
				return null;
			}
		}
		return clientWrapper;
	}

	public void printSystemLog(String message) {
		printLog(SYSTEMLOG, message);
	}

	public void printLog(String category, String message) {
		if (!ENABLE)
			return;
		TClient clientWrapper = getClientWrapper();
		if (clientWrapper != null) {
			TFLogService.Client aClient = (TFLogService.Client) clientWrapper.getClient();
			if (aClient != null) {
				try {
					aClient.printLog(category, message);
				}
				catch (Exception ex1) {
					clientWrapper.close();
					clientWrapper = getClientWrapper();
					aClient = (TFLogService.Client) clientWrapper.getClient();
					try {
						aClient.printLog(category, message);
					}
					catch (Exception ex2) {
						System.err.println("[Exception] FLogServiceClient.printLog():" + ex2.getMessage());
					}
				}
				ClientFactory.releaseClient(clientWrapper);
			}
		}
		else {
			System.err.println("[Exception] FLogServiceClient.printLog(): clientWrapper is NULL");
		}
	}

	public void printLog_ow(String category, String message) {
		if (!ENABLE)
			return;
		TClient clientWrapper = getClientWrapper();
		if (clientWrapper != null) {
			TFLogService.Client aClient = (TFLogService.Client) clientWrapper.getClient();
			if (aClient != null) {
				try {
					aClient.printLog_ow(category, message);
				}
				catch (Exception ex1) {
					clientWrapper.close();
					clientWrapper = getClientWrapper();
					aClient = (TFLogService.Client) clientWrapper.getClient();
					try {
						aClient.printLog_ow(category, message);
					}
					catch (Exception ex2) {
						System.err.println("[Exception] FLogServiceClient.printLog_ow():" + ex2.getMessage());
					}
				}
				ClientFactory.releaseClient(clientWrapper);
			}
		}
		else {
			System.err.println("[Exception] FLogServiceClient.printLog_ow(): clientWrapper is NULL");
		}
	}
}
