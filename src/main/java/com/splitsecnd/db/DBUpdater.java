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
	
	private static final String INSERT_EVENT = "insert into EmergencyEvent " +
				"(device_id, created, event_sent, event_response)" +
				" values (?,now(),?,?)";
	
	
	@Override
	public void configure() throws Exception {
		splitsecndDatasource = setupDataSource();
		
        fromF("vertx:splitsecnd.dbUpdater")
        .log("Inserting Event Response record.")
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
			Exchange eventResponseExchange = exchange.getIn().getBody(Exchange.class);
			String device = exchange.getIn().getHeader("ein");
			String eventSent = (String) exchange.getProperty("eventJson");
			Connection conn = splitsecndDatasource.getConnection();
			
			PreparedStatement ps = conn.prepareStatement(INSERT_EVENT);
			ps.setString(1, device);
			ps.setString(2, eventSent);
			ps.setString(3, eventResponseExchange.getIn().getBody(String.class));
			
			ps.execute();
			
			ps.close();
			conn.close();
		}
		
	}	
}
