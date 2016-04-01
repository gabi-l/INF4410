package ca.polymtl.inf4410.tp2.server;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.FileManager;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operations;
import ca.polymtl.inf4410.tp2.shared.OperationInfo;
import ca.polymtl.inf4410.tp2.shared.ServerOverloadException;


public class Server implements ServerInterface {

	public static void main(String[] args) {
		/* Retrieving the args */
		
		String configFileName = null;
		if (args.length > 0) {
			configFileName = args[0];
		}
		else {
			System.out.println("There were no command... ");
			return;
		}
		Server server = new Server(configFileName);
		server.run();
	}

	private int port;
	private FileManager fileManager = null;
	private int normalCapacity;
	
	public Server(String configFileName) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir/");
		
		/* Extract the config */
		List<String> serverConfig = null;
		try {
			serverConfig = fileManager.readConfig(configFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		this.port = Integer.parseInt(serverConfig.get(0));
		normalCapacity = Integer.parseInt(serverConfig.get(1));
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry(this.port);
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	@Override
	public int signIn() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int fib(int x) throws RemoteException {
		// TODO Auto-generated method stub
		//TODO: Handle correctly the error rates
		int err = 0;
		int result;
		
		if(err == 0) {
			result = Operations.fib(x);
		}
		else {
			result = Operations.fib(x) + 1;
		}
		
		System.out.println("Fib(" + x + ") = " + result);
		return result;
	}

	@Override
	public int prime(int x) throws RemoteException {
		// TODO Auto-generated method stub
		//TODO: Handle correctly the error rates
		int err = 0;
		int result;
		
		if(err == 0) {
			result = Operations.prime(x);
		}
		else {
			result = Operations.prime(x) + 1;
		}
		
		System.out.println("Prime(" + x + ") = " + result);
		return result;
	}
	
	//Verify if the task is accepted by the server
	public boolean isAcceptedTask(Vector<OperationInfo> task) {

		if(task.size() > this.normalCapacity) {
			
			//compute the rate of refusal when the task has a size bigger than the guaranteed size of the server
			int refusalRate = (int)((task.size() - this.normalCapacity) / ((double) this.normalCapacity * 9) * 100);
			if(refusalRate > 100) {
				refusalRate = 100;
			}
			System.out.println("the value of the refusal rate is: " + refusalRate );
			
			//Generate a random number between 0 and 100 ( 0 <= random <= 100)
			int random = (int)( Math.random() * 101);
			System.out.println("the value of the random number is: " + random );
			
			//if the random number is in the refusalRate margin
			if(random <= refusalRate) {
				
				return false;
			}
			
		}
		
		return true;
	}
	
	@Override
	public Vector<Integer> executeTask(Vector<OperationInfo> task) throws RemoteException, ServerOverloadException {
		
		System.out.println("Trying to execute a task with a size of: " + task.size());
		Vector<Integer> opResult = new Vector<Integer>();
		if(!isAcceptedTask(task)) {
			
			throw (new ServerOverloadException());
		}
		
		for(int i = 0; i < task.size(); ++i) {
			
			if(task.get(i).command.equals("fib")) {
				
				//Add the result of fib to the vector of results
				opResult.add(this.fib(Integer.parseInt(task.get(i).operand)));
			}
			else if(task.get(i).command.equals("prime")) {
				
				//Add the result of prime to the vector of results
				opResult.add(this.prime(Integer.parseInt(task.get(i).operand)));
			}			
		}
		
		return opResult;
	}
	
	public void unitTest() {
		
		/* 
		 *  Task 1 will be normal case 
		 *  Task 2 will be the possible overoad case (>0% and <100%)
		 *  Task 3 will be the 100% overload case
		 *                        										*/
		
		Vector<OperationInfo> task1 = new Vector<OperationInfo>();
		Vector<OperationInfo> task2 = new Vector<OperationInfo>();
		Vector<OperationInfo> task3 = new Vector<OperationInfo>();
		
		Vector<Integer> results1; 
		Vector<Integer> results2;
		Vector<Integer> results3;		
		OperationInfo opFib = new OperationInfo("fib", "27", 0);
		OperationInfo opPrime = new OperationInfo("prime", "28", 0);
		for(int i = 0; i < 20; ++i) {
			
			if(i % 2 == 0) {
				
				task1.add(opFib);
			}
			
			else {
				task1.add(opPrime);
			}
			
		}
		
		for(int i = 0; i < 50; ++i) {
					
			if(i % 2 == 0) {
				
				task2.add(opFib);
			}
			
			else {
				task2.add(opPrime);
			}
			
		}
		
		for(int i = 0; i < 150; ++i) {
			
			if(i % 2 == 0) {
				
				task3.add(opFib);
			}
			
			else {
				task3.add(opPrime);
			}
			
		}
		
		try {
			System.out.println("-------------------------Test1-------------------------");
			results1 = executeTask(task1);
			System.out.println("the size of result1 is: " + results1.size());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Server overload exception thrown for task1");
		}
		
		try {
			System.out.println("-------------------------Test2-------------------------");
			results2 = executeTask(task2);
			System.out.println("the size of result2 is: " + results2.size());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Server overload exception thrown for task2");
		}
		
		try {
			System.out.println("-------------------------Test3-------------------------");
			results3 = executeTask(task3);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Server overload exception thrown for task3");
		}
	}
	
}
