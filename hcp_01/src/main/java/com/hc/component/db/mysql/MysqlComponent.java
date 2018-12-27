package com.hc.component.db.mysql;

import org.dom4j.Element;

import com.hc.component.ComponentType;
import com.hc.component.db.mysql.base.MysqlManagerImpl;
import com.hc.share.Component;

public class MysqlComponent extends Component<MysqlManager, MysqlListener> implements MysqlBuilder {
	private String packetPath = "com.hc";
	private int nThread = 2;
	private String dbConfigPath = "HikariConfig.properties";
	
	@Override
	public void setListener(MysqlListener listener) {
		this.listener = listener;
	}

	@Override
	public void setPacketPath(String packetPath) {
		this.packetPath = packetPath;
	}

	@Override
	public void setUseThread(int nThread) {
		this.nThread = nThread;
	}

	@Override
	public void setHikaricpConfigPaht(String dbConfigPath) {
		this.dbConfigPath = dbConfigPath;
	}
	
	@Override
	public void build() throws Exception {
		this.setType(ComponentType.MYSQL);
		this.manager = new MysqlManagerImpl(this.packetPath, this.nThread, this.dbConfigPath);
		this.manager.registListener(this.listener);
		this.listener.onInit(this.manager);
	}
	
	@Override
	public void build(Element element) throws Exception {
		if(element.attributeValue("packetpath") != null)
			this.packetPath = element.attributeValue("packetpath");
		if(element.attributeValue("workethreadnum") != null)
			this.nThread = Integer.parseInt(element.attributeValue("workethreadnum"));
		if(element.attributeValue("hikariconfig") != null)
			this.dbConfigPath = element.attributeValue("hikariconfig");
		if(element.attributeValue("listener") != null) {
			Class<?> listenerClass = Class.forName(element.attributeValue("listener"));
			this.listener = (MysqlListener) listenerClass.newInstance();
		}
		this.build();
	}
}
