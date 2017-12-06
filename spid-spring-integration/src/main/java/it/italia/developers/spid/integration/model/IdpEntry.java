package it.italia.developers.spid.integration.model;

public class IdpEntry {

	private String identifier;
	private String entityId;
	private String name;
	private String imageUrl;

	public IdpEntry() {
	}

	public IdpEntry(final String identifier, final String entityId, final String name, String imageUrl) {
		this.identifier = identifier;
		this.entityId = entityId;
		this.name = name;
		this.imageUrl = imageUrl;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(final String entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
