package com.splitsecnd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.vertx.java.core.json.JsonObject;

import com.google.gson.Gson;
import com.nxttxn.vramel.Exchange;
import com.nxttxn.vramel.LoggingLevel;
import com.nxttxn.vramel.Processor;
import com.nxttxn.vramel.builder.FlowBuilder;

public class DBUpdater extends FlowBuilder {

	DataSource splitsecndDatasource;
	
	private static final String INSERT_EVENT = "insert into ATPEvent " +
				"(device_id, created, atp_event_id, atp_creation_timestamp, atp_last_update_timestamp)" +
				" values (?,now(),?,?,?)";
	
	
	@Override
	public void configure() throws Exception {
		splitsecndDatasource = setupDataSource();
		
        fromF("vertx:splitsecnd.dbUpdater")
        .log("Inserting ATP Response record.")
        .log("DeviceId2: ${header.deviceId}")
        .log(LoggingLevel.DEBUG, DBUpdater.class.getName(), "[DBUpdater]: ${body}")
        .onException(Exception.class).handled(true).log(LoggingLevel.ERROR, DBUpdater.class.getName(), "Error").end()
        .process(new UpdateDBProcessor()).end();
       		
	}

	private DataSource setupDataSource() {
		JsonObject config = getConfigObject("splitsecnd-db");
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(config.getString("driver"));
		ds.setUsername(config.getString("username"));
		ds.setPassword(config.getString("password"));
		ds.setInitialSize(config.getInteger("initialSize",2));
		ds.setMaxActive(config.getInteger("maxActive",2));
		ds.setMaxIdle(config.getInteger("maxIdle",2));
		ds.setUrl(config.getString("url"));
		
		return ds;
	}
	protected class UpdateDBProcessor implements Processor {
		
		@Override
		public void process(Exchange exchange) throws Exception {
			ATPResponse result = new Gson().fromJson(exchange.getIn().getBody(String.class), ATPResponse.class);
			String device = exchange.getIn().getHeader("deviceId");
			Connection conn = splitsecndDatasource.getConnection();
			
			PreparedStatement ps = conn.prepareStatement(INSERT_EVENT);
			ps.setString(1, device);
			ps.setString(2, result.getEventCorrelation().getAtpEventId());
			ps.setString(3, result.getEventCorrelation().getAtpCreationTimestamp());
			ps.setString(4, result.getEventCorrelation().getAtpLastUpdateTimestamp());
			
			ps.execute();
		}
		
	}
	
	protected class ATPResponse {
		protected class EventCorrelation {
			private String AtpEventId;
			private String AtpCreationTimestamp;
			private String AtpLastUpdateTimestamp;
			public String getAtpEventId() {
				return AtpEventId;
			}
			public void setAtpEventId(String atpEventId) {
				AtpEventId = atpEventId;
			}
			public String getAtpCreationTimestamp() {
				return AtpCreationTimestamp;
			}
			public void setAtpCreationTimestamp(String atpCreationTimestamp) {
				AtpCreationTimestamp = atpCreationTimestamp;
			}
			public String getAtpLastUpdateTimestamp() {
				return AtpLastUpdateTimestamp;
			}
			public void setAtpLastUpdateTimestamp(String atpLastUpdateTimestamp) {
				AtpLastUpdateTimestamp = atpLastUpdateTimestamp;
			}
		}

		private EventCorrelation EventCorrelation;

		public EventCorrelation getEventCorrelation() {
			return EventCorrelation;
		}

		public void setEventCorrelation(EventCorrelation EventCorrelation) {
			this.EventCorrelation = EventCorrelation;
		}
	}
}
