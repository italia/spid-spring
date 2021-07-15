package it.italia.developers.spid.spidspringrest.model;

import java.util.List;

import it.italia.developers.spid.integration.model.IdpEntry;
import lombok.Data;

@Data
public class SpidProviders {

	private List<IdpEntry> identityProviders;
	private List<ExtraInfo> extraInfo;
}
