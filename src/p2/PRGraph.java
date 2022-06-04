package p2;

/**
 * Graph Interface implemented by PRGraphImpl class
 * 
 * @author Jordi Linares
 *
 */
public interface PRGraph {
	/**
	 * Adds a process to the graph
	 * 
	 * @param name
	 */
	void addProcess(String name);

	/**
	 * Adds a resource to the graph
	 * 
	 * @param name
	 */
	void addResource(String name);

	/**
	 * Process opens a resource
	 * 
	 * @param process
	 * @param resource
	 * @throws DeadlockException
	 */
	void open(String process, String resource) throws DeadlockException;

	/**
	 * Process closes a resource
	 * 
	 * @param process
	 * @param resource
	 */
	void close(String process, String resource);
}
