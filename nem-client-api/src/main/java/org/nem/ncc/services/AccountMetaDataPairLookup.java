package org.nem.ncc.services;

import org.nem.core.model.Address;
import org.nem.core.model.ncc.AccountMetaDataPair;
import org.nem.core.serialization.AccountLookup;

/**
 * An interface for looking up account metadata pairs.
 */
public interface AccountMetaDataPairLookup extends AccountLookup {

	/**
	 * Looks up a metadata pair by its id.
	 *
	 * @param id The account id.
	 * @return The account with the specified id.
	 */
	public AccountMetaDataPair findPairByAddress(final Address id);
}