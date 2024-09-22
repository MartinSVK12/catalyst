package sunsetsatellite.catalyst.multipart.api.impl.dragonfly;

public class NamespaceId {
	public static final String coreNamespaceId = "minecraft"; // This is also used as the default namespace if one is not provided
	private final String namespace;
	private final String id;
	public NamespaceId(String namespace, String id){
		this.namespace = namespace.toLowerCase();
		if (!namespace.equals(this.namespace)) throw new IllegalArgumentException("Namespaces must be lowercase! '" + namespace + "'");
		this.id = id.toLowerCase();
		if (!id.equals(this.id)) throw new IllegalArgumentException("ID must be lowercase! '" + id + "'");
	}
	public String getNamespace(){
		return namespace;
	}
	public String getId(){
		return id;
	}
	public String toString(){
		return (namespace + ":" + id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String){
			return this.toString().equals(((String) obj).toLowerCase());
		}
		if (obj instanceof NamespaceId){
			return this.namespace.equals(((NamespaceId) obj).namespace) && this.id.equals(((NamespaceId) obj).id);
		}
		return super.equals(obj);
	}
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	public static NamespaceId idFromString(String formattedString){
		String formattedStringLower = formattedString.toLowerCase();
		if (!formattedString.equals(formattedStringLower)) throw new IllegalArgumentException("All Namespaces and IDs must be lowercase! '" + formattedString + "'");
		String namespace = coreNamespaceId;
		String id;
		if (formattedStringLower.contains(":")){
			namespace = formattedStringLower.split(":")[0];
			id = formattedStringLower.split(":")[1];
		} else {
			id = formattedStringLower;
		}

		if (namespace.contains(":") || namespace.isEmpty()) throw new IllegalArgumentException("Namespace '" + namespace + "' is not formatted correctly!");
		if (id.contains(":") || id.isEmpty()) throw new IllegalArgumentException("Id '" + id + "' is not formatted correctly!");

		return new NamespaceId(namespace, id);
	}
}

