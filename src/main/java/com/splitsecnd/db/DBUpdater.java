package com.splitsecnd.db;

import com.nxttxn.vramel.Exchange;
import com.nxttxn.vramel.LoggingLevel;
import com.nxttxn.vramel.Processor;
import com.nxttxn.vramel.builder.FlowBuilder;

public class DBUpdater extends FlowBuilder {

	@Override
	public void configure() throws Exception {
        fromF("vertx:splitsecnd.dbUpdater")
        .log(LoggingLevel.DEBUG, DBUpdater.class.getName(), "[DBUpdater]: ${body}")
        .onException(Exception.class).handled(true).log(LoggingLevel.ERROR, DBUpdater.class.getName(), "Error").end()
        .process(new UpdateDBProcessor());
       		
	}


	protected class UpdateDBProcessor implements Processor {
		
		@Override
		public void process(Exchange exchange) throws Exception {
			System.out.println(exchange.getIn().getBody(String.class));
		}
		
	}
}
