package p2;

import java.util.*;

/**
 * PRGraphImpl class
 * 
 * @author Jordi Linares
 *
 */
public class PRGraphImpl implements PRGraph {
	// HashMap that will store each node of the Graph
	private Map<String, Node> map;
	// HashSet used to check if a deadlock is produced
	private Set<String> setName;

	/**
	 * Creates a new Graph by initializing the map
	 */
	public PRGraphImpl() {
		map = new HashMap<>();
		setName = new HashSet<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addProcess(String name) {
		Node processNode = new Process(name);
		map.put(name, processNode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addResource(String name) {
		Node resourceNode = new Resource(name);
		map.put(name, resourceNode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open(String process, String resource) throws DeadlockException {
		if (!map.containsKey(process) || !map.containsKey(resource))
			throw new IllegalArgumentException(
					"Process " + process + " or Resource " + resource + " may not have been declared before");

		// Getting the process & resource we want from hashmap
		Process proc = (Process) map.get(process);
		Resource res = (Resource) map.get(resource);

		// If the nodeSet of resource is empty, it means the resource is not assigned to
		// a process, so we just add that process to the nodeSet
		if (res.nodeSet.isEmpty()) {
			res.nodeSet.add(proc);
		}

		// Else, it means that that resource is already assigned to another process, so
		// what we do is we add the process into the linkedNodeSet
		// of the resource (enqueue), and we also add the resource into the nodeSet of
		// the process, indicating we're waiting for that resource
		// Finally, we check if a Deadlock has been created
		else {
			res.linkedNodeSet.add(proc);
			proc.nodeSet.add(res);
			checkDeadlock(process, resource);

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close(String process, String resource) {
		if (!map.containsKey(process) || !map.containsKey(resource))
			throw new IllegalArgumentException(
					"Process " + process + " or Resource " + resource + " may not have been declared before");

		// Getting the process & resource we want from hashmap
		Process proc = (Process) map.get(process);
		Resource res = (Resource) map.get(resource);

		if (!res.nodeSet.contains(proc)) {
			throw new IllegalStateException("The resource " + resource + " is not assigned to process " + process);
		}

		// First of all we always remove the process from the resource nodeSet, because
		// we're closing it
		res.nodeSet.remove(proc);

		// Then, if that resource has some processes in the linkedNodeSet waiting, we
		// iterate once in order to get the first one from the queue,
		// we remove that process from the queue, we also remove the resource from the
		// nodeSet of the process (as we're no longer waiting for it),
		// and we add that process into the nodeSet of the resource
		if (res.linkedNodeSet.size() > 0) {
			Process p = res.linkedNodeSet.iterator().next();
			res.linkedNodeSet.remove(p);
			p.nodeSet.remove(res);
			res.nodeSet.add(p);
		}
	}

	/**
	 * Prints the Graph in the appropriate format
	 */
	@Override
	public String toString() {
		String s1 = "";
		String s2 = "";

		for (String key : map.keySet()) {

			// Printing Processes
			if (map.get(key) instanceof Process) {
				s1 += "Process " + key;
				Process p = (Process) map.get(key);

				s1 = hasResource(s1, p);

				for (Node n : map.get(key).nodeSet) {
					s1 += ". It is waiting for " + n.name;
				}
				s1 += "\n";
			}

			// Printing Resources
			else {
				s2 += "Resource " + key;
				for (Node n : map.get(key).nodeSet) {
					s2 += ", it is assigned to " + n.name;
				}
				s2 += "\n";
			}
		}

		return s1 + s2;
	}

	// Private method to check which resources in the hashmap have the process in
	// their nodeSets, in order to print them
	private String hasResource(String s, Process p) {
		boolean first = true;
		for (String key : map.keySet()) {
			if (map.get(key) instanceof Resource) {
				if (map.get(key).nodeSet.contains(p)) {
					if (first) {
						s += " has " + map.get(key).name;
						first = false;
					} else
						s += ", " + map.get(key).name;
				}
			}
		}
		return s;
	}

	// Private recursive method to check if there's any deadlock in the graph, uses
	// setName to store every Node name and throws a DeadlockException
	// when a duplicated value is added into setName
	private void checkDeadlock(String process, String resource) throws DeadlockException {

		Resource r = (Resource) map.get(resource);
		if (r.nodeSet.size() > 0) {
			for (Node n : r.nodeSet) {
				for (Node res : n.nodeSet) {
					if (setName.contains(n.name) || setName.contains(res.name)) {
						throw new DeadlockException();
					}
					setName.add(n.name);
					setName.add(res.name);
					checkDeadlock(n.name, res.name);
				}
			}
		}
		setName.clear();
	}

	// Node class, it is a parent class that has two children: Process & Resource
	private static class Node {
		// Set of nodes that are pointed by this node
		Set<Node> nodeSet;
		// Node name
		String name;

		private Node() {
			nodeSet = new HashSet<>();
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			Node n = (Node)o;
			return n.name.equals(name);
		}

	}

	// Process child class
	private static class Process extends Node {
		private Process(String name) {
			super.name = name;
		}
	}

	// Resource child class
	private static class Resource extends Node {
		// Queue of processes that are waiting for this resource
		private Set<Process> linkedNodeSet;

		private Resource(String name) {
			linkedNodeSet = new LinkedHashSet<>();
			super.name = name;
		}
	}
}
