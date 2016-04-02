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
	private int percentageOfMaliciousness;
	
	public Server(String configFileName) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir/");
		
		/* Extract the config */
		List<String> serverConfig = null;
		try {
			serverConfig = fileManager.readConfig(configFileName);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		this.port = Integer.parseInt(serverConfig.get(0));
		normalCapacity = Integer.parseInt(serverConfig.get(1));
		percentageOfMaliciousness = Integer.parseInt(serverConfig.get(2));
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
	
	/* calls the fib operation from the class Operations */
	private int fib(int x) {

		int result;
		result = Operations.fib(x);		
		System.out.println("Fib(" + x + ") = " + result);
		return result;
	}
	
	/* Calls the fib operation but modifies the result so that it is not the good answer */
	private int fibMalicious(int x) {

		int result;
		result = Operations.fib(x);
		result += (int) (Math.random() * 5 +1);
		System.out.println("Error Fib(" + x + ") = " + result);
		return result;
	}

	/* calls the prime operation from the class Operations */
	private int prime(int x) {

		int result;
		result = Operations.prime(x);
		System.out.println("Prime(" + x + ") = " + result);
		return result;
	}
	
	/* Calls the prime operation but modifies the result so that it is not the good answer */
	private int primeMalicious(int x) {

		int result;
		result = Operations.prime(x);
		result += (int) (Math.random() * 5 +1);
		System.out.println("Error Prime(" + x + ") = " + result);
		return result;
	}
	
	/* Verify if the task is accepted by the server */
	private boolean isAcceptedTask(Vector<OperationInfo> task) {

		if(task.size() > this.normalCapacity) {
			
			//compute the rate of refusal when the task has a size bigger than the guaranteed size of the server
			int refusalRate = (int)((task.size() - this.normalCapacity) / ((double) this.normalCapacity * 9) * 100);
			if(refusalRate > 100) {
				refusalRate = 100;
			}
			System.out.println("the value of the refusal rate is: " + refusalRate );
			
			//Generate a random number between 1 and 100 ( 1 <= random <= 100)
			int random = (int)( Math.random() * 100 + 1);
			
			//if the random number is in the refusalRate margin
			if(random <= refusalRate) {
				
				return false;
			}
			
		}
		
		return true;
	}
	
	private boolean isMaliciousAnswer() {		
		
		//Generate a random number between 0 and 99 ( 0 <= random <= 99)
		int random = (int) (Math.random() * 100);
		//If the random generated number is out of the malicious margin
		if(random >= percentageOfMaliciousness) {
			return false;
		}
		
		return true;
	}

	@Override
	public int signIn() throws RemoteException {
		
		return 0;
	}
	
	/* This function calls the right operation. */
	@Override
	public Vector<Integer> executeTask(Vector<OperationInfo> task) throws RemoteException, ServerOverloadException {
		
		System.out.println("\nTrying to execute a task with a size of: " + task.size());
		Vector<Integer> opResult = new Vector<Integer>();
		if(!isAcceptedTask(task)) {
			
			throw (new ServerOverloadException());
		}
		
		for(int i = 0; i < task.size(); ++i) {
			
			if(task.get(i).command.equals("fib")) {
				
				if(isMaliciousAnswer() == false) {
					//Add the result of fib to the vector of results
					opResult.add(this.fib(Integer.parseInt(task.get(i).operand)));
				}
				else {
					//Add the result of the malicious fib to the vector of results
					opResult.add(this.fibMalicious(Integer.parseInt(task.get(i).operand)));
				}
				
			}
			else if(task.get(i).command.equals("prime")) {
				
				if(isMaliciousAnswer() == false) {
					//Add the result of prime to the vector of results
					opResult.add(this.prime(Integer.parseInt(task.get(i).operand)));
				}
				else {
					//Add the result of the malicious prime to the vector of results
					opResult.add(this.primeMalicious(Integer.parseInt(task.get(i).operand)));
				}
			}			
		}
		
		return opResult;
	}
	
	/* This function tests the server execution */
	public void unitTest() {
		
		/* 
		 *  Task 1 will be the normal case 
		 *  Task 2 will be the possible overload case (>0% and <100%)
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
			e.printStackTrace();
		} catch (ServerOverloadException e) {
			e.printStackTrace();
			System.out.println("Server overload exception thrown for task1");
		}
		
		try {
			System.out.println("-------------------------Test2-------------------------");
			results2 = executeTask(task2);
			System.out.println("the size of result2 is: " + results2.size());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServerOverloadException e) {
			e.printStackTrace();
			System.out.println("Server overload exception thrown for task2");
		}
		
		try {
			System.out.println("-------------------------Test3-------------------------");
			results3 = executeTask(task3);
			System.out.println("the size of result3 is: " + results3.size());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServerOverloadException e) {
			e.printStackTrace();
			System.out.println("Server overload exception thrown for task3");
		}
	}
	
}
