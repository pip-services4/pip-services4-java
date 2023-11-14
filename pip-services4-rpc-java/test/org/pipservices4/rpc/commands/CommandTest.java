package org.pipservices4.rpc.commands;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.IExecutable;
import org.pipservices4.components.exec.Parameters;

public class CommandTest {
	private Command command;
	
	@Before
	public void setCommand() {
/*		IExecutable interfaceEx = new IExecutable() {
			public Object execute(IContext context, Parameters parameters)
					throws ApplicationException {
				if (context.equals("wrongId")) 
					throw new ApplicationException(null, null, null, "Test error");
				
				return 0;
            }
		};*/
		command = new Command("name", null, new CommandExec());
	}
	
	@Test
	public void testCommand() {
		try {
			command = new Command(null, null, null);
		} catch (NullPointerException ex) {
			assertEquals("Command name is not set", ex.getMessage());
		}
		
		try {
			command = new Command("name", null, null);
		} catch (NullPointerException ex) {
			assertEquals("Command function is not set", ex.getMessage());
		}
	}
	
	@Test
	public void testGetName() {
		assertEquals("name", command.getName());
	}
	
	@Test
	public void testExecute() throws ApplicationException {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
	       map.put(8, "title 8");
	       map.put(11, "title 11");
	    Parameters param = new Parameters(map);

		assertEquals(command.execute(Context.fromTraceId("a"), param), 0);

		try {
			command.execute(Context.fromTraceId("wrongId"), param);
		} catch (ApplicationException e) {
			assertEquals("Test error", e.getMessage());
		}
		
	}
}
