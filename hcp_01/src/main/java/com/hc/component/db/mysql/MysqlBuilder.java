package com.hc.component.db.mysql;

import com.hc.share.Builder;

public interface MysqlBuilder extends Builder<MysqlListener> {
	void setPacketPath(String packetPath);
	void setUseThread(int nThread);
	void setHikaricpConfigPaht(String dbConfigPath);
}
