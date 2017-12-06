package it.italia.developers.spid.integration.model;

import java.util.Date;

public class ResponseDecoded {

	private String codiceIdentificativo;
	private String nome;
	private String cognome;

	private String luogoNascita;
	private String provinciaNascita;
	private Date dataNascita;

	private String sesso;
	private String ragioneSociale;

	private String indirizzoSedeLegale;
	private String codiceFiscale;
	private String partitaIva;

	private String documentoIdentita;
	private Date dataScadenzaIdentita;

	private String indirizzoDomicilio;
	private String numeroTelefono;
	private String emailAddress;
	private String emailPec;

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(final String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceIdentificativo() {
		return codiceIdentificativo;
	}

	public void setCodiceIdentificativo(final String codiceIdentificativo) {
		this.codiceIdentificativo = codiceIdentificativo;
	}

	public String getLuogoNascita() {
		return luogoNascita;
	}

	public void setLuogoNascita(final String luogoNascita) {
		this.luogoNascita = luogoNascita;
	}

	public String getProvinciaNascita() {
		return provinciaNascita;
	}

	public void setProvinciaNascita(final String provinciaNascita) {
		this.provinciaNascita = provinciaNascita;
	}

	public Date getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(final Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(final String sesso) {
		this.sesso = sesso;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(final String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getIndirizzoSedeLegale() {
		return indirizzoSedeLegale;
	}

	public void setIndirizzoSedeLegale(final String indirizzoSedeLegale) {
		this.indirizzoSedeLegale = indirizzoSedeLegale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(final String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(final String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getDocumentoIdentita() {
		return documentoIdentita;
	}

	public void setDocumentoIdentita(final String documentoIdentita) {
		this.documentoIdentita = documentoIdentita;
	}

	public Date getDataScadenzaIdentita() {
		return dataScadenzaIdentita;
	}

	public void setDataScadenzaIdentita(final Date dataScadenzaIdentita) {
		this.dataScadenzaIdentita = dataScadenzaIdentita;
	}

	public String getIndirizzoDomicilio() {
		return indirizzoDomicilio;
	}

	public void setIndirizzoDomicilio(final String indirizzoDomicilio) {
		this.indirizzoDomicilio = indirizzoDomicilio;
	}

	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(final String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailPec() {
		return emailPec;
	}

	public void setEmailPec(final String emailPec) {
		this.emailPec = emailPec;
	}

}
