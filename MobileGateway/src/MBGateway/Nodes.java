
package MBGateway;

public class Nodes {
	int nodeid;
	int to_be_updated;
	String type;
	int version;
	
	public Nodes() {
	}
	
	public Nodes(int id, int flag, String t, int ver){
		this.nodeid = id;
		this.to_be_updated = flag;
		this.type = t;
		this.version = ver;
	}
	
	public void set_nodeid(int id) {
		nodeid = id;
	}
	
	public void set_type(int flag) {
		to_be_updated = flag;
	}
	
	public void set_type(String newType) {
		type = newType;
	}
	
	public void set_version(int newVersion) {
		version = newVersion;
	}
	
	
}
