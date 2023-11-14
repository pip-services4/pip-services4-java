package org.pipservices4.container.example;

public class Program {
	
	public static void main(String[] args)
    {
        try
        {
        	DummyProcess process = new DummyProcess();
            process.run(args);

        }
        catch (Exception ex)
        {
        	System.err.println(ex);
        }
    }
}